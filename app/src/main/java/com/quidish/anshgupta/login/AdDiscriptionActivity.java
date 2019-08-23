package com.quidish.anshgupta.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import com.quidish.anshgupta.login.Home.BottomNavifation.HomeFragment;
import com.quidish.anshgupta.login.LoginRegister.LoginSignupactivity;
import com.quidish.anshgupta.login.Messaging.MessageActivity;
import com.quidish.anshgupta.login.Messaging.MyChatActivity;
import com.quidish.anshgupta.login.MyAccount.MyWishlistActivity;
import com.quidish.anshgupta.login.MyAdsAndUserProfile.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class AdDiscriptionActivity extends AppCompatActivity implements  OnMapReadyCallback{

    TextView  adtitle,price,username,des,wishtext;
    TextView address,condition,camtext;
    String ad_no,clgid,mobileno;
    LinearLayout wish,chat;
    ImageView chatimg,wishimg;
    MyCustomPagerAdapter myCustomPagerAdapter;
    Double lat=28.7041,lng=77.1025;
    ViewPager mDemoSlider;
    Toolbar toolbar;
    List<String> images=new ArrayList<>();
    LinearLayout wishlist,message,call;
    FirebaseAuth firebaseAuth,myfba;
    FirebaseUser fuser;
    FirebaseDatabase firebaseDatabase;
    String wish_no="0";
    LinearLayout userp;
    DatabaseReference ref;
    String uname;
    String userid,soldno,uid;
    ImageView marker;
    private GoogleMap mMap;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_ad);

        adtitle=findViewById(R.id.adtitle);
        price=findViewById(R.id.price);
        marker=findViewById(R.id.marker);
        address=findViewById(R.id.address);
        des=findViewById(R.id.des);
        condition=findViewById(R.id.condition);
        wishlist=findViewById(R.id.wishlist);
        message=findViewById(R.id.message);
        wishtext=findViewById(R.id.wishtext);
        call=findViewById(R.id.call);
        camtext=findViewById(R.id.camtext);
        username=findViewById(R.id.username);
        wish=findViewById(R.id.wish);
        chat=findViewById(R.id.chat);
        chatimg=findViewById(R.id.chatimg);
        wishimg=findViewById(R.id.wishimg);
        userp=findViewById(R.id.userp);
        mDemoSlider =findViewById(R.id.slider);
        firebaseAuth = FirebaseAuth.getInstance();
        myfba= FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fuser = firebaseAuth.getCurrentUser();

        if(fuser!=null)
        userid=fuser.getUid();

        chatimg.setColorFilter(Color.argb(255, 255, 255, 255));
        wishimg.setColorFilter(Color.argb(255, 255, 255, 255));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.containsKey("msgad_no"))
            {
                ad_no=bundle.getString("msgad_no");

                String[] splited = ad_no.split(" ");

                clgid=splited[0];
                ad_no=splited[1];

                message.setVisibility(View.GONE);
                wishlist.setVisibility(View.GONE);
                addetailsset(ad_no);

            }

            else{

                ad_no=bundle.getString("ad_no");
                String[] splited = ad_no.split(" ");

                clgid=splited[0];
                ad_no=splited[1];

                if(HomeFragment.listwish.contains(clgid+" "+ad_no))
                {
                    wish_no="1";
                    wishtext.setText("WISHLISTED");
                }

                addetailsset(ad_no);

            }

        }


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();

        int serachnumber=pref.getInt("recentadnumber", 0);

        if(serachnumber>=6){

            for(int i=5;i>=1;i--){

                String srchitm="recentad"+(i);

                String reecentad_no=pref.getString(srchitm, null);

                srchitm="recentad"+(i+1);
                searchhint.putString(srchitm,reecentad_no);

                searchhint.putInt("recentadnumber",serachnumber+1);
                searchhint.apply();
            }

            String srchitm="recentad"+(1);
            searchhint.putString(srchitm,clgid+" "+ad_no);

            searchhint.putInt("recentadnumber",serachnumber+1);
            searchhint.apply();

        }

        else {
            String srchitm="recentad"+(serachnumber+1);
            searchhint.putString(srchitm,clgid+" "+ad_no);

            searchhint.putInt("recentadnumber",serachnumber+1);
            searchhint.apply();
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
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
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


        userp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(uid!=null){
                    Intent intent=new Intent(getApplicationContext(),UserProfile.class);
                    intent.putExtra("userid", uid);
                    startActivity(intent);
                }
            }
        });



        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AdDiscriptionActivity.this);
                builder.setMessage("Are you sure. You want to Call")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Intent.ACTION_CALL);
                                i.setData(Uri.parse("tel:" + mobileno));
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                startActivity(i);
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

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser==null)
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(),MessageActivity.class);
                    intent.putExtra("ad_no",clgid+" "+ad_no);
                    intent.putExtra("usertype","buyer");
                    intent.putExtra("act","1");
                    intent.putExtra("uid","0");
                    intent.putExtra("name",uname);
                    startActivity(intent);
                }
            }
        });



        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser==null)
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }

                else
                {
                    if(wish_no.equals("1")){
                        ref=FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("wish").child(clgid+" "+ad_no);

                        ref.setValue(null);
                        wishtext.setText("WISHLIST");
                        HomeFragment.listwish.remove(clgid+" "+ad_no);
                    }

                    else {

                        ref=FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("wish").child(clgid+" "+ad_no);
                        wishtext.setText("WISHLISTED");

                        HomeFragment.listwish.add(clgid+" "+ad_no);
                        ref.setValue(clgid+" "+ad_no);

                    }
                }

            }
        });


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

                myCustomPagerAdapter = new MyCustomPagerAdapter(AdDiscriptionActivity.this, images);
                mDemoSlider.setAdapter(myCustomPagerAdapter);
                TabLayout tabLayout = findViewById(R.id.tab_layout);
                tabLayout.setupWithViewPager(mDemoSlider, true);


                adtitle.setText(dataSnapshot.child("ad_title").getValue(String.class));
                price.setText(dataSnapshot.child("price").getValue(String.class));
                address.setText(dataSnapshot.child("Address_Complete").getValue(String.class));
                camtext.setText(dataSnapshot.child("Address_Nick").getValue(String.class).toUpperCase());

                des.setText(dataSnapshot.child("ad_details").getValue(String.class));

                condition.setText(dataSnapshot.child("condition").getValue(String.class).toUpperCase());

                username.setText(dataSnapshot.child("name").getValue(String.class));

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
                mobileno=dataSnapshot.child("mobile").getValue(String.class);

                uid = dataSnapshot.child("userid").getValue(String.class);
                soldno=dataSnapshot.child("sold").getValue(String.class);

                String shownumber=(dataSnapshot.child("shownumber").getValue(String.class));

                if(shownumber!=null && shownumber.equals("0"))
                {
                    call.setVisibility(View.GONE);
                }

                if(soldno!=null && soldno.equals("1"))
                {
                    call.setVisibility(View.GONE);
                    message.setVisibility(View.GONE);
                    wishlist.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}


