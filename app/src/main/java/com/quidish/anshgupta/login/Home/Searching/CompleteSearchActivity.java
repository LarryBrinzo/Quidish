package com.quidish.anshgupta.login.Home.Searching;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;
import com.quidish.anshgupta.login.SpashScreenActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.max;

public class CompleteSearchActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SearchView search;
    LinearLayout back;
    List<String> sugglist=new ArrayList<>();
    NestedScrollView scrollView;
    ProgressBar progbar;
    CoordinatorLayout coordinatorLayout;
    private static final String ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION";
    RecyclerView suggestionrecycle;
    SuggestionAdapter suggestionAdapter;
    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
    SharedPreferences.Editor searchhint = pref.edit();

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

        handleVoiceSearch(getIntent());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String s = bundle.getString("active");

            if(s != null && s.equals("1")) {

                String ss = bundle.getString("searchitem");

                if(ss != null && !ss.isEmpty()) {
                    String x=ss;
                    search.setQuery(x,false);
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getApplicationContext(), MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                    suggestions.saveRecentQuery(x, null);
                    ss = ss.toLowerCase();

                    Intent intent=new Intent(getApplicationContext(),SearchShowActivity.class);
                    intent.putExtra("searchstring", ss);
                    startActivity(intent);

                }
            }
        }


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {

                newText=newText.trim();
                newText=newText.toLowerCase();

                sugglist.clear();
                suggestionrecycle.removeAllViewsInLayout();
                suggestionAdapter.notifyDataSetChanged();

                if(newText.length()>0) {

                    for (String s : SpashScreenActivity.searchdict) {

                        if(s.length()>newText.length() && s.startsWith(newText)){
                            sugglist.add(s);
                            suggestionAdapter.notifyItemInserted(sugglist.size() - 1);
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
                    int serachnumber=pref.getInt("serachnumber", 0)+1;
                    String srchitm="recent"+Integer.toString(serachnumber);
                    searchhint.putString(srchitm,s);

                    s=s.toLowerCase();
                    Intent intent=new Intent(getApplicationContext(),SearchShowActivity.class);
                    intent.putExtra("searchstring", s);
                    startActivity(intent);
                    return true;
                }

                return false;
            }

        });


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
