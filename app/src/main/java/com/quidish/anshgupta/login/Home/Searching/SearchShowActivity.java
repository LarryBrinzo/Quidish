package com.quidish.anshgupta.login.Home.Searching;

import android.content.Intent;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.quidish.anshgupta.login.Home.HomeActivity;
import com.quidish.anshgupta.login.Home.UserAdsAdapter;
import com.quidish.anshgupta.login.R;
import com.quidish.anshgupta.login.SpashScreenActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.StrictMath.max;

public class SearchShowActivity extends AppCompatActivity {

    List<AdModel> searchlist=new ArrayList<>();
    FirebaseAuth myfba;
    DatabaseReference databaseReference;
    NestedScrollView scrollView;
    ProgressBar progbar;
    long adstart,adend,itm=0;
    UserAdsAdapter recyclerAdapterSearch;
    int sclrct=0,notify=1,scroll=1;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recycle;
    FirebaseUser fuser;
    String userid,userclg="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_show);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba= FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle=findViewById(R.id.recycle);
        progbar=findViewById(R.id.prog);
        scrollView=findViewById(R.id.scrollView2);

        if(fuser!=null)
            userid=myfba.getCurrentUser().getUid();

        recyclerAdapterSearch = new UserAdsAdapter(searchlist,SearchShowActivity.this);
        RecyclerView.LayoutManager recyceAll = new GridLayoutManager(SearchShowActivity.this,2);
        recycle.setLayoutManager(recyceAll);
        recyceAll.setAutoMeasureEnabled(false);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapterSearch);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String s = bundle.getString("searchstring");

            if(s != null) {
                    searchlist.clear();
                    recycle.removeAllViewsInLayout();
                    recyclerAdapterSearch.notifyDataSetChanged();
                    Adtraversal(s);
            }
        }

    }

    public void searchevent(final String searchitem,final long start){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(long i=start;i>=adend;i--) {

                    String ad_no=Long.toString(i);

                    itm++;

                    String adinst = dataSnapshot.child(ad_no).child("institute").getValue(String.class);
                    String adsold = dataSnapshot.child(ad_no).child("sold").getValue(String.class);
                    String usid = dataSnapshot.child(ad_no).child("userid").getValue(String.class);

                    if (adsold==null || adsold.equals("1"))
                    { if(adend>0){
                        adend--; } }

                    else if (fuser != null) {
                        if (userid.equals(usid))
                        {if(adend>0){
                            adend--;
                        } } }

                    else if (userclg != null && !userclg.equals("") && !userclg.equals("0") && !userclg.equals(adinst))
                    { if(adend>0){
                        adend--;
                    } }

                    else{

                        int result=0;

                        ad_no=Long.toString(i);
                        String price = dataSnapshot.child(ad_no).child("price").getValue(String.class);
                        String image1 = dataSnapshot.child(ad_no).child("image1").getValue(String.class);


                        String adtitle = dataSnapshot.child(ad_no).child("ad_title").getValue(String.class);
                        assert adtitle != null;
                        adtitle=adtitle.trim();
                        String adtitle2=adtitle.toLowerCase();

                        String model = dataSnapshot.child(ad_no).child("model").getValue(String.class);
                        assert model != null;
                        model=model.trim();
                        model=model.toLowerCase();

                        String category = dataSnapshot.child(ad_no).child("category").getValue(String.class);
                        assert category != null;
                        category=category.trim();
                        category=category.toLowerCase();

                        String sub_category = dataSnapshot.child(ad_no).child("sub_category").getValue(String.class);
                        assert sub_category != null;
                        sub_category=sub_category.trim();
                        sub_category=sub_category.toLowerCase();

                        String brand = dataSnapshot.child(ad_no).child("brand").getValue(String.class);
                        assert brand != null;
                        brand=brand.trim();
                        String brand2=brand.toLowerCase();

                        price = "₹" + price;

                        AdModel fire = new AdModel();

                        fire.setPrice(price);
                        fire.setTitle(adtitle);
                        fire.setBrand(brand);
                        fire.setAdno(ad_no);
                        fire.setUrl1(image1);
                        fire.setActivity("2");
                        fire.setSold("0");
                        fire.setUser(usid);

                        if(HomeActivity.listwish.contains(ad_no))
                            fire.setWish("1");

                        else
                            fire.setWish("0");

                        String[] splited = searchitem.split(" ");

                        for (String ss : splited) {
                            boolean b = model.contains(ss);
                            if (b){
                                result=1;
                                break;
                            }

                            b=adtitle2.contains(ss);
                            if (b){
                                result=1;
                                break;
                            }

                            b=brand2.contains(ss);
                            if (b){
                                result=1;
                                break;
                            }

                            b=category.contains(ss);
                            if (b){
                                result=1;
                                break;
                            }

                            b=sub_category.contains(ss);
                            if (b){
                                result=1;
                                break;
                            }
                        }

                        if(result==1){
                            searchlist.add(fire);

                            if(notify==1){

                                if(searchlist.size()>=1)
                                    recyclerAdapterSearch.notifyItemInserted(searchlist.size() - 1);

                                else
                                    recyclerAdapterSearch.notifyItemInserted(0);
                            }

                        }

                        else if(adend>0){
                            adend--;
                        }

                    }
                }

                adstart=adend;
                sclrct=1;
                scroll=1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    public void Adtraversal(final String searchst){

        progbar.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adstart= SpashScreenActivity.adcount-1;
                itm=0;
                adend=max(adstart-49,1);
                searchevent(searchst,adstart);
                progbar.setVisibility(View.GONE);
            }
        }, 2000);


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = scrollView.getChildAt(scrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView
                        .getScrollY()));

                if (diff == 0 && sclrct==1 && itm<SpashScreenActivity.adcount && scroll==1) {

                    scroll=0;
                    notify=1;

                    progbar.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adend=max(adstart-25,1);
                            searchevent(searchst,adstart);

                            progbar.setVisibility(View.GONE);
                        }
                    }, 1000);
                }
            }
        });


        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > oldScrollY) {
                    //Scroll DOWN
                }
                else if (scrollY < oldScrollY) {
                    if (sclrct==1 && itm<SpashScreenActivity.adcount && scroll==0) {
                        notify=0;
                    }
                }

                if (scrollY == 0) {
                    //TOP SCROLL
                }

                if (scrollY == ( v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight() )) {
                    //BOTTOM SCROLL
                }
            }
        });

    }

}
