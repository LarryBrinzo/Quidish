package com.quidish.anshgupta.login;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Pair;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.Home.HomeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;


import static java.lang.Math.max;

public class SpashScreenActivity extends AppCompatActivity {

    public static Set<String> searchdict = new HashSet<>();
    SearchView search;
    DatabaseReference databaseReference;
    NestedScrollView scrollView;
    ProgressBar progbar;
    public static long adcount=0,adstart,adend;
    public static LinkedHashMap<String,String> userads=new LinkedHashMap<>();
    ValueEventListener eventListener;
    String userid;
    FirebaseAuth firebaseAuth;
    FirebaseUser fuser;
    DatabaseReference sgreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        fuser = firebaseAuth.getCurrentUser();

        if(fuser!=null){
            userid=firebaseAuth.getCurrentUser().getUid();

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("recent");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                    userads.clear();

                    for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()) {

                        String key;
                        key=dataSnapshot1.getKey();

                        userads.put(dataSnapshot1.getValue(String.class),key);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4500);

        searchdict.add("cycle");
        searchdict.add("book");
        searchdict.add("electronics");
        searchdict.add("mobile");
        searchdict.add("gear cycle");
        searchdict.add("cycle gear");
        searchdict.add("non-gear cycle");
        searchdict.add("cycle non-gear");
        searchdict.add("text book");
        searchdict.add("tablets");
        searchdict.add("mobile phone");
        searchdict.add("accessories");
        searchdict.add("laptop");
        searchdict.add("appliances");
        searchdict.add("computer peripherals");
        searchdict.add("peripherals computer");
        searchdict.add("speaker");
        searchdict.add("camera");
        searchdict.add("camera accessories");
        searchdict.add("accessories camera");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                adcount=dataSnapshot.getChildrenCount();
                adstart=adcount-1;
                adend=max(adstart-29,1);

                searchSuggestions();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
//        Thread myThread = new Thread(){
//            @Override
//            public void run() {
//                try {
//                    sleep(1800);
//                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//                    startActivity(intent);
//                    finish();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        myThread.start();
    }

    public void searchSuggestions() {

        sgreference = FirebaseDatabase.getInstance().getReference().child("Ads");

        eventListener=sgreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(long i=adstart;i>=1;i--) {

                        String ad_no;

                        ad_no=Long.toString(i);

                        String verfied = dataSnapshot.child(ad_no).child("verified").getValue(String.class);
                        if(verfied!=null && verfied.equals("1"))
                            continue;

                        String model = dataSnapshot.child(ad_no).child("model").getValue(String.class);
                        assert model != null;
                        model=model.trim();
                        model=model.toLowerCase();
                        searchdict.add(model);

                        String brand = dataSnapshot.child(ad_no).child("brand").getValue(String.class);
                        assert brand != null;
                        brand=brand.trim();
                        String brand2=brand.toLowerCase();
                        searchdict.add(brand2);

                        searchdict.add(brand2+" "+model);
                        searchdict.add(model+" "+brand2);

                    String adtitle = dataSnapshot.child(ad_no).child("ad_title").getValue(String.class);
                    assert adtitle != null;
                    adtitle=adtitle.trim();
                    String adtitle2=adtitle.toLowerCase();

                    String[] splited = adtitle2.split(" ");

                    for (String aSplited : splited) {

                        if (aSplited.length() > 2 && !model.contains(aSplited) && !brand.contains(aSplited) && !aSplited.contains("colour") && !aSplited.contains("color")) {
                            searchdict.add(aSplited + " " + brand2 + " " + model);
                            searchdict.add(aSplited + " " + model + " " + brand2);
                            searchdict.add(brand2 + " " + model + " " + aSplited);
                            searchdict.add(model + " " + brand2 + " " + aSplited);
                            searchdict.add(brand2 + " " + aSplited);
                            searchdict.add(model + " " + aSplited);
                            searchdict.add(aSplited + " " + brand2);
                            searchdict.add(aSplited + " " + model);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

}
