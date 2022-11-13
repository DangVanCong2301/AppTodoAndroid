package com.example.todolistapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.todolistapp.R;

public class MainActivity extends AppCompatActivity {

    private Animation topAnim, bottomAnim;
    private ImageView imgLogo;
    private TextView tvAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initUi();
        initAnimation();
        setAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }

    private void initUi() {
        imgLogo = findViewById(R.id.img_logo);
        tvAppName = findViewById(R.id.tv_app_name);

    }

    private void initAnimation() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
    }

    private void setAnimation() {
        imgLogo.setAnimation(topAnim);
        tvAppName.setAnimation(bottomAnim);
    }
}