package com.quidish.anshgupta.login.PostYourAd.PostAd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.quidish.anshgupta.login.Home.BottomNavifation.BottomNavigationDrawerActivity;
import com.quidish.anshgupta.login.MyAdsAndUserProfile.MyAdDiscriptionActivity;
import com.quidish.anshgupta.login.R;

public class AdConfirmationActivity extends AppCompatActivity {

    LinearLayout back,preview;
    String ad_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_confirmation);

        back=findViewById(R.id.backbt);
        preview=findViewById(R.id.preview);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            ad_no=bundle.getString("ad_no");

        }

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdConfirmationActivity.this, MyAdDiscriptionActivity.class);
                intent.putExtra("ad_no", ad_no);
                intent.putExtra("confirmation", "1");
                startActivity(intent);
                finishAffinity();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(), BottomNavigationDrawerActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
