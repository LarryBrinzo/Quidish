package com.quidish.anshgupta.login;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.quidish.anshgupta.login.Home.BottomNavigation.BottomNavigationDrawerActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), BottomNavigationDrawerActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4500);

    }

}
