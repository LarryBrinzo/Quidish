package com.quidish.anshgupta.login.Home.Searching;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.Home.BottomNavigation.HomeFragment;
import com.quidish.anshgupta.login.Home.UserAdsAdapter;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static java.lang.StrictMath.max;

public class SearchShowActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,Comparator<Pair<String,Integer>> {

    FirebaseAuth myfba;
    DatabaseReference databaseReference;
    NestedScrollView scrollView;
    ProgressBar progbar;
    long adstart,adend,itm=0,endstart=1;
    UserAdsAdapter recyclerAdapterSearch;
    int sclrct=0,notify=1,scroll=1;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recycle;
    HashSet<String> set = new HashSet<String>();
    TextView prod;
    int result=0;
    FirebaseUser fuser;
    String adtitle_s,category_s,brand_s;
    public static List<AdModel> searchlist=new ArrayList<>();
    String userid,suggstring=null;
    int rsearchinsert=0;
    LinearLayout backb;
    String sstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_show);

        checkConnection();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba= FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle=findViewById(R.id.recycle);
        progbar=findViewById(R.id.prog);
        scrollView=findViewById(R.id.scrollView2);
        prod=findViewById(R.id.prod);
        backb=findViewById(R.id.backbt);

        set.clear();

        recyclerAdapterSearch = new UserAdsAdapter(searchlist,SearchShowActivity.this);
        RecyclerView.LayoutManager recyceAll = new GridLayoutManager(SearchShowActivity.this,2);
        recycle.setLayoutManager(recyceAll);
        recyceAll.setAutoMeasureEnabled(false);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapterSearch);

        backb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(fuser!=null)
            userid=myfba.getCurrentUser().getUid();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String s = bundle.getString("searchstring");
            prod.setText(s);

            if (bundle.containsKey("click")){
                searchlist.clear();
                recycle.removeAllViewsInLayout();
                recyclerAdapterSearch.notifyDataSetChanged();
                rsearchinsert=1;
                Adtraversal(s);
            }

            if(rsearchinsert==0){
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();

                int serachnumber=pref.getInt("serachnumber", 0)+1;
                String srchitm="recent"+(serachnumber);
                searchhint.putString(srchitm,s);

                searchhint.putInt("serachnumber",serachnumber);
                searchhint.apply();

                sstring=s;
            }

            if (bundle.containsKey("sugghstring")){
                searchlist.clear();
                recycle.removeAllViewsInLayout();
                recyclerAdapterSearch.notifyDataSetChanged();
                suggstring=bundle.getString("sugghstring");
                Adtraversal(s);
            }

            else if(rsearchinsert==0) {
                searchlist.clear();
                recycle.removeAllViewsInLayout();
                recyclerAdapterSearch.notifyDataSetChanged();
                Adtraversal(s);
            }
        }


    }

    public void searchevent(final String searchitem,final long start){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final String userclgid=pref.getString("userinstituteid", null);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads").child(userclgid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                searchlist.clear();

                for(long i=start;i>=adend;i--) {

                    String ad_no=Long.toString(i);

                    itm++;

                    String adsold = dataSnapshot.child(ad_no).child("sold").getValue(String.class);
                    String usid = dataSnapshot.child(ad_no).child("userid").getValue(String.class);

                    if (adsold==null || adsold.equals("1"))
                    { if(adend>0){
                        adend--; } }

                    else if (fuser != null &&  (userid.equals(usid))) {
                        if(adend>0){
                            adend--;
                        } }

                    else if(set.contains(userclgid+" "+ad_no)){}

                    else{

                        result=0;

                        ad_no=Long.toString(i);
                        String price = dataSnapshot.child(ad_no).child("price").getValue(String.class);
                        String image1 = dataSnapshot.child(ad_no).child("image").getValue(String.class);

                        String adtitle = dataSnapshot.child(ad_no).child("ad_title").getValue(String.class);
                        adtitle=adtitle.trim();
                        adtitle_s=adtitle.toLowerCase();

                        String category = dataSnapshot.child(ad_no).child("category").getValue(String.class);
                        category=category.trim();
                        category_s=category.toLowerCase();

                        String brand = dataSnapshot.child(ad_no).child("brand").getValue(String.class);
                        brand=brand.trim();
                        brand_s=brand.toLowerCase();

                        serachString(searchitem);

                        if(suggstring!=null){
                            serachString(suggstring);
                        }

                        if(result==1){

                            String[] splited = image1.split(" ");

                            AdModel fire = new AdModel();

                            fire.setPrice(price);
                            fire.setTitle(adtitle);
                            fire.setBrand(brand);
                            fire.setAdno(userclgid+" "+ad_no);
                            fire.setUrl1(splited[0]);
                            fire.setActivity("2");
                            fire.setSold("0");
                            fire.setUser(usid);

                            if(HomeFragment.listwish.contains(ad_no))
                                fire.setWish("1");

                            else
                                fire.setWish("0");

                            set.add(userclgid+" "+ad_no);

                            searchlist.add(fire);

                            if(rsearchinsert==0){

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();

                                int serachnumber=pref.getInt("serachnumber", 0);

                                String srchimg="recentimg"+(serachnumber);
                                searchhint.putString(srchimg,splited[0]);

                                searchhint.apply();

                                rsearchinsert=1;
                            }

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




                    if(i<=endstart)
                        break;




                    ad_no=Long.toString(endstart);

                    itm++;
                    endstart++;

                    adsold = dataSnapshot.child(ad_no).child("sold").getValue(String.class);
                    usid = dataSnapshot.child(ad_no).child("userid").getValue(String.class);

                    if (adsold==null || adsold.equals("1"))
                    { if(adend>0){
                        adend--; } }

                    else if (fuser != null &&  (userid.equals(usid))) {
                        if(adend>0){
                            adend--;
                        } }

                    else if(set.contains(userclgid+" "+ad_no))
                    { }

                    else{

                        result=0;

                        ad_no=Long.toString(i);
                        String price = dataSnapshot.child(ad_no).child("price").getValue(String.class);
                        String image1 = dataSnapshot.child(ad_no).child("image").getValue(String.class);

                        String adtitle = dataSnapshot.child(ad_no).child("ad_title").getValue(String.class);
                        adtitle=adtitle.trim();
                        adtitle_s=adtitle.toLowerCase();

                        String category = dataSnapshot.child(ad_no).child("category").getValue(String.class);
                        category=category.trim();
                        category_s=category.toLowerCase();

                        String brand = dataSnapshot.child(ad_no).child("brand").getValue(String.class);
                        brand=brand.trim();
                        brand_s=brand.toLowerCase();

                        serachString(searchitem);

                        if(suggstring!=null){
                            serachString(suggstring);
                        }

                        if(result==1){


                            String[] splited = image1.split(" ");

                            AdModel fire = new AdModel();

                            fire.setPrice(price);
                            fire.setTitle(adtitle);
                            fire.setBrand(brand);
                            fire.setAdno(userclgid+" "+ad_no);
                            fire.setUrl1(splited[0]);
                            fire.setActivity("2");
                            fire.setSold("0");
                            fire.setUser(usid);

                            set.add(userclgid+" "+ad_no);

                            searchlist.add(fire);

                            if(rsearchinsert==0){

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();

                                int serachnumber=pref.getInt("serachnumber", 0);

                                String srchimg="recentimg"+(serachnumber);
                                searchhint.putString(srchimg,splited[0]);

                                searchhint.apply();

                                rsearchinsert=1;
                            }

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


    public void serachString(String searchitem){

        searchitem=searchitem.toLowerCase();
        String[] splited = searchitem.split(" ");

        for (String ss : splited) {
            boolean b = adtitle_s.contains(ss);
            if (b){
                result=1;
                break;
            }

            b=brand_s.contains(ss);
            if (b){
                result=1;
                break;
            }

            b=category_s.contains(ss);
            if (b){
                result=1;
                break;
            }


        }

    }



    public void Adtraversal(final String searchst){

        progbar.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adstart= StrictMath.max(HomeFragment.adcount,1);
                itm=0;
                adend= Math.max(adstart-49,1);
                scroll=0;
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

                if (diff == 0 && sclrct==1 && itm<=HomeFragment.adcount && scroll==1) {

                    scroll=0;
                    notify=1;

                    progbar.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adend= Math.max(adstart-25,1);
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
                    if (sclrct==1 && itm<=HomeFragment.adcount && scroll==0) {
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


    @Override
    public int compare(Pair<String,Integer> p1, Pair<String,Integer> p2) {
        return p1.first.length() - p2.first.length();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
    }
    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
