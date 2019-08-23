package com.quidish.anshgupta.login.MyAccount;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.quidish.anshgupta.login.R;

import java.util.ArrayList;
import java.util.List;

public class MyWishlistActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    String userid;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser fuser;
    FirebaseAuth myfba;
    ProgressBar progressBar;
    WishlistAdapter wishlistAdapter;
    List<AdModel> wishlist=new ArrayList<>();
    List<String> listwish=new ArrayList<>();
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

        wishlistAdapter = new WishlistAdapter(wishlist,MyWishlistActivity.this);
        RecyclerView.LayoutManager recyceAll = new GridLayoutManager(MyWishlistActivity.this,2);
        recycle.setLayoutManager(recyceAll);
        recyceAll.setAutoMeasureEnabled(false);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(wishlistAdapter);

        recycle.removeAllViewsInLayout();
        empty.setVisibility(View.GONE);

        progressBar.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        userwish();
    }

    public void addproductad(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                wishlist.clear();

                for(int i=listwish.size()-1;i>=0;i--) {

                    String[] splited = listwish.get(i).split(" ");

                    String ad_no=splited[1];
                    String clgid=splited[0];

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
                    fire.setAdno(listwish.get(i));
                    fire.setUrl1(splited2[0]);
                    fire.setName(name);

                    wishlist.add(fire);

                    wishlistAdapter.notifyItemInserted(wishlist.size() - 1);

                }

                wishlistAdapter.notifyDataSetChanged();

                if(listwish.isEmpty())
                    empty.setVisibility(View.VISIBLE);
                else
                    empty.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    public void userwish(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("wish");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listwish.clear();

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                    String product_no=dataSnapshot1.getValue(String.class);
                    listwish.add(product_no);
                }

                addproductad();

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
