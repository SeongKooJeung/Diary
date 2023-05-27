package com.example.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddDiaryActivity extends AppCompatActivity {

    private EditText editTextDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        editTextDiary = findViewById(R.id.editTextDiary);

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 작성한 일기를 MainActivity로 반환
                String diaryContent = editTextDiary.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("diary", diaryContent);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
