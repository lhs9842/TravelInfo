package com.applewebkit.localinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewPin extends AppCompatActivity {
    String num;
    DBHelper dbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pin);
        this.setTitle("조회한 내용 보기");
        dbHelper = new DBHelper(getApplicationContext(), "data.db", null, 1);
        db = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String letter = intent.getStringExtra("letter");
        num = intent.getStringExtra("num");
        TextView TitleForm = findViewById(R.id.TitleView);
        TextView LetterForm = findViewById(R.id.LetterView);
        TitleForm.setText(title);
        LetterForm.setText(letter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public void ClickDelete(View view){
        db.delete("content", "num=?", new String[] {num});
        Toast.makeText(this.getApplicationContext(), "정상적으로 삭제되었습니다.", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }
    public void ClickModify(View view){
        Intent intent = new Intent(getApplicationContext(), ModifyPin.class);
        intent.putExtra("num", num);
        startActivityForResult(intent, 1);
    }
}