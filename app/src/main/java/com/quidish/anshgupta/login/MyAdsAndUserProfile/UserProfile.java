package com.quidish.anshgupta.login.MyAdsAndUserProfile;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;

import java.util.Random;

public class UserProfile extends AppCompatActivity implements activefrag.OnFragmentInteractionListener,inactivefrag.OnFragmentInteractionListener,ConnectivityReceiver.ConnectivityReceiverListener {

    static String userid;
    LinearLayout back;
    ImageView img1,img2;
    TextView profile,usern,inst;
    DatabaseReference databaseReference;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        checkConnection();

    Bundle bundle2 = getIntent().getExtras();
    if (bundle2 != null) {
        userid=bundle2.getString("userid");

        if(userid!=null)
        linkaccount();
    }

    back=findViewById(R.id.backbt);
    profile=findViewById(R.id.profile_image);
    usern=findViewById(R.id.usern);
    inst=findViewById(R.id.inst);
    img2=findViewById(R.id.img2);
    img1=findViewById(R.id.img1);

    Random rand = new Random();
    int x=rand.nextInt(4);

    if(x==0)
        profile.setBackgroundResource(R.drawable.cir_borderimg);
    else if(x==1)
        profile.setBackgroundResource(R.drawable.cir_borderimg2);
    else if(x==2)
        profile.setBackgroundResource(R.drawable.cir_borderimg3);
    else if(x==3)
        profile.setBackgroundResource(R.drawable.cir_borderimg4);

    back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    });

    final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_container);
    collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD);

    AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
        boolean isShow = true;
        int scrollRange = -1;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (scrollRange == -1) {
                scrollRange = appBarLayout.getTotalScrollRange();
            }
            if (scrollRange + verticalOffset == 0) {
                collapsingToolbarLayout.setTitle(usern.getText().toString());
                img2.setVisibility(View.VISIBLE);
                img1.setVisibility(View.GONE);
                isShow = true;
            } else if(isShow) {
                collapsingToolbarLayout.setTitle(" ");
                isShow = false;
            }
            if (scrollRange + verticalOffset != 0) {
                img2.setVisibility(View.GONE);
                img1.setVisibility(View.VISIBLE);
            }
        }
    });

        ViewPager viewPager = findViewById(R.id.pager);
        MyPagerAdapter2 myPagerAdapter = new MyPagerAdapter2(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    public static String GetData()
    {
        return userid;
    }

@Override
public void onBackPressed() {
        userid=null;
        super.onBackPressed();
        }

private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
        }

@Override
protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
        }

@Override
public void onNetworkConnectionChanged(boolean isConnected) {
        showDialog(isConnected);
        }

    private void showDialog(boolean isConnected)
    {
        if (!isConnected) {

            Intent intent=new Intent(getApplicationContext(),No_InternetActivity.class);
            startActivity(intent);


        }
    }
    public void linkaccount() {

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(userid==null)
                    return;

                String user_inst = dataSnapshot.child("users").child(userid).child("institute").getValue(String.class);
                String user_name = dataSnapshot.child("users").child(userid).child("username").getValue(String.class);

                usern.setText(user_name);
                inst.setText(user_inst);

                if(user_name!=null)
                profile.setText(user_name.substring(0,1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

@Override
public void onFragmentInteraction(Uri uri) {

        }
}
