package com.quidish.anshgupta.login.MyAdsAndUserProfile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.Home.BottomNavifation.BottomNavigationDrawerActivity;
import com.quidish.anshgupta.login.LoginRegister.LoginSignupactivity;
import com.quidish.anshgupta.login.Messaging.MyChatActivity;
import com.quidish.anshgupta.login.MyAccount.MyWishlistActivity;
import com.quidish.anshgupta.login.MyCustomPagerAdapter;
import com.quidish.anshgupta.login.PostYourAd.PostAd.AdConfirmationActivity;
import com.quidish.anshgupta.login.PostYourAd.PostAd.PhoneVerificationActivity;
import com.quidish.anshgupta.login.PostYourAd.PostAd.SavedAddressAdapter;
import com.quidish.anshgupta.login.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdDiscriptionActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView  adtitle,price,des;
    TextView address,condition,camtext;
    String ad_no,clgid;
    LinearLayout wish,chat;
    ImageView chatimg,wishimg;
    MyCustomPagerAdapter myCustomPagerAdapter;
    Double lat=28.7041,lng=77.1025;
    ViewPager mDemoSlider;
    Toolbar toolbar;
    List<String> images=new ArrayList<>();
    LinearLayout edit,sold;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth,myfba;
    FirebaseUser fuser;
    FirebaseDatabase firebaseDatabase;
    LinearLayout userp;
    String uname;
    String conf="0",soldno,uid;
    ImageView marker;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads2);

        adtitle=findViewById(R.id.adtitle);
        price=findViewById(R.id.price);
        marker=findViewById(R.id.marker);
        address=findViewById(R.id.address);
        des=findViewById(R.id.des);
        condition=findViewById(R.id.condition);
        sold=findViewById(R.id.sold);
        edit=findViewById(R.id.edit);
        camtext=findViewById(R.id.camtext);
        wish=findViewById(R.id.wish);
        chat=findViewById(R.id.chat);
        chatimg=findViewById(R.id.chatimg);
        wishimg=findViewById(R.id.wishimg);
        userp=findViewById(R.id.userp);
        mDemoSlider =findViewById(R.id.slider);
        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);
        firebaseAuth = FirebaseAuth.getInstance();
        myfba= FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fuser = firebaseAuth.getCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        chatimg.setColorFilter(Color.argb(255, 255, 255, 255));
        wishimg.setColorFilter(Color.argb(255, 255, 255, 255));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

                ad_no=bundle.getString("ad_no");
                String[] splited = ad_no.split(" ");

                clgid=splited[0];
                ad_no=splited[1];

                addetailsset(ad_no);

            if (bundle.containsKey("confirmation"))
                conf="1";

        }


        wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser!=null)
                {
                    Intent intent=new Intent(getApplicationContext(), MyWishlistActivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(), LoginSignupactivity.class);
                    startActivity(intent);
                }
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser!=null)
                {
                    Intent intent=new Intent(getApplicationContext(), MyChatActivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setTitleTextColor(Color.BLACK);

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
                    // collapsingToolbarLayout.setTitle(adtitle.getText().toString());
                    isShow = true;

                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

                    chatimg.setColorFilter(Color.argb(255, 26, 40, 53));
                    wishimg.setColorFilter(Color.argb(255, 26, 40, 53));
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;

                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


                    chatimg.setColorFilter(Color.argb(255, 255, 255, 255));
                    wishimg.setColorFilter(Color.argb(255, 255, 255, 255));
                }
            }
        });



        sold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyAdDiscriptionActivity.this);
                builder.setMessage("Are you sure. You want to Delete this Ad")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                itemsold();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),EditAdActivity.class);
                intent.putExtra("ad_no", ad_no);
                startActivity(intent);

            }
        });

    }



    public void itemsold(){
        DatabaseReference current_user;

        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(clgid).child(ad_no).child("sold");
        current_user.setValue("1");

        finish();
    }

    public void addetailsset(String ad_no) {

        DatabaseReference refer;
        refer=firebaseDatabase.getReference().child("Ads").child(clgid).child(ad_no);
        refer.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String brand=(dataSnapshot.child("brand").getValue(String.class));
                String name=(dataSnapshot.child("ad_title").getValue(String.class));

                SpannableString ss1=  new SpannableString(brand);
                ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, ss1.length(), 0);

                ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                Spannable wordTwo = new SpannableString(name);

                wordTwo.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                adtitle.append(ss1);
                adtitle.append(" ");
                adtitle.append(wordTwo);

                String image=(dataSnapshot.child("image").getValue(String.class));
                String[] splited = image.split(" ");

                for(int i=0;i<splited.length;i++)
                    images.add(splited[i]);

                myCustomPagerAdapter = new MyCustomPagerAdapter(MyAdDiscriptionActivity.this, images);
                mDemoSlider.setAdapter(myCustomPagerAdapter);
                TabLayout tabLayout = findViewById(R.id.tab_layout);
                tabLayout.setupWithViewPager(mDemoSlider, true);


                adtitle.setText(dataSnapshot.child("ad_title").getValue(String.class));
                price.setText(dataSnapshot.child("price").getValue(String.class));
                address.setText(dataSnapshot.child("Address_Complete").getValue(String.class));
                camtext.setText(dataSnapshot.child("Address_Nick").getValue(String.class).toUpperCase());

                des.setText(dataSnapshot.child("ad_details").getValue(String.class));

                condition.setText(dataSnapshot.child("condition").getValue(String.class).toUpperCase());

                lat=Double.parseDouble(dataSnapshot.child("Address_Lat").getValue(String.class));
                lng=Double.parseDouble(dataSnapshot.child("Address_Lng").getValue(String.class));

                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(lat,
                                lng));
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(18);

                mMap.moveCamera(center);
                mMap.animateCamera(zoom);

                addCustomMarker();

                uname=dataSnapshot.child("name").getValue(String.class);

                uid = dataSnapshot.child("userid").getValue(String.class);
                soldno=dataSnapshot.child("sold").getValue(String.class);

                String shownumber=(dataSnapshot.child("shownumber").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }

    private void addCustomMarker() {

        if (mMap == null) {
            return;
        }

        // adding a marker on map with image from  drawable
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,
                        lng))
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.marker)));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(28.7041,
                        77.1025));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(18);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        mMap.getUiSettings().setZoomControlsEnabled(true);

    }



    @Override
    public void onBackPressed() {
        if(conf.equals("0"))
        super.onBackPressed();

        else {
            Intent intent = new Intent(MyAdDiscriptionActivity.this, BottomNavigationDrawerActivity.class);
            startActivity(intent);
            finishAffinity();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
