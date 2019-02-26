package com.quidish.anshgupta.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.Home.HomeActivity;
import com.quidish.anshgupta.login.Home.Searching.SuggestionAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.max;

public class SpashScreenActivity extends AppCompatActivity {

    public static Set<String> searchdict = new HashSet<>();
    SearchView search;
    DatabaseReference databaseReference;
    NestedScrollView scrollView;
    ProgressBar progbar;
    public static long adcount=0,adstart,adend;
    FirebaseUser fuser;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);

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

    public void searchSuggestions(){

        final DatabaseReference sgreference = FirebaseDatabase.getInstance().getReference().child("Ads");

        sgreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(long i=adstart;i>=1;i--) {

                        String ad_no;

                        ad_no=Long.toString(i);

                        String adtitle = dataSnapshot.child(ad_no).child("ad_title").getValue(String.class);
                        assert adtitle != null;
                        adtitle=adtitle.trim();
                        String adtitle2=adtitle.toLowerCase();
                        searchdict.add(adtitle2);

                        String model = dataSnapshot.child(ad_no).child("model").getValue(String.class);
                        assert model != null;
                        model=model.trim();
                        model=model.toLowerCase();
                        searchdict.add(model);

                        String category = dataSnapshot.child(ad_no).child("category").getValue(String.class);
                        assert category != null;
                        category=category.trim();
                        category=category.toLowerCase();
                        searchdict.add(category);

                        String sub_category = dataSnapshot.child(ad_no).child("sub_category").getValue(String.class);
                        assert sub_category != null;
                        sub_category=sub_category.trim();
                        sub_category=sub_category.toLowerCase();
                        searchdict.add(sub_category);

                        String brand = dataSnapshot.child(ad_no).child("brand").getValue(String.class);
                        assert brand != null;
                        brand=brand.trim();
                        String brand2=brand.toLowerCase();
                        searchdict.add(brand2);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intent);
        finish();

    }

}
