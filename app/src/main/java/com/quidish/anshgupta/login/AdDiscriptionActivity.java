package com.quidish.anshgupta.login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.Home.HomeActivity;
import com.quidish.anshgupta.login.LoginRegister.LoginSignupactivity;
import com.quidish.anshgupta.login.Messaging.MessageActivity;
import com.quidish.anshgupta.login.MyAdsAndUserProfile.UserProfile;

import java.util.Objects;
import java.util.Random;

public class AdDiscriptionActivity extends AppCompatActivity {

    LinearLayout specification, description, spec_layout,userp;
    View dline, sline;
    TextView des_layout, spectext, destext,adtitle,price,username,email,profile;
    TextView address,brand,model,includes,year,condition;
    Button call,offer;
    String pic1="0",pic2="0",pic3="0",pic4="0",ad_no,activity;
    ImageView image1,image2,image3,image4;
    ImageButton wish,wishr;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth,myfba;
    FirebaseUser fuser;
    FirebaseDatabase firebaseDatabase;
    String wish_no="0";
    DatabaseReference ref;
    String uname;
    String userid,soldno,uid;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_ad);

        des_layout = findViewById(R.id.des_layout);
        spec_layout = findViewById(R.id.spec_layout);
        specification = findViewById(R.id.specifications);
        description = findViewById(R.id.description);
        dline = findViewById(R.id.descriptionline);
        sline = findViewById(R.id.specificationsline);
        destext = findViewById(R.id.destext);
        spectext = findViewById(R.id.spectext);
        call = findViewById(R.id.call);
        adtitle=findViewById(R.id.adtitle);
        price=findViewById(R.id.price);
        address=findViewById(R.id.address);
        brand=findViewById(R.id.brand);
        model=findViewById(R.id.model);
        includes=findViewById(R.id.includes);
        year=findViewById(R.id.year);
        condition=findViewById(R.id.condition);
        email=findViewById(R.id.email);
        username=findViewById(R.id.username);
        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);
        image4=findViewById(R.id.image4);
        wish=findViewById(R.id.wish);
        wishr=findViewById(R.id.wishr);
        profile=findViewById(R.id.profile_image);
        offer=findViewById(R.id.offer);
        userp=findViewById(R.id.userp);
        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);

        Random rand = new Random();
        int x=rand.nextInt(4);

        if(x==0)
            profile.setBackgroundResource(R.drawable.cir_borderimg);
        else if(x==1)
            profile.setBackgroundResource(R.drawable.cir_borderimg2);
        else if(x==2)
            profile.setBackgroundResource(R.drawable.cir_borderimg3);
        else if(x==3)
            profile.setBackgroundResource(R.drawable.cir_borderimg4);

        firebaseAuth = FirebaseAuth.getInstance();
        myfba= FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fuser = firebaseAuth.getCurrentUser();

        if(fuser!=null)
            userid=firebaseAuth.getCurrentUser().getUid();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.containsKey("msgad_no"))
            {
                ad_no=bundle.getString("msgad_no");
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                call.setVisibility(View.GONE);
                offer.setVisibility(View.GONE);
                addetailsset(ad_no);
                wishcheck();
            }

            else{

                ad_no=bundle.getString("ad_no");
                addetailsset(ad_no);
                wishcheck();

            }
        }

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_container);
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD);

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(adtitle.getText().toString());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        userp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(uid!=null){
                    Intent intent=new Intent(getApplicationContext(),UserProfile.class);
                    intent.putExtra("userid", uid);
                    startActivity(intent);
                }
            }
        });

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                intent.putExtra("pic",pic1);
                startActivity(intent);
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                intent.putExtra("pic",pic2);
                startActivity(intent);
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                intent.putExtra("pic",pic3);
                startActivity(intent);
            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                intent.putExtra("pic",pic4);
                startActivity(intent);
            }
        });

        specification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sline.setVisibility(View.VISIBLE);
                dline.setVisibility(View.GONE);
                des_layout.setVisibility(View.GONE);
                spec_layout.setVisibility(View.VISIBLE);

                destext.setTextColor(Color.BLACK);
                spectext.setTextColor(Color.rgb(35, 197, 160));
            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dline.setVisibility(View.VISIBLE);
                sline.setVisibility(View.GONE);
                spec_layout.setVisibility(View.GONE);
                des_layout.setVisibility(View.VISIBLE);

                spectext.setTextColor(Color.BLACK);
                destext.setTextColor(Color.rgb(35, 197, 160));
            }
        });

//        call.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(YourAdActivity.this);
//                builder.setMessage("Are you sure. You want to Call")
//                        .setCancelable(false)
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                Intent i = new Intent(Intent.ACTION_CALL);
//                                i.setData(Uri.parse("tel:" + mobile_no.getText().toString()));
//                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                                    return;
//                                }
//                                startActivity(i);
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
//
//            }
//        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser==null)
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(),MessageActivity.class);
                    intent.putExtra("ad_no",ad_no);
                    intent.putExtra("usertype","buyer");
                    intent.putExtra("act","1");
                    intent.putExtra("uid","0");
                    intent.putExtra("name",uname);
                    startActivity(intent);
                }
            }
        });

        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser==null)
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(),MessageActivity.class);
                    intent.putExtra("ad_no",ad_no);
                    intent.putExtra("usertype","buyer");
                    intent.putExtra("act","1");
                    intent.putExtra("uid","0");
                    intent.putExtra("name",uname);
                    intent.putExtra("offer","1");
                    startActivity(intent);
                }
            }
        });


        wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser==null)
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }

                else
                {
                    ref=FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("wish").child(ad_no);

                    wishr.setVisibility(View.VISIBLE);
                    wish.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
//                    Vibrator v = (Vibrator) getApplication().getSystemService(Context.VIBRATOR_SERVICE);
//                    assert v != null;
//                    v.vibrate(400);
                    HomeActivity.listwish.add(ad_no);
                    ref.setValue(ad_no);
                }

            }
        });

        wishr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser==null)
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }

                else
                {
                    ref=FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("wish").child(ad_no);

                    ref.setValue(null);
                    wish.setVisibility(View.VISIBLE);
                    wishr.setVisibility(View.GONE);
                    HomeActivity.listwish.remove(ad_no);
                }
            }
        });


    }

    public void addetailsset(String ad_no) {

        DatabaseReference refer;
        refer=firebaseDatabase.getReference().child("Ads").child(ad_no);
        refer.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adtitle.setText(dataSnapshot.child("ad_title").getValue(String.class));
                price.setText("₹ "+dataSnapshot.child("price").getValue(String.class));
                address.setText(dataSnapshot.child("address").getValue(String.class));
                brand.setText(dataSnapshot.child("brand").getValue(String.class));
                model.setText(dataSnapshot.child("model").getValue(String.class));
                includes.setText(dataSnapshot.child("includes").getValue(String.class));
                year.setText(dataSnapshot.child("year").getValue(String.class));
                condition.setText(dataSnapshot.child("condition").getValue(String.class));
                email.setText(dataSnapshot.child("email_id").getValue(String.class));
                username.setText(dataSnapshot.child("name").getValue(String.class));
                profile.setText(dataSnapshot.child("name").getValue(String.class).substring(0,1));
                des_layout.setText(dataSnapshot.child("ad_details").getValue(String.class));
                pic1=dataSnapshot.child("image1").getValue(String.class);
                uname=dataSnapshot.child("name").getValue(String.class);
                pic2=dataSnapshot.child("image2").getValue(String.class);
                pic3=dataSnapshot.child("image3").getValue(String.class);
                pic4=dataSnapshot.child("image4").getValue(String.class);
                uid = dataSnapshot.child("userid").getValue(String.class);
                soldno=dataSnapshot.child("sold").getValue(String.class);

                if(soldno!=null && soldno.equals("1"))
                {
                    call.setVisibility(View.GONE);
                    offer.setVisibility(View.GONE);
                }

                setdata();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }

    public void wishcheck(){

        for(int i=0;i<HomeActivity.listwish.size();i++)
        {
            if(HomeActivity.listwish.get(i).equals(ad_no))
            {
                wish_no="1";
                break;
            }
        }

        if(wish_no.equals("1"))
        {
            wishr.setVisibility(View.VISIBLE);
            wish.setVisibility(View.GONE);
        }

        else
        {
            wish.setVisibility(View.VISIBLE);
            wishr.setVisibility(View.GONE);
        }
    }

    public void setdata(){

        Glide.with(getApplicationContext()).load(pic1).into(image1);

        if(!pic2.equals("0"))
        {
            image2.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(pic2).into(image2);
        }
        if(!pic3.equals("0"))
        {
            image3.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(pic3).into(image3);
        }
        if(!pic4.equals("0"))
        {
            image4.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(pic4).into(image4);
        }

        if(wish_no.equals("1"))
        {
            wishr.setVisibility(View.VISIBLE);
            wish.setVisibility(View.GONE);
        }

        else
        {
            wish.setVisibility(View.VISIBLE);
            wishr.setVisibility(View.GONE);
        }

        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}


