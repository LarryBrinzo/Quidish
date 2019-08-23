package com.quidish.anshgupta.login.Home.BottomNavigation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.MyAdsAndUserProfile.MyActiveAdsAdapter;
import com.quidish.anshgupta.login.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdsFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;
        view= inflater.inflate(R.layout.frag_ads, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle =view.findViewById(R.id.recycle);
        empty=view.findViewById(R.id.empty);

        adsAdapter = new MyActiveAdsAdapter(listactive, getContext());
        RecyclerView.LayoutManager recyceAll = new GridLayoutManager(getContext(),1);
        recycle.setLayoutManager(recyceAll);
        recyceAll.setAutoMeasureEnabled(false);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(adsAdapter);

        recycle.setNestedScrollingEnabled(false);

        if(myfba.getCurrentUser()==null){
            empty.setVisibility(View.VISIBLE);
        }

        else {
        userid=myfba.getCurrentUser().getUid();

        listactive.clear();
        addproductad();
        }

        return view;
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
    public void onResume() {
        super.onResume();

        // register connection status listene
    }


}

