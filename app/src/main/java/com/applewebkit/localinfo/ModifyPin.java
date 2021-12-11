package com.applewebkit.localinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyPin extends AppCompatActivity {
    String num;
    DBHelper dbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pin);
        this.setTitle("내용 수정하기");
        dbHelper = new DBHelper(getApplicationContext(), "data.db", null, 1);
        db = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        num = intent.getStringExtra("num");
        String sql = "SELECT title, letter FROM content WHERE num="+ num + ";";
        Cursor c = db.rawQuery(sql,null);
        c.moveToNext();
        String title = c.getString(c.getColumnIndexOrThrow("title"));
        String letter = c.getString(c.getColumnIndexOrThrow("letter"));
        TextView tit = findViewById(R.id.TitleForm);
        TextView le = findViewById(R.id.LetterForm);
        tit.setText(title);
        le.setText(letter);
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
            String sql = "UPDATE content set title = '" + title + "', letter = '" + letter + "' WHERE num = '" + num + "';";
            db.execSQL(sql);
            Toast.makeText(this.getApplicationContext(), "정상적으로 수정되었습니다.", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }
    }
    public void clickCancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }
}