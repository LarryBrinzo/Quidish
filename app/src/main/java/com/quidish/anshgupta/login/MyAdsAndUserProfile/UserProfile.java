package com.quidish.anshgupta.login.MyAdsAndUserProfile;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserProfile extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    static String userid;
    List<AdModel> listactive=new ArrayList<>();
    List<String> userads=new ArrayList<>();
    RecyclerView recycle;
    UserAdsAdapter adsAdapter;
    TextView usern;
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

    usern=findViewById(R.id.usern);
    recycle =findViewById(R.id.recycle);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null)
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);


    adsAdapter = new UserAdsAdapter(listactive, UserProfile.this);
    RecyclerView.LayoutManager recyceAll = new GridLayoutManager(UserProfile.this,2);
    recycle.setLayoutManager(recyceAll);
    recyceAll.setAutoMeasureEnabled(false);
    recycle.setItemAnimator( new DefaultItemAnimator());
    recycle.setAdapter(adsAdapter);

    recycle.setNestedScrollingEnabled(false);

    listactive.clear();
    addproductad();

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
                isShow = true;
            } else if(isShow) {
                collapsingToolbarLayout.setTitle(" ");
                isShow = false;
            }
            if (scrollRange + verticalOffset != 0) {
            }
        }
    });



    }


    public void addproductad(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Posted Ad");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userads.clear();

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                    String product_no=dataSnapshot1.getValue(String.class);
                    userads.add(product_no);
                }

                useradsort();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void useradsort(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listactive.clear();

                for(int i=userads.size()-1;i>=0;i--) {

                    String[] splited = userads.get(i).split(" ");

                    String ad_no=splited[1];
                    String clgid=splited[0];

                    String adsold = dataSnapshot.child(clgid).child(ad_no).child("sold").getValue(String.class);

                    if(adsold==null)
                        continue;

                    String adtitle = dataSnapshot.child(clgid).child(ad_no).child("ad_title").getValue(String.class);
                    String price = dataSnapshot.child(clgid).child(ad_no).child("price").getValue(String.class);
                    String brand = dataSnapshot.child(clgid).child(ad_no).child("brand").getValue(String.class);
                    String image1 = dataSnapshot.child(clgid).child(ad_no).child("image").getValue(String.class);
                    String name = dataSnapshot.child(clgid).child(ad_no).child("name").getValue(String.class);

                    AdModel fire = new AdModel();

                    String[] splited2 = image1.split(" ");

                    fire.setPrice(price);
                    fire.setTitle(adtitle);
                    fire.setBrand(brand);
                    fire.setAdno(userads.get(i));
                    fire.setUrl1(splited2[0]);
                    fire.setName(name);
                    fire.setSold(adsold);

                    listactive.add(fire);

                    adsAdapter.notifyItemInserted(listactive.size() - 1);

                }

                adsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
    userid=null;
        onBackPressed();
        return true;
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

               // String user_inst = dataSnapshot.child("Users").child(userid).child("Institute").getValue(String.class);
                String user_name = dataSnapshot.child("Users").child(userid).child("Full_Name").getValue(String.class);

                usern.setText(user_name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

}
