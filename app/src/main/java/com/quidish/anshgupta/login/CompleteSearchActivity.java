package com.quidish.anshgupta.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.List;

public class CompleteSearchActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    EditText search;
    LinearLayout back;
    ImageButton voices;
    List<FireModel> searchlist=new ArrayList<>();
    List<String> listwish=new ArrayList<>();
    FirebaseAuth myfba;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    CoordinatorLayout coordinatorLayout;
    private static final int REQUEST_CODE = 1234;
    RecyclerView recycle;
    FirebaseUser fuser;
    String userid;
    int j;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_search);

        coordinatorLayout=findViewById(R.id.coordinator);
        checkConnection();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba= FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();

        if(fuser!=null)
            userid=myfba.getCurrentUser().getUid();

        search=findViewById(R.id.search);
        back=findViewById(R.id.backbt);
        voices=findViewById(R.id.voice);
        recycle =findViewById(R.id.recycle);
        progressBar=findViewById(R.id.prog2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String s = bundle.getString("active");

            if(s != null && s.equals("1")) {

                String ss = bundle.getString("searchitem");

                if(ss != null && !ss.isEmpty()) {
                    String x=ss;
                    search.setText(x);
                    ss = ss.toLowerCase();
                    userwish(ss);
                }
            }
        }

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    recycle.removeAllViewsInLayout();
                    String s=search.getText().toString();

                    if(!s.isEmpty())
                    {
                    s=s.toLowerCase();
                    userwish(s);
                    return true;
                    }
                }
                return false;
            }
        });

        voices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            final ArrayList < String > matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!matches.isEmpty())
            {
                String Query = matches.get(0);
                search.setText(Query);
                voices.setEnabled(false);

                String s=Query;

                if(s != null && !s.isEmpty())
                { search.setText(s);
                    s=s.toLowerCase();
                    userwish(s);
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void searchevent(final String searchitem){

    progressBar.setVisibility(View.VISIBLE);

    databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");
    j=listwish.size();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            String ad_no = dataSnapshot.child("tad_no").getValue(String.class);
            int x = Integer.parseInt(ad_no);
            x--;

            searchlist.clear();

            for(int i=x;i>0;i--) {

                int res=0;
                ad_no = Integer.toString(i);

                String wish_no="0";

                if(j>0)
                    wish_no=listwish.get(j-1);

                String adsold = dataSnapshot.child(ad_no).child("sold").getValue(String.class);
                String usid = dataSnapshot.child(ad_no).child("userid").getValue(String.class);

                if(adsold==null || adsold.equals("1"))
                    continue;

                if(fuser!=null)
                { if(userid.equals(usid))
                    continue;}


                String adtitle = dataSnapshot.child(ad_no).child("ad_title").getValue(String.class);
                assert adtitle != null;
                adtitle=adtitle.trim();
                adtitle=adtitle.toLowerCase();
                String model = dataSnapshot.child(ad_no).child("model").getValue(String.class);
                assert model != null;
                model=model.trim();
                model=model.toLowerCase();
                String include = dataSnapshot.child(ad_no).child("includes").getValue(String.class);
                String year = dataSnapshot.child(ad_no).child("year").getValue(String.class);
                String condition = dataSnapshot.child(ad_no).child("condition").getValue(String.class);
                String addetails = dataSnapshot.child(ad_no).child("ad_details").getValue(String.class);
                String price = dataSnapshot.child(ad_no).child("price").getValue(String.class);
                String category = dataSnapshot.child(ad_no).child("category").getValue(String.class);
                assert category != null;
                category=category.trim();
                category=category.toLowerCase();
                String sub_category = dataSnapshot.child(ad_no).child("sub_category").getValue(String.class);
                assert sub_category != null;
                sub_category=sub_category.trim();
                sub_category=sub_category.toLowerCase();
                String address = dataSnapshot.child(ad_no).child("address").getValue(String.class);
                String brand = dataSnapshot.child(ad_no).child("brand").getValue(String.class);
                assert brand != null;
                brand=brand.trim();
                brand=brand.toLowerCase();
                String email = dataSnapshot.child(ad_no).child("email_id").getValue(String.class);
                String mobile = dataSnapshot.child(ad_no).child("mobile").getValue(String.class);
                String name = dataSnapshot.child(ad_no).child("name").getValue(String.class);
                String image1 = dataSnapshot.child(ad_no).child("image1").getValue(String.class);
                String image2 = dataSnapshot.child(ad_no).child("image2").getValue(String.class);
                String image3 = dataSnapshot.child(ad_no).child("image3").getValue(String.class);
                String image4 = dataSnapshot.child(ad_no).child("image4").getValue(String.class);

                price = "₹ " + price;

                FireModel fire = new FireModel();

                fire.setPrice(price);
                fire.setTitle(adtitle);
                fire.setModel(model);
                fire.setIncludes(include);
                fire.setYear(year);
                fire.setCondition(condition);
                fire.setDetails(addetails);
                fire.setAddress(address);
                fire.setBrand(brand);
                fire.setEmail(email);
                fire.setMobile(mobile);
                fire.setName(name);
                fire.setUrl1(image1);
                fire.setUrl2(image2);
                fire.setUrl3(image3);
                fire.setUrl4(image4);
                fire.setAdno(ad_no);
                fire.setActivity("0");
                fire.setSold("0");
                fire.setUser(usid);

                if(wish_no.equals(ad_no) && j>0)
                {
                    j--;
                    fire.setWish("1");
                }

                else
                    fire.setWish("0");

                String[] splited = searchitem.split(" ");

                for (String ss : splited) {
                    boolean b = model.contains(ss);

                    if (b)
                        res = 1;
                }

                for (String ss : splited) {
                    boolean b = adtitle.contains(ss);

                    if (b)
                        res = 1;
                }

                for (String ss : splited) {
                    boolean b = brand.contains(ss);

                    if (b)
                        res = 1;
                }

                for (String ss : splited) {
                    boolean b = category.contains(ss);

                    if (b)
                        res = 1;
                }

                for (String ss : splited) {
                    boolean b = sub_category.contains(ss);

                    if (b)
                        res = 1;
                }

                if(res==1)
                searchlist.add(fire);

                progressBar.setVisibility(View.GONE);

            }
            searchresults();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

            progressBar.setVisibility(View.GONE);
        }
    });

}

    public void searchresults(){

        progressBar.setVisibility(View.VISIBLE);
        recycle.removeAllViewsInLayout();
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(searchlist,CompleteSearchActivity.this);
        RecyclerView.LayoutManager recyce = new GridLayoutManager(CompleteSearchActivity.this,1);
        recycle.setLayoutManager(recyce);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);

        progressBar.setVisibility(View.GONE);
    }

    public void userwish(final String sitem){

        progressBar.setVisibility(View.VISIBLE);

        if(fuser!=null)

        {   databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("wish");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    listwish.clear();
                    for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                        String advalue=dataSnapshot1.getValue(String.class);

                        listwish.add(advalue);

                    }
                    searchevent(sitem);
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    progressBar.setVisibility(View.GONE);
                }
            });
        }

        else
            searchevent(sitem);

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

        // register connection status listener
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
