package com.applewebkit.localinfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.location.Location;
import android.location.LocationManager;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener  {
    LocationManager lm = null;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1000;
    MapView mapView;
    DBHelper dbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionCheck == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        dbHelper = new DBHelper(getApplicationContext(), "data.db", null, 1);
        db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
    }
    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode != -1){
            switch(requestCode){
                case REQUEST_ACCESS_FINE_LOCATION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        getLocation();
                    }
                    else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                refreshPOI();
            }
        }
    }
    protected void getLocation(){
        Location location = null;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), true);
        }
    }
    public void refreshPOI(){
        mapView.removeAllPOIItems();
        String sql = "SELECT num, latitude, longitude FROM content;";
        Cursor c = db.rawQuery(sql, null);
        while(c.moveToNext()){
            double latitude = c.getDouble(c.getColumnIndexOrThrow("latitude"));
            double longitude = c.getDouble(c.getColumnIndexOrThrow("longitude"));
            MapPoint point = MapPoint.mapPointWithGeoCoord(latitude, longitude);
            MapPOIItem marker = new MapPOIItem();
            marker.setTag(0);
            marker.setMapPoint(point);
            marker.setItemName(Integer.toString(c.getInt(c.getColumnIndexOrThrow("num"))));
            marker.setMarkerType(MapPOIItem.MarkerType.RedPin);
            mapView.addPOIItem(marker);
        }
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        refreshPOI();
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        refreshPOI();
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate gps = mapPoint.getMapPointGeoCoord();
        Intent intent = new Intent(getApplicationContext(), AddPin.class);
        intent.putExtra("lat", gps.latitude);
        intent.putExtra("lon", gps.longitude);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        String num = mapPOIItem.getItemName();
        String sql = "SELECT title, letter FROM content WHERE num="+ num + ";";
        Cursor c = db.rawQuery(sql,null);
        c.moveToNext();
        Intent intent = new Intent(getApplicationContext(), ViewPin.class);
        intent.putExtra("title", c.getString(c.getColumnIndexOrThrow("title")));
        intent.putExtra("letter", c.getString(c.getColumnIndexOrThrow("letter")));
        intent.putExtra("num", num);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}