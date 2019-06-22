package com.quidish.anshgupta.login.Home.Searching;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
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
import com.quidish.anshgupta.login.Home.UserAdsAdapter;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;
import com.quidish.anshgupta.login.SpashScreenActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.StrictMath.max;

public class SearchShowActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,Comparator<Pair<String,Integer>> {

    List<AdModel> searchlist=new ArrayList<>();
    FirebaseAuth myfba;
    DatabaseReference databaseReference;
    NestedScrollView scrollView;
    ProgressBar progbar;
    List<Pair<String,Integer>> sugglist=new ArrayList<>();
    long adstart,adend,itm=0,endstart=1;
    UserAdsAdapter recyclerAdapterSearch;
    int sclrct=0,notify=1,scroll=1;
    ImageView searchbt;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recycle,suggestionrecycle;
    LinearLayout filter,bbtn,bbtn2,toolbar1,toolbar2;
    TextView prod;
    FirebaseUser fuser;
    List<Pair<String,Integer>> list=new ArrayList<>();
    String userid,userclg="0",suggstring=null;
    int rsearchinsert=0;
    SuggestionAdapter suggestionAdapter;
    SearchView search;
    String sstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_show);

        checkConnection();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search = findViewById(R.id.search);
        assert searchManager != null;
        search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search.setIconifiedByDefault(false);
        search.setQueryRefinementEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba= FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle=findViewById(R.id.recycle);
        suggestionrecycle =findViewById(R.id.suggestion);
        progbar=findViewById(R.id.prog);
        scrollView=findViewById(R.id.scrollView2);
        prod=findViewById(R.id.prod);
        filter=findViewById(R.id.filter);
        bbtn=findViewById(R.id.backbt);
        bbtn2=findViewById(R.id.backbt2);
        searchbt=findViewById(R.id.searchbt);
        toolbar1=findViewById(R.id.toolbar1);
        toolbar2=findViewById(R.id.toolbar2);

        recyclerAdapterSearch = new UserAdsAdapter(searchlist,SearchShowActivity.this);
        RecyclerView.LayoutManager recyceAll = new GridLayoutManager(SearchShowActivity.this,2);
        recycle.setLayoutManager(recyceAll);
        recyceAll.setAutoMeasureEnabled(false);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapterSearch);

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
                String srchitm="recent"+Integer.toString(serachnumber);
                searchhint.putString(srchitm,s);

                String srchimg="recentimg"+Integer.toString(serachnumber);
                searchhint.putString(srchimg,"1");

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

            else if(s != null && rsearchinsert==0) {
                searchlist.clear();
                recycle.removeAllViewsInLayout();
                recyclerAdapterSearch.notifyDataSetChanged();
                Adtraversal(s);
            }
        }


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

        suggestionAdapter = new SuggestionAdapter(sugglist,SearchShowActivity.this);
        RecyclerView.LayoutManager recyceSugg = new GridLayoutManager(SearchShowActivity.this,1);
        suggestionrecycle.setLayoutManager(recyceSugg);
        recyceSugg.setAutoMeasureEnabled(false);
        suggestionrecycle.setItemAnimator( new DefaultItemAnimator());
        suggestionrecycle.setAdapter(suggestionAdapter);


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

                    if(sugglist.size()>0 && list.size()==0)
                        suggstring=sugglist.get(0).first;

                    searchlist.clear();
                    recycle.removeAllViewsInLayout();
                    recyclerAdapterSearch.notifyDataSetChanged();
                    Adtraversal(s);

                    return true;
                }

                return false;
            }

        });


        searchbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar1.setVisibility(View.GONE);
                toolbar2.setVisibility(View.VISIBLE);

                search.requestFocus();
            }
        });

        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view.findFocus());
                }
            }
        });

        bbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        bbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(fuser!=null)
            userid=myfba.getCurrentUser().getUid();

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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

                    else if (fuser != null &&  (userid.equals(usid))) {
                        if(adend>0){
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

                        if(suggstring!=null){

                            String[] splited2 = suggstring.split(" ");

                            for (String ss : splited2) {
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
                        }

                        if(result==1){

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

                            searchlist.add(fire);

                            if(rsearchinsert==0){

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();

                                int serachnumber=pref.getInt("serachnumber", 0);

                                String srchimg="recentimg"+Integer.toString(serachnumber);
                                searchhint.putString(srchimg,image1);

                                searchhint.apply();

                                CompleteSearchActivity.recsugglist.add(0,new Pair <> (Integer.toString(serachnumber),new Pair <>(sstring, image1)));
                                CompleteSearchActivity.recyclerAdapterRecent.notifyItemInserted(0);

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

                    adinst = dataSnapshot.child(ad_no).child("institute").getValue(String.class);
                    adsold = dataSnapshot.child(ad_no).child("sold").getValue(String.class);
                    usid = dataSnapshot.child(ad_no).child("userid").getValue(String.class);

                    if (adsold==null || adsold.equals("1"))
                    { if(adend>0){
                        adend--; } }

                    else if (fuser != null &&  (userid.equals(usid))) {
                        if(adend>0){
                            adend--;
                        } }

                    else{

                        int result=0;

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

                        if(suggstring!=null){

                            String[] splited2 = suggstring.split(" ");

                            for (String ss : splited2) {
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
                        }

                        if(result==1){

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

                            searchlist.add(fire);

                            if(rsearchinsert==0){

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();

                                int serachnumber=pref.getInt("serachnumber", 0);

                                String srchimg="recentimg"+Integer.toString(serachnumber);
                                searchhint.putString(srchimg,image1);

                                searchhint.apply();

                                CompleteSearchActivity.recsugglist.add(0,new Pair <> (Integer.toString(serachnumber),new Pair <>(sstring, image1)));
                                CompleteSearchActivity.recyclerAdapterRecent.notifyItemInserted(0);

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

                if (diff == 0 && sclrct==1 && itm<SpashScreenActivity.adcount && scroll==1 && adstart<=endstart) {

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

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }


}
