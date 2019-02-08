package com.quidish.anshgupta.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyWishlistActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener  {

    String userid;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser fuser;
    FirebaseAuth myfba;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference,reference;
    List<FireModel> listhome=new ArrayList<>();
    List<FireModel> listwish=new ArrayList<>();
    RecyclerView recycle;
    LinearLayout empty;
    LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wishlist);

        checkConnection();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle =findViewById(R.id.recycle);
        progressBar=findViewById(R.id.prog2);
        back=findViewById(R.id.backbt);
        empty=findViewById(R.id.empty);
        userid=myfba.getCurrentUser().getUid();

        recycle.setNestedScrollingEnabled(false);

        if(listwish.size()==0)
            empty.setVisibility(View.VISIBLE);
        else
            empty.setVisibility(View.GONE);

        recycle.removeAllViewsInLayout();
        empty.setVisibility(View.GONE);

        progressBar.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        addproductad();
    }

    public void addproductad(){

        progressBar.setVisibility(View.VISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String ad_no = dataSnapshot.child("tad_no").getValue(String.class);
                int x = Integer.parseInt(ad_no);
                x--;

                listhome.clear();
                for(int i=1;i<=x;i++) {

                    ad_no = Integer.toString(i);

                    String adsold = dataSnapshot.child(ad_no).child("sold").getValue(String.class);
                    String usid = dataSnapshot.child(ad_no).child("userid").getValue(String.class);
                  //  String posted = dataSnapshot.child(ad_no).child("posted").getValue(String.class);

                    String adtitle = dataSnapshot.child(ad_no).child("ad_title").getValue(String.class);
                    String model = dataSnapshot.child(ad_no).child("model").getValue(String.class);
                    String include = dataSnapshot.child(ad_no).child("includes").getValue(String.class);
                    String year = dataSnapshot.child(ad_no).child("year").getValue(String.class);
                    String condition = dataSnapshot.child(ad_no).child("condition").getValue(String.class);
                    String addetails = dataSnapshot.child(ad_no).child("ad_details").getValue(String.class);
                    String price = dataSnapshot.child(ad_no).child("price").getValue(String.class);
                    String address = dataSnapshot.child(ad_no).child("address").getValue(String.class);
                    String brand = dataSnapshot.child(ad_no).child("brand").getValue(String.class);
                    String email = dataSnapshot.child(ad_no).child("email_id").getValue(String.class);
                    String mobile = dataSnapshot.child(ad_no).child("mobile").getValue(String.class);
                    String name = dataSnapshot.child(ad_no).child("name").getValue(String.class);
                    String image1 = dataSnapshot.child(ad_no).child("image1").getValue(String.class);
                    String image2 = dataSnapshot.child(ad_no).child("image2").getValue(String.class);
                    String image3 = dataSnapshot.child(ad_no).child("image3").getValue(String.class);
                    String image4 = dataSnapshot.child(ad_no).child("image4").getValue(String.class);

                    price = "₹ " + price;

                    FireModel fire = new FireModel();

                    fire.setPrice(price);
                    fire.setTitle(adtitle);
                    fire.setModel(model);
                    fire.setIncludes(include);
                    fire.setYear(year);
                    fire.setCondition(condition);
                    fire.setDetails(addetails);
                    fire.setAddress(address);
                    fire.setBrand(brand);
                    fire.setEmail(email);
                    fire.setMobile(mobile);
                    fire.setName(name);
                    fire.setUrl1(image1);
                    fire.setUrl2(image2);
                    fire.setUrl3(image3);
                    fire.setUrl4(image4);
                    fire.setAdno(ad_no);
                    fire.setActivity("1");
                    fire.setSold(adsold);
                    fire.setUser(usid);

                    listhome.add(fire);

                }

                userwish();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void userwish(){

        progressBar.setVisibility(View.VISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("wish");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listwish.clear();

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                    String advalue=dataSnapshot1.getValue(String.class);
                    int x = Integer.parseInt(advalue);
                    x--;

                    FireModel ff=listhome.get(x);

                        ff.setWish("1");
                        listwish.add(ff);
                }



                progressBar.setVisibility(View.GONE);
                 AdshowWish();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void AdshowWish(){

        recycle.removeAllViewsInLayout();

        if(listwish.size()==0)
            empty.setVisibility(View.VISIBLE);

        else {
            empty.setVisibility(View.GONE);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(listwish,MyWishlistActivity.this);
        RecyclerView.LayoutManager recyce = new GridLayoutManager(MyWishlistActivity.this,1);
        recycle.setLayoutManager(recyce);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);

        progressBar.setVisibility(View.GONE);}
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
