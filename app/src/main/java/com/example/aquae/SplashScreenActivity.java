package com.example.aquae;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(new Session(getApplicationContext()).checkSession()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        else {
            ImageView logo = findViewById(R.id.logo);

            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish();
            }, 3000);

            logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splashscreenanimation));
        }
    }
}
