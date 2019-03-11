package com.octagrimme.brow.activities;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.octagrimme.brow.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final ConstraintLayout constraintLayout = findViewById(R.id.splashScreen);
        Animation splash = AnimationUtils.loadAnimation(this, R.anim.splash_animation);

        constraintLayout.startAnimation(splash);
        int SPLASH_TIME_OUT = 1000;
        new android.os.Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            startActivity(new android.content.Intent(SplashActivity.this, LoginActivity.class));

            // close this activity
            // Following the documentation, right after starting the activity
            // we override the transition
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
