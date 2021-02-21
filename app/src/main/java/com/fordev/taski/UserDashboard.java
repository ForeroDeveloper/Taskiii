package com.fordev.taski;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class UserDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dashboard);
    }

    public void LoginClick(View view) {
        Intent intent = new Intent(UserDashboard.this, LoginScreen.class);
        startActivity(intent);

    }

    public void SignUpClick(View view) {
    }
}