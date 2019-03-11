package com.octagrimme.brow.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.octagrimme.brow.R;

import android.content.SharedPreferences;
import android.os.Bundle;

public class sms1 extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms1);

        sharedPreferences = getSharedPreferences("phoneNum", MODE_PRIVATE);

    }
}
