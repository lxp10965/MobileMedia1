package com.xpl.mobilemedia1.activity;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.xpl.mobilemedia.R;


public class WelcomeActivity extends Activity {

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();

            }
        },2000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        return super.onTouchEvent(event);
    }

    public void startMainActivity(){
        Intent intent =new Intent(WelcomeActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}