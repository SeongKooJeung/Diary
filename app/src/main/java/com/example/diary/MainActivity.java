package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_DIARY = 1;
    private static final int REQUEST_CODE_RECORD_AUDIO_PERMISSION = 2;
    private TextView textViewDiaryContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewDiaryContent = findViewById(R.id.textViewDiaryContent);

        Button buttonAddDiary = findViewById(R.id.buttonAddDiary);
        buttonAddDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새로운 일기 작성 화면으로 이동
                Intent intent = new Intent(MainActivity.this, AddDiaryActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_DIARY);
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonVoiceToText = findViewById(R.id.buttonVoiceToText);
        buttonVoiceToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 음성 인식 시작
                startSpeechToText();
            }
        });

        // 오디오 녹음 권한 요청
        requestRecordAudioPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_DIARY && resultCode == RESULT_OK) {
            // 작성한 일기를 받아와서 텍스트뷰에 표시
            String newDiary = data.getStringExtra("diary");

            // 현재 날짜와 시간을 가져와서 표시
            String currentDate = getCurrentDateTime();
            String diaryWithDateTime = currentDate + "\n\n" + newDiary;

            // 기존의 일기 내용과 추가된 일기를 합쳐서 표시
            String existingDiary = textViewDiaryContent.getText().toString();
            String combinedDiary = existingDiary + "\n\n" + diaryWithDateTime;

            textViewDiaryContent.setText(combinedDiary);
        } else if (requestCode == REQUEST_CODE_RECORD_AUDIO_PERMISSION && resultCode == RESULT_OK) {
            // 음성 인식 결과 처리
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String voiceText = matches.get(0);

                // 현재 날짜와 시간을 가져와서 표시
                String currentDate = getCurrentDateTime();
                String diaryWithDateTime = currentDate + "\n\n" + voiceText;

                // 기존의 일기 내용과 추가된 일기를 합쳐서 표시
                String existingDiary = textViewDiaryContent.getText().toString();
                String combinedDiary = existingDiary + "\n\n" + diaryWithDateTime;

                textViewDiaryContent.setText(combinedDiary);
            }
        }
    }


    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    private void requestRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE_RECORD_AUDIO_PERMISSION);
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "듣고 있는 중.");
        startActivityForResult(intent, REQUEST_CODE_RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 오디오 녹음 권한이 허용된 경우, 음성 인식을 시작
                startSpeechToText();
            } else {
                // 오디오 녹음 권한이 거부된 경우, 사용자에게 알림을 표시하거나 다른 처리를 수행할 수 있습니다.
                textViewDiaryContent.setText("오디오 녹음 권한이 거부되었습니다.");
            }
        }
    }
}
