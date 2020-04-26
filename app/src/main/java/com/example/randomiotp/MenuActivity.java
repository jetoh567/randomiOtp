package com.example.randomiotp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private Button btnOtp;
    private Button btnForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnOtp = findViewById(R.id.btnOtp);
        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),KISA_SHA256.class);
                startActivity(intent);
            }
        });

        btnForm = findViewById(R.id.btnForm);
        btnForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FormActivity.class); //폼생성으로 수정
                startActivity(intent);
            }
        });
    }
}
