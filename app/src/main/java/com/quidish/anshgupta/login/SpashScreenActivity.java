package com.quidish.anshgupta.login;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.quidish.anshgupta.login.Home.BottomNavifation.BottomNavigationDrawerActivity;

public class SpashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);



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
