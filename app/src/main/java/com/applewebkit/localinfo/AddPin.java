package com.applewebkit.localinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import android.widget.Toast;

public class AddPin extends AppCompatActivity {
    double lat, lon;
    DBHelper dbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pin);
        this.setTitle("정보 추가하기");
        dbHelper = new DBHelper(getApplicationContext(), "data.db", null, 1);
        db = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", -1);
        lon = intent.getDoubleExtra("lon", -1);
        if(lat == -1 || lat == -1) finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( event.getAction() == MotionEvent.ACTION_OUTSIDE ) {
            return false;
        }
        return true;
    }
    public void clickSave(View view){
        TextView tit = findViewById(R.id.TitleForm);
        TextView le = findViewById(R.id.LetterForm);
        String title = tit.getText().toString();
        String letter = le.getText().toString();
        if(title.equals("") || letter.equals("")) {
            Toast.makeText(this.getApplicationContext(), "제목과 본문을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        else{
            String sql = "INSERT INTO content(latitude, longitude, title, letter) VALUES(" + lat + ", " + lon + ", '" + title + "', '" + letter + "');";
            db.execSQL(sql);
            Toast.makeText(this.getApplicationContext(), "정상적으로 게시되었습니다.", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }
    }
    public void clickCancel(View view){
        setResult(RESULT_OK);
        finish();
    }
}