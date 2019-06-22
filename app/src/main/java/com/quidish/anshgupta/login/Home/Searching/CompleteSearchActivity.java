package com.quidish.anshgupta.login.Home.Searching;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.Home.HomeActivity;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;
import com.quidish.anshgupta.login.SpashScreenActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CompleteSearchActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,Comparator<Pair<String,Integer>> {

    SearchView search;
    LinearLayout back,viewed;
    List<Pair<String,Integer>> sugglist=new ArrayList<>();
    List<Pair<String,Integer>> list=new ArrayList<>();
    public static List<Pair<String,Pair<String,String>>> recsugglist=new ArrayList<>();
    NestedScrollView scrollView;
    LinearLayout books,mobile,cycle,electronics;
    ProgressBar progbar;
    FirebaseAuth firebaseAuth;
    List<String> userads=new ArrayList<>();
    FirebaseUser fuser;
    public static TextView edit;
    public static LinearLayout recent;
    ValueEventListener eventListener;
    List<AdModel> listitms=new ArrayList<>();
    CoordinatorLayout coordinatorLayout;
    private static final String ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION";
    public static RecyclerView suggestionrecycle,horzontalrecycle,recycleitm;
    SuggestionAdapter suggestionAdapter;
    public static RecentSearchAdapter recyclerAdapterRecent;
    ImageView fadeimage;
    ItemAdapter recycleitmAdapter;
    private int overallXScroll = 0;
    int bkct=0;
    String userid;
    float alpha = 1.0f;
    float newAlpha = 1.0f;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_search);

        coordinatorLayout=findViewById(R.id.coordinator);
        checkConnection();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search = findViewById(R.id.search);
        assert searchManager != null;
        search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search.setIconifiedByDefault(false);
        search.setQueryRefinementEnabled(true);

        back=findViewById(R.id.backbt);
        scrollView=findViewById(R.id.scrollView2);
        progbar=findViewById(R.id.prog);
        suggestionrecycle =findViewById(R.id.suggestion);
        horzontalrecycle=findViewById(R.id.horzontalrecycle);
        edit=findViewById(R.id.edit);
        recycleitm=findViewById(R.id.itmrecycle);
        fadeimage=findViewById(R.id.imageView4);
        recent=findViewById(R.id.recent);
        books=findViewById(R.id.books);
        cycle=findViewById(R.id.cycle);
        mobile=findViewById(R.id.mobile);
        electronics=findViewById(R.id.electronics);
        viewed=findViewById(R.id.viewed);

        recyclerAdapterRecent = new RecentSearchAdapter(recsugglist,CompleteSearchActivity.this);
        RecyclerView.LayoutManager recycesugg = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        horzontalrecycle.setLayoutManager(recycesugg);
        horzontalrecycle.setLayoutManager(recycesugg);
        horzontalrecycle.setAdapter(recyclerAdapterRecent);

        displayrecentsearch();

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

        suggestionrecycle.setNestedScrollingEnabled(false);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        suggestionAdapter = new SuggestionAdapter(sugglist,CompleteSearchActivity.this);
        RecyclerView.LayoutManager recyceSugg = new GridLayoutManager(CompleteSearchActivity.this,1);
        suggestionrecycle.setLayoutManager(recyceSugg);
        recyceSugg.setAutoMeasureEnabled(false);
        suggestionrecycle.setItemAnimator( new DefaultItemAnimator());
        suggestionrecycle.setAdapter(suggestionAdapter);

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
        {
            userid= firebaseAuth.getCurrentUser().getUid();
            additm();
        }

        handleVoiceSearch(getIntent());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String s = bundle.getString("active");

            if(s != null && s.equals("1")) {

                String ss = bundle.getString("searchitem");

                if(ss != null && !ss.isEmpty()) {
                    String x=ss;
                    search.setQuery(x,false);
                    ss = ss.toLowerCase();

                    Intent intent=new Intent(getApplicationContext(),SearchShowActivity.class);
                    intent.putExtra("searchstring", ss);
                    intent.putExtra("click", "1");
                    startActivity(intent);
                }
            }
        }

        books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ss = "book";
                ss = ss.toLowerCase();

                Intent intent=new Intent(getApplicationContext(),SearchShowActivity.class);
                intent.putExtra("searchstring", ss);
                intent.putExtra("click", "1");
                startActivity(intent);
            }
        });

        cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ss = "cycle";
                ss = ss.toLowerCase();

                Intent intent=new Intent(getApplicationContext(),SearchShowActivity.class);
                intent.putExtra("searchstring", ss);
                intent.putExtra("click", "1");
                startActivity(intent);
            }
        });

        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ss = "mobile";
                ss = ss.toLowerCase();

                Intent intent=new Intent(getApplicationContext(),SearchShowActivity.class);
                intent.putExtra("searchstring", ss);
                intent.putExtra("click", "1");
                startActivity(intent);
            }
        });

        electronics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ss = "electronics";
                ss = ss.toLowerCase();

                Intent intent=new Intent(getApplicationContext(),SearchShowActivity.class);
                intent.putExtra("searchstring", ss);
                intent.putExtra("click", "1");
                startActivity(intent);
            }
        });


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {

                newText=newText.trim();
                newText=newText.toLowerCase();

                list.clear();
                suggestionrecycle.removeAllViewsInLayout();
                suggestionAdapter.notifyDataSetChanged();

                if(newText.length()>0) {

                    for (String s : SpashScreenActivity.searchdict) {

                        if(s.length()>newText.length() && s.startsWith(newText) && list.size()<=5)
                            list.add(new Pair <> (s,newText.length()));

                        if(list.size()>5)
                            break;
                    }

                    if(list.size()>0) {
                        sugglist.clear();
                        suggestionrecycle.removeAllViewsInLayout();
                        suggestionAdapter.notifyDataSetChanged();

                        sugglist.addAll(list);
                        CompleteSearchActivity ss = new CompleteSearchActivity();
                        Collections.sort(sugglist, ss);
                        suggestionAdapter.notifyDataSetChanged();
                    }

                    else {

                        for (String s : SpashScreenActivity.searchdict) {

                            if(s.length()>newText.length() && s.contains(newText) && list.size()<=5)
                                list.add(new Pair <> (s,0));

                            if(list.size()>5)
                                break;
                        }

                        String[] splited = newText.split(" ");

                        for(int j=0;j<splited.length;j++){

                            for (String s : SpashScreenActivity.searchdict) {

                               if(s.length()>splited[j].length() && s.contains(splited[j]) && list.size()<=5)
                                   list.add(new Pair <> (s,0));

                                if(list.size()>5)
                                    break;
                            }

                            if(list.size()>5)
                                break;
                        }

                        if(list.size()>0){
                            sugglist.clear();
                            suggestionrecycle.removeAllViewsInLayout();
                            suggestionAdapter.notifyDataSetChanged();

                            sugglist.addAll(list);
                            CompleteSearchActivity ss = new CompleteSearchActivity();
                            Collections.sort(sugglist, ss);
                            suggestionAdapter.notifyDataSetChanged();
                        }

                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                String s=query;

                if(!s.isEmpty())
                {
                    s=s.toLowerCase();
                    Intent intent=new Intent(getApplicationContext(),SearchShowActivity.class);
                    intent.putExtra("searchstring", s);
                    if(sugglist.size()>0 && list.size()==0)
                        intent.putExtra("sugghstring", sugglist.get(0).first);
                    startActivity(intent);
                    return true;
                }

                return false;
            }

        });


    }


    public void displayrecentsearch(){

        recsugglist.clear();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int serachnumber=pref.getInt("serachnumber", 0);

        int ind=0;

        for(int i=serachnumber;i>=1;i--){
            String srchkey="recent"+Integer.toString(i);
            String srchimgkey="recentimg"+Integer.toString(i);

            String searchitm=pref.getString(srchkey, null);
            String searchimgitm=pref.getString(srchimgkey, null);

            if(searchitm!=null && searchimgitm!=null){
            recsugglist.add(new Pair <> (Integer.toString(i),new Pair <>(searchitm, searchimgitm)));
            recyclerAdapterRecent.notifyItemInserted(recsugglist.size() - 1);

                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();

                String srchitm="recent"+recsugglist.get(ind).first;
                searchhint.putString(srchitm,recsugglist.get(ind).second.first);

                String srchimg="recentimg"+recsugglist.get(ind).first;
                searchhint.putString(srchimg,recsugglist.get(ind).second.second);
                searchhint.apply();

                ind++;

                searchhint.putInt("serachnumber",recsugglist.size());
                searchhint.apply();
            }
        }

        if(recsugglist.size()==0)
            recent.setVisibility(View.GONE);
    }


    private void handleVoiceSearch(Intent intent) {
        if (intent != null && ACTION_VOICE_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search.setQuery(query, true);

            Intent intent2=new Intent(getApplicationContext(),SearchShowActivity.class);
            intent2.putExtra("searchstring", query);
            startActivity(intent);
        }
    }


    public void additm(){

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("recent");

        eventListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                userads.clear();

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()) {

                    userads.add(dataSnapshot1.getValue(String.class));
                }

                addinsert();

                if(userads.size()>1)
                viewed.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    public void addinsert(){

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Ads");

        eventListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                listitms.clear();
                AdModel xx=new AdModel();
                xx.setAdno("-1");

                listitms.add(xx);

                int si=userads.size();

                for(int i=si-1;i>=0;i--) {

                    String ad_no = userads.get(i);

                    String usid = dataSnapshot.child(ad_no).child("userid").getValue(String.class);

                    if (HomeActivity.fuser != null && HomeActivity.userid.equals(usid)) {
                    }

                    else{

                        bkct++;

                        if(bkct>=6)
                            break;

                        String adtitle = dataSnapshot.child(ad_no).child("ad_title").getValue(String.class);
                        String price = dataSnapshot.child(ad_no).child("price").getValue(String.class);
                        String brand = dataSnapshot.child(ad_no).child("brand").getValue(String.class);
                        String image1 = dataSnapshot.child(ad_no).child("image1").getValue(String.class);

                        price = "₹" + price;

                        AdModel fire = new AdModel();

                        fire.setPrice(price);
                        fire.setTitle(adtitle);
                        fire.setBrand(brand);
                        fire.setAdno(ad_no);
                        fire.setActivity("2");
                        fire.setSold("0");
                        fire.setUser(usid);
                        fire.setUrl1(image1);

                        if(HomeActivity.listwish.contains(ad_no))
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
        return p1.first.length() - p2.first.length();
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
