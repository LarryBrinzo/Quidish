package com.quidish.anshgupta.login.Home.BottomNavigation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.quidish.anshgupta.login.PostYourAd.PostAd.SelectPictureActivity;
import com.quidish.anshgupta.login.R;

public class BottomNavigationDrawerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    LinearLayout sell;
    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new WishFragment();
    final Fragment fragment3 = new MyAdsFragment();
    Fragment fragment4;
    final FragmentManager fm = getSupportFragmentManager();
    public static BottomNavigationView navigation;
    Fragment active = fragment1;
    FirebaseAuth myfba;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_drawer);

        sell=findViewById(R.id.sell);
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();

        if(fuser!=null){
            fragment4 = new ProfileFragment();
        }
        else
            fragment4 = new LoginFragment();

        navigation = findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(this);

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectPictureActivity.Selectedimagesno.clear();
                SelectPictureActivity.Selectedpos.clear();
                SelectPictureActivity.SelectedFilepath.clear();
                SelectPictureActivity.Allimages.clear();
                SelectPictureActivity.imageno=1;

                Intent intent=new Intent(getApplicationContext(),SelectPictureActivity.class);
                intent.putExtra("start","1");
                startActivity(intent);

            }
        });

        fm.beginTransaction().add(R.id.fragment_container, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragment_container,fragment1, "1").commit();

       // loadFragment(new HomeFragment());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                return true;

            case R.id.navigation_myads:
                fm.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                return true;

            case R.id.navigation_wish:
                fm.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                return true;

            case R.id.navigation_profile:
                fm.beginTransaction().hide(active).show(fragment4).commit();
                active = fragment4;
                return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {

        if (navigation.getSelectedItemId() == R.id.navigation_home)
        {
            super.onBackPressed();
            finish();
        }
        else
        {
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }

}
