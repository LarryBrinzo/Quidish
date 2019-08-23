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
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.MyAccount.WishlistAdapter;
import com.quidish.anshgupta.login.R;

import java.util.ArrayList;
import java.util.List;


public class WishFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;
        view= inflater.inflate(R.layout.frag_wish, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle =view.findViewById(R.id.recycle);
        progressBar=view.findViewById(R.id.prog2);
        empty=view.findViewById(R.id.empty);

        recycle.setNestedScrollingEnabled(false);

        wishlistAdapter = new WishlistAdapter(wishlist, getContext());
        RecyclerView.LayoutManager recyceAll = new GridLayoutManager(getContext(),2);
        recycle.setLayoutManager(recyceAll);
        recyceAll.setAutoMeasureEnabled(false);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(wishlistAdapter);

        recycle.removeAllViewsInLayout();
        empty.setVisibility(View.GONE);

        progressBar.setVisibility(View.GONE);

        if(myfba.getCurrentUser()==null){
            empty.setVisibility(View.VISIBLE);
        }

        else {
            userid=myfba.getCurrentUser().getUid();

            userwish();
            addproductad();
        }


        return view;
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
                    price = "₹" + price;

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

}
