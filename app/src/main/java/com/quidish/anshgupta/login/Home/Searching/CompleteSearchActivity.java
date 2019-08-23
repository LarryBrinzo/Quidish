package com.quidish.anshgupta.login.Home.Searching;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.quidish.anshgupta.login.Home.AllCategoriesAdapter;
import com.quidish.anshgupta.login.Home.BottomNavigation.HomeFragment;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class CompleteSearchActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,Comparator<Pair<String,Integer>> {

    EditText search;
    ProgressBar searchprog;
    LinearLayout back,viewed,rest;
    List<Pair<String,Integer>> sugglist=new ArrayList<>();
    public static List<Pair<String,Pair<String,String>>> recsugglist=new ArrayList<>();
    NestedScrollView scrollView;
    ImageView cancel;
    int ct = 0;
    ProgressBar progbar;
    FirebaseAuth firebaseAuth;
    List<String> userads=new ArrayList<>();
    FirebaseUser fuser;
    public static TextView edit;
    TextView noresult;
    public static LinearLayout recent;
    ValueEventListener eventListener;
    List<AdModel> listitms=new ArrayList<>();
    CoordinatorLayout coordinatorLayout;
    public static RecyclerView suggestionrecycle,horzontalrecycle,recycleitm,categoryrecycle;
    SuggestionAdapter suggestionAdapter;
    public static RecentSearchAdapter recyclerAdapterRecent;
    ImageView fadeimage;
    ItemAdapter recycleitmAdapter;
    private int overallXScroll = 0;
    int bkct=0;
    private List<Pair<String,String>> catlist=new ArrayList<>();
    String userid;
    float alpha = 1.0f;
    float newAlpha = 1.0f;
    AllCategoriesAdapter allCategoriesAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_search);

        coordinatorLayout=findViewById(R.id.coordinator);
        checkConnection();

        search = findViewById(R.id.search);
        categoryrecycle =findViewById(R.id.categoryrecycle);
        back=findViewById(R.id.backbt);
        searchprog = findViewById(R.id.searchprog);
        scrollView=findViewById(R.id.scrollView2);
        progbar=findViewById(R.id.prog);
        suggestionrecycle =findViewById(R.id.suggestion);
        horzontalrecycle=findViewById(R.id.horzontalrecycle);
        edit=findViewById(R.id.edit);
        recycleitm=findViewById(R.id.itmrecycle);
        fadeimage=findViewById(R.id.imageView4);
        recent=findViewById(R.id.recent);
        viewed=findViewById(R.id.viewed);
        rest = findViewById(R.id.rest);
        noresult = findViewById(R.id.noresult);
        cancel=findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setText("");
            }
        });


        allCategoriesAdapter = new AllCategoriesAdapter(catlist,CompleteSearchActivity.this);
        GridLayoutManager recycecat = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false);
        categoryrecycle.setLayoutManager(recycecat);
        categoryrecycle.setLayoutManager(recycecat);
        categoryrecycle.setAdapter(allCategoriesAdapter);

        addCategories();

        recyclerAdapterRecent = new RecentSearchAdapter(recsugglist,CompleteSearchActivity.this);
        RecyclerView.LayoutManager recycesugg = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        horzontalrecycle.setLayoutManager(recycesugg);
        horzontalrecycle.setLayoutManager(recycesugg);
        horzontalrecycle.setAdapter(recyclerAdapterRecent);

        displayrecentsearch();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                int serachnumber=pref.getInt("serachnumber", 0);

                for(int i=serachnumber;i>=1;i--){

                    String itm="recent"+(i),img="recentimg"+(i);

                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();

                    searchhint.remove(itm);
                    searchhint.remove(img);

                    searchhint.apply();

                }

                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();
                searchhint.putInt("serachnumber",0);
                searchhint.apply();

                recent.setVisibility(View.GONE);
                recsugglist.clear();


            }
        });

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                InputMethodManager imm = (InputMethodManager) getApplicationContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                if (scrollY > oldScrollY) {
                    assert imm != null;
                    if (!imm.isAcceptingText()){
                    sugglist.clear();
                    suggestionrecycle.removeAllViewsInLayout();
                    suggestionAdapter.notifyDataSetChanged();}
                }
                else if (scrollY < oldScrollY) {
                    assert imm != null;
                    if (!imm.isAcceptingText()){
                        sugglist.clear();
                        suggestionrecycle.removeAllViewsInLayout();
                        suggestionAdapter.notifyDataSetChanged();}
                }

                if (scrollY == 0) {
                }

                if (scrollY == ( v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight() )) {
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        recycleitmAdapter = new ItemAdapter(listitms,CompleteSearchActivity.this);
        RecyclerView.LayoutManager recyceitms = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recycleitm.setLayoutManager(recyceitms);
        recycleitm.setLayoutManager(recyceitms);
        recycleitm.setAdapter(recycleitmAdapter);

        recycleitm.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                overallXScroll = overallXScroll + dx;

                newAlpha = alpha - 0.0055f;
                if (dx > 0 && newAlpha>=0.0f && newAlpha<=1.0f){
                    fadeimage.setAlpha(newAlpha);
                    alpha = newAlpha;
                }

                //if scroll right
                newAlpha = alpha + 0.012f;
                if (dx < 0 && newAlpha>=0.0f && newAlpha<=1.0f && overallXScroll<=480){
                    fadeimage.setAlpha(newAlpha);
                    alpha = newAlpha;
                }

                newAlpha = 1.0f;
                if(overallXScroll==0) {
                    fadeimage.setAlpha(newAlpha);
                    alpha = newAlpha;
                }

                newAlpha = 0.0f;
                if(overallXScroll>=480) {
                    fadeimage.setAlpha(newAlpha);
                    alpha = newAlpha;
                }
                }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        fuser = firebaseAuth.getCurrentUser();

        viewed.setVisibility(View.GONE);

        if(fuser!=null)
        userid= firebaseAuth.getCurrentUser().getUid();
        additm();



        suggestionAdapter = new SuggestionAdapter(sugglist, CompleteSearchActivity.this);
        RecyclerView.LayoutManager recyceSugg = new GridLayoutManager(CompleteSearchActivity.this, 1);
        suggestionrecycle.setLayoutManager(recyceSugg);
        recyceSugg.setAutoMeasureEnabled(false);
        suggestionrecycle.setItemAnimator(new DefaultItemAnimator());
        suggestionrecycle.setAdapter(suggestionAdapter);




        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence st, int start, int before, int count) {

                String newText = st.toString();

                if (newText.length() == 0) {
                    suggestionrecycle.setVisibility(View.GONE);
                    rest.setVisibility(View.VISIBLE);

                    sugglist.clear();
                    suggestionrecycle.removeAllViewsInLayout();
                    suggestionAdapter.notifyDataSetChanged();
                    searchprog.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                    noresult.setVisibility(View.GONE);
                }

                newText = newText.trim();
                newText = newText.toLowerCase();

                if (newText.length() == 1) {
                    searchprog.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                    noresult.setVisibility(View.GONE);

                }

                if (newText.length() > 1) {

                    searchprog.setVisibility(View.VISIBLE);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    String userclgid=pref.getString("userinstituteid", null);

                    DatabaseReference ref;
                    ref = FirebaseDatabase.getInstance().getReference().child("Ads").child(userclgid);

                    final String finalNewText = newText;
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            ct = 0;

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                String actualproductname=dataSnapshot1.child("ad_title").getValue(String.class);
                                String actualproductbrand=dataSnapshot1.child("brand").getValue(String.class);
                                String actualproductcat=dataSnapshot1.child("category").getValue(String.class);

                                stringStartsWith(actualproductname,finalNewText);
                                stringStartsWith(actualproductbrand,finalNewText);
                                stringStartsWith(actualproductcat,finalNewText);

                            }

                            searchprog.setVisibility(View.GONE);
                            cancel.setVisibility(View.VISIBLE);
                            suggestionrecycle.setVisibility(View.VISIBLE);
                            rest.setVisibility(View.GONE);

                            if (sugglist.size() == 0) {
                                noresult.setVisibility(View.VISIBLE);
                                noresult.setText("Sorry, we couldn't find result matching " + "\"" + search.getText() + "\"");
                            } else {
                                noresult.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    suggestionAdapter.notifyDataSetChanged();

                }

            }


        });



    }

    public void addCategories(){

        catlist.clear();

        catlist.add(new Pair("Mobile Phones & Tablets","mobile_cat"));
        catlist.add(new Pair("Electronics","electronics"));
        catlist.add(new Pair("Bicycles","bicycle"));
        catlist.add(new Pair("Furniture","furniture"));
        catlist.add(new Pair("Entertainment","entertainment"));
        catlist.add(new Pair("Photography","photo"));
        catlist.add(new Pair("Books & Stationery","books_cat"));
        catlist.add(new Pair("Home Appliances","ironing"));
        catlist.add(new Pair("Men's Fashion","men"));
        catlist.add(new Pair("Women's Fashion","women"));

        allCategoriesAdapter.notifyDataSetChanged();

    }


    public void stringStartsWith(String name, String finalNewText){

        name= name.toLowerCase();

        if(name.length()> finalNewText.length() && (name.startsWith(finalNewText))){

            if(ct==0){
                ct=1;

                sugglist.clear();
                suggestionrecycle.removeAllViewsInLayout();
                suggestionAdapter.notifyDataSetChanged();
            }

            if(sugglist.size()==0)
                sugglist.add(new Pair(name,finalNewText.length()));

            else
                sugglist.add(new Pair(name,finalNewText.length()));

        }

        else if(name.length()> finalNewText.length() && (name.contains(finalNewText))){

            if(ct==0){
                ct=1;

                sugglist.clear();
                suggestionrecycle.removeAllViewsInLayout();
                suggestionAdapter.notifyDataSetChanged();
            }

            sugglist.add(new Pair(name,0));

        }

        else {

            String[] splited = finalNewText.split(" ");

            for (String aSplited : splited) {

                if (name.length() > aSplited.length() && name.contains(aSplited)){

                    if(ct==0){
                        ct=1;

                        sugglist.clear();
                        suggestionrecycle.removeAllViewsInLayout();
                        suggestionAdapter.notifyDataSetChanged();
                    }

                    sugglist.add(new Pair(name,0));
                }
            }
        }

    }


    public void displayrecentsearch(){

        recsugglist.clear();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int serachnumber=pref.getInt("serachnumber", 0);

        for(int i=serachnumber;i>=1;i--){
            String srchkey="recent"+(i);
            String srchimgkey="recentimg"+(i);

            String searchitm=pref.getString(srchkey, null);
            String searchimgitm=pref.getString(srchimgkey, null);

            if(searchitm!=null){
                if(searchimgitm==null){
                    recsugglist.add(new Pair <> (Integer.toString(i),new Pair <>(searchitm, "1")));
                    recyclerAdapterRecent.notifyItemInserted(recsugglist.size() - 1);}

                else {
                    recsugglist.add(new Pair <> (Integer.toString(i),new Pair <>(searchitm, searchimgitm)));
                    recyclerAdapterRecent.notifyItemInserted(recsugglist.size() - 1);
                }

            }
        }

        if(recsugglist.size()==0)
            recent.setVisibility(View.GONE);
    }



    public void additm(){

        userads.clear();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int serachnumber=pref.getInt("recentadnumber", 0);

        for(int i=serachnumber;i>=1;i--){
            String srchkey="recentad"+(i);

            String searchitm=pref.getString(srchkey, null);

            if(searchitm!=null){
                userads.add(searchitm);

            }
        }

        addinsert();

        if(userads.size()>=1)
            viewed.setVisibility(View.VISIBLE);

    }


    public void addinsert(){

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Ads");

        eventListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                listitms.clear();
//                AdModel xx=new AdModel();
//                xx.setAdno("-1");
//
//                listitms.add(xx);

                int si=userads.size();

                for(int i=si-1;i>=0;i--) {

                    String ad_no = userads.get(i);

                    String[] splited = ad_no.split(" ");
                    ad_no=splited[1];
                    String clgid=splited[0];

                    String usid = dataSnapshot.child(clgid).child(ad_no).child("userid").getValue(String.class);
                    String adsold = dataSnapshot.child(clgid).child(ad_no).child("sold").getValue(String.class);

                    if (adsold == null || adsold.equals("1")){}

                    else if (HomeFragment.fuser != null && HomeFragment.userid.equals(usid)) {
                    }

                    else{

                        bkct++;

                        if(bkct>=6)
                            break;

                        String adtitle = dataSnapshot.child(clgid).child(ad_no).child("ad_title").getValue(String.class);
                        String price = dataSnapshot.child(clgid).child(ad_no).child("price").getValue(String.class);
                        String brand = dataSnapshot.child(clgid).child(ad_no).child("brand").getValue(String.class);
                        String image1 = dataSnapshot.child(clgid).child(ad_no).child("image").getValue(String.class);


                        AdModel fire = new AdModel();

                        String[] splited2 = image1.split(" ");

                        fire.setPrice(price);
                        fire.setTitle(adtitle);
                        fire.setBrand(brand);
                        fire.setAdno(clgid+" "+ad_no);
                        fire.setActivity("2");
                        fire.setSold("0");
                        fire.setUser(usid);
                        fire.setUrl1(splited2[0]);

                        if(HomeFragment.listwish.contains(ad_no))
                            fire.setWish("1");

                        else
                            fire.setWish("0");

                        listitms.add(fire);
                        recycleitmAdapter.notifyItemInserted(listitms.size() - 1);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }



    @Override
    public int compare(Pair<String,Integer> p1, Pair<String,Integer> p2) {
        return p1.second - p2.second;
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

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int serachnumber=pref.getInt("serachnumber", 0);

        if(serachnumber==0)
            recent.setVisibility(View.GONE);

        horzontalrecycle.scrollToPosition(0);

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
