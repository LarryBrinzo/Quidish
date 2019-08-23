package com.quidish.anshgupta.login.MyAdsAndUserProfile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.PostYourAd.PostAd.SelectPictureActivity;
import com.quidish.anshgupta.login.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdsActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    LinearLayout back;
    FirebaseAuth myfba;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser fuser;
    String userid;
    DatabaseReference databaseReference;
    List<AdModel> listactive=new ArrayList<>();
    List<String> userads=new ArrayList<>();
    RecyclerView recycle;
    LinearLayout empty;
    MyActiveAdsAdapter adsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);
        checkConnection();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle =findViewById(R.id.recycle);
        empty=findViewById(R.id.empty);

        adsAdapter = new MyActiveAdsAdapter(listactive, MyAdsActivity.this);
        RecyclerView.LayoutManager recyceAll = new GridLayoutManager(MyAdsActivity.this,1);
        recycle.setLayoutManager(recyceAll);
        recyceAll.setAutoMeasureEnabled(false);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(adsAdapter);

        recycle.setNestedScrollingEnabled(false);

        userid=myfba.getCurrentUser().getUid();

        listactive.clear();
        addproductad();

        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

                    String ad_no = splited[1];
                    String clgid=splited[0];

                    String adsold = dataSnapshot.child(clgid).child(ad_no).child("sold").getValue(String.class);

                    if(adsold==null)
                        continue;

                    String adtitle = dataSnapshot.child(clgid).child(ad_no).child("ad_title").getValue(String.class);
                    String price = dataSnapshot.child(clgid).child(ad_no).child("price").getValue(String.class);
                    String brand = dataSnapshot.child(clgid).child(ad_no).child("brand").getValue(String.class);
                    String image1 = dataSnapshot.child(clgid).child(ad_no).child("image").getValue(String.class);
                    String name = dataSnapshot.child(clgid).child(ad_no).child("name").getValue(String.class);

                    String[] splited2 = image1.split(" ");

                    AdModel fire = new AdModel();

                    fire.setPrice(price);
                    fire.setTitle(adtitle);
                    fire.setBrand(brand);
                    fire.setAdno(userads.get(i));
                    fire.setUrl1(splited2[0]);
                    fire.setName(name);
                    fire.setSold(adsold);
                    fire.setCondition(dataSnapshot.child(clgid).child(ad_no).child("condition").getValue(String.class));

                    listactive.add(fire);

                    adsAdapter.notifyItemInserted(listactive.size() - 1);

                }

                adsAdapter.notifyDataSetChanged();

                if(userads.isEmpty())
                    empty.setVisibility(View.VISIBLE);
                else
                    empty.setVisibility(View.GONE);
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


}
