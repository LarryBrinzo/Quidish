package com.quidish.anshgupta.login.Home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.Query;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.Home.Location.GetUsersLocationActivity;
import com.quidish.anshgupta.login.Home.Searching.CompleteSearchActivity;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.LoginRegister.LoginSignupactivity;
import com.quidish.anshgupta.login.MyAdsAndUserProfile.MyAdsActivity;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Messaging.MyChatActivity;
import com.quidish.anshgupta.login.MyAccount.MyWishlistActivity;
import com.quidish.anshgupta.login.MyAccount.MyaccountActivity;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.PostYourAd.PostAd.SelectPictureActivity;
import com.quidish.anshgupta.login.R;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import static java.lang.Math.max;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ConnectivityReceiver.ConnectivityReceiverListener {

    FirebaseAuth myfba;
    DrawerLayout drawer;
    ImageButton drawtog,wishlist,voices;
    private SliderLayout mDemoSlider;
    FloatingActionButton sell;
    ValueEventListener eventListener;
    static public String userid,mapdate,userclg="0";
    FirebaseDatabase firebaseDatabase;
    NavigationView navigationView;
    NestedScrollView scrollView;
    static public FirebaseUser fuser;
    HashMap<String,Integer> url_maps = new HashMap<>();
    NotificationChannel mChannel;
    NotificationManager notifManager;
    DatabaseReference databaseReference;
    ImageView hideimg;
    LinearLayout location;
    PagerIndicator pagerIndicator;
    private static final int REQUEST_CODE = 1234;
    TextView useremail,uclg;
    List<AdModel> listBooks=new ArrayList<>();
    public static List<AdModel> listAll=new ArrayList<>();
    EditText search;
    public static List<String> listwish=new ArrayList<>();
    int fblog=0;
    int sclrct=0,notify=0;
    int tunreadmsg=0,scroll=1;
    ProgressBar progbar;
    private int oldScrollYPostion = 0;
    RecyclerView recycle,horzontalrecycle;
    long adcount=0,adstart,adend,itm=0;
    UserAdsAdapter recyclerAdapterAll;
    CategoryAdapter recyclerAdapterBooks;
    ShimmerFrameLayout container;



    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        checkConnection();

        drawer=findViewById(R.id.drawer_layout);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle =findViewById(R.id.recycle);
        recycle.setNestedScrollingEnabled(false);
        sell=findViewById(R.id.sell);
        hideimg=findViewById(R.id.hideimg);
        search=findViewById(R.id.search);
        drawtog=findViewById(R.id.drawtog);
        location=findViewById(R.id.location);
        wishlist=findViewById(R.id.wishlist);
        voices=findViewById(R.id.voice);
        scrollView=findViewById(R.id.scrollView2);
        mDemoSlider =findViewById(R.id.slider);
        pagerIndicator =  findViewById(R.id.banner);
        progbar=findViewById(R.id.prog);
        horzontalrecycle=findViewById(R.id.horzontalrecycle);

        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();

        recyclerAdapterAll = new UserAdsAdapter(listAll,HomeActivity.this);
        RecyclerView.LayoutManager recyceAll = new GridLayoutManager(HomeActivity.this,2);
        recycle.setLayoutManager(recyceAll);
        recyceAll.setAutoMeasureEnabled(false);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapterAll);

        recyclerAdapterBooks = new CategoryAdapter(listBooks,HomeActivity.this);
        RecyclerView.LayoutManager recycebooks = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        horzontalrecycle.setLayoutManager(recycebooks);
        horzontalrecycle.setLayoutManager(recycebooks);
        horzontalrecycle.setAdapter(recyclerAdapterBooks);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                adcount=dataSnapshot.getChildrenCount();
                adstart=adcount-1;
                adend=max(adstart-29,1);
                userwish();

                addbooksad();
                Adtraversal();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        hideimg.setVisibility(View.VISIBLE);

        ImagesfromDatabase();

        search.setText("");

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),GetUsersLocationActivity.class);
                startActivity(intent);

            }
        });

        if(fuser!=null)
        {
            userid=myfba.getCurrentUser().getUid();
            NavigationView navigation = findViewById(R.id.nav_view);
            Menu nav_Menu = navigation.getMenu();
            DatabaseReference current_user;
            current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("lastseen");
            current_user.setValue("Online");
            nav_Menu.findItem(R.id.drawlogout).setVisible(true);
        }

        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            fblog=1;
        }

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView.getScrollY() > oldScrollYPostion) {
                    sell.hide();
                } else if (scrollView.getScrollY() < oldScrollYPostion || scrollView.getScrollY() <= 0) {
                    sell.show();
                }
                oldScrollYPostion = scrollView.getScrollY();
            }
        });


        navigationView=findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        MenuItem tools= menu.findItem(R.id.drawhome);
        SpannableString st = new SpannableString(tools.getTitle());
        st.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, st.length(), 0);
        tools.setTitle(st);

        tools= menu.findItem(R.id.drawbooks);
        st = new SpannableString(tools.getTitle());
        st.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, st.length(), 0);
        tools.setTitle(st);

        tools= menu.findItem(R.id.drawcycle);
        st = new SpannableString(tools.getTitle());
        st.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, st.length(), 0);
        tools.setTitle(st);

        tools= menu.findItem(R.id.drawelec);
        st = new SpannableString(tools.getTitle());
        st.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, st.length(), 0);
        tools.setTitle(st);

        tools= menu.findItem(R.id.drawmobile);
        st = new SpannableString(tools.getTitle());
        st.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, st.length(), 0);
        tools.setTitle(st);

        navigationView.setNavigationItemSelectedListener(this);

        View headview =navigationView.getHeaderView(0);
        useremail=headview.findViewById(R.id.useremail);

        String s="Login / Sign up";
        useremail.setText(s);

        linkaccount();

//        if(fuser!=null) {
//
//            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("Allmsg");
//
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @SuppressLint("WrongConstant")
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    tunreadmsg = 0;
//
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//
//                        if (dataSnapshot1.hasChild("msgno")) {
//                            String x = dataSnapshot1.child("msgno").getValue(String.class);
//
//                            if (x != null) {
//                                int addv = Integer.parseInt(x);
//                                tunreadmsg += addv;
//                            }
//
//                        }
//
//                    }
//
//                    if (tunreadmsg > 0 && MessageActivity.work==2) {
//
//                        if (notifManager == null) {
//                            notifManager = (NotificationManager) getSystemService
//                                    (Context.NOTIFICATION_SERVICE);
//                        }
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            NotificationCompat.Builder builder;
//                            Intent intent = new Intent(getApplicationContext(), MyChatActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            PendingIntent pendingIntent;
//                            int importance = NotificationManager.IMPORTANCE_HIGH;
//                            if (mChannel == null) {
//                                mChannel = new NotificationChannel
//                                        ("0", "Unread Messages", importance);
//                                mChannel.setDescription("You have " + Integer.toString(tunreadmsg) + " unread Messages");
//                                mChannel.enableVibration(true);
//                                notifManager.createNotificationChannel(mChannel);
//                            }
//                            builder = new NotificationCompat.Builder(HomeActivity.this, "0");
//
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            pendingIntent = PendingIntent.getActivity(HomeActivity.this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
//                            builder.setContentTitle("Unread Messages")
//                                    .setSmallIcon(R.drawable.quid2) // required
//                                    .setContentText("You have " + Integer.toString(tunreadmsg) + " unread Messages")  // required
//                                    .setDefaults(Notification.DEFAULT_ALL)
//                                    .setAutoCancel(true)
//                                    .setLargeIcon(BitmapFactory.decodeResource
//                                            (getResources(), R.drawable.quid2))
//                                    .setBadgeIconType(R.drawable.quid2)
//                                    .setContentIntent(pendingIntent)
//                                    .setSound(RingtoneManager.getDefaultUri
//                                            (RingtoneManager.TYPE_NOTIFICATION));
//                            Notification notification = builder.build();
//                            notifManager.notify(0, notification);
//                        } else {
//
//                            Intent intent = new Intent(getApplicationContext(), MyChatActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            PendingIntent pendingIntent = null;
//
//                            pendingIntent = PendingIntent.getActivity(HomeActivity.this, 0, intent, 0);
//
//                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(HomeActivity.this)
//                                    .setContentTitle("Unread Messages")
//                                    .setContentText("You have " + Integer.toString(tunreadmsg) + " unread Messages")
//                                    .setAutoCancel(true)
//                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))
//                                    .setSound(defaultSoundUri)
//                                    .setSmallIcon(R.drawable.quid2)
//                                    .setContentIntent(pendingIntent)
//                                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle("Unread Messages").bigText("You have " + Integer.toString(tunreadmsg) + " unread Messages"));
//
//                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                            assert notificationManager != null;
//                            notificationManager.notify(0, notificationBuilder.build());
//                        }
//
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }


        headview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser!=null)
                {
                    Intent intent=new Intent(getApplicationContext(),MyaccountActivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }, 10);


            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectPictureActivity.Selectedimagesno.clear();
                SelectPictureActivity.Selectedpos.clear();
                SelectPictureActivity.SelectedFilepath.clear();
                SelectPictureActivity.Allimages.clear();
                SelectPictureActivity.imageno=1;

                Intent intent=new Intent(getApplicationContext(),SelectPictureActivity.class);
                intent.putExtra("start","1");
                startActivity(intent);

//                if(fuser==null)
//                {
//                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
//                    startActivity(intent);
//                }
//
//                else{
//                    Intent intent=new Intent(getApplicationContext(),CategoryActivity.class);
//                    startActivity(intent);
//                }
            }
        });

        drawtog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser!=null)
                {
                    Intent intent=new Intent(getApplicationContext(),MyWishlistActivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }
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

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),CompleteSearchActivity.class);
                intent.putExtra("active", "0");
                startActivity(intent);
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
                String  Query = matches.get(0);
                Intent intent=new Intent(getApplicationContext(),CompleteSearchActivity.class);
                intent.putExtra("active", "1");
                intent.putExtra("searchitem", Query);
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void ImagesfromDatabase(){

        url_maps.put("1", R.drawable.profback);
        url_maps.put("2", R.drawable.profback);
        url_maps.put("3", R.drawable.drawerback);
        url_maps.put("4", R.drawable.profback);

        setUpSliderLayout();
    }

    public void linkaccount() {

        if(fuser==null)
            return;

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String user_email = dataSnapshot.child("users").child(userid).child("email").getValue(String.class);
                useremail.setText(user_email);

                if(user_email==null || user_email.equals(""))
                {
                    String name = dataSnapshot.child("users").child(userid).child("username").getValue(String.class);
                    useremail.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void onBackPressed() {
        backButtonHandler();
    }

    public void backButtonHandler() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else
        {
            if(fuser!=null){

                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm:ss");
                String strDate =mdformat.format(calendar.getTime());

                if (calendar.get(Calendar.AM_PM) == 0) {
                    strDate+=" AM";
                } else {
                    strDate+=" PM";
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String date = df.format(Calendar.getInstance().getTime());

                date+=" ";
                date+=strDate;

                mapdate=date;

                DatabaseReference current_user;
                current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("lastseen");
                current_user.setValue(mapdate);

            }

            finishAffinity();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.drawhome:
            {
                //homelist();
                break;
            }
            case R.id.drawbooks:
            {
                //booklist();
                break;}
            case R.id.drawcycle:
            {
                //cyclelist();
                break;}
            case R.id.drawmobile:
            {
                //mobilelist();
                break;}
            case R.id.drawelec:
            {
                //electronicslist();
                break;}
            case R.id.drawwish:
            {
                if(fuser!=null)
                {
                    Intent intent=new Intent(getApplicationContext(),MyWishlistActivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);

                }

                break;
            }
            case R.id.drawmyad:
            {

                if(fuser!=null)
                {
                    Intent intent=new Intent(getApplicationContext(),MyAdsActivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }

                break;
            }

            case R.id.chat:
            {

                if(fuser!=null)
                {
                    Intent intent=new Intent(getApplicationContext(),MyChatActivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }
                break;
            }

            case R.id.drawacc:
            {
                if(fuser!=null)
                {
                    Intent intent=new Intent(getApplicationContext(),MyaccountActivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }
                break;
            }

            case R.id.rateus:
            {
                Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                openURL.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.quidish.anshgupta.login"));
                startActivity(openURL);
                break;
            }

            case R.id.contactus:
            {
                Intent intent=new Intent(getApplicationContext(),ContactUsActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.drawlogout:
            {
                if(fuser!=null)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("Are you sure you want to logout your account from this device.")
                            .setCancelable(false)
                            .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    myfba.signOut();
                                    if(fblog==1)
                                        LoginManager.getInstance().logOut();

                                    Calendar calendar = Calendar.getInstance();
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm:ss");
                                    String strDate =mdformat.format(calendar.getTime());

                                    if (calendar.get(Calendar.AM_PM) == 0) {
                                        strDate+=" AM";
                                    } else {
                                        strDate+=" PM";
                                    }

                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                    String date = df.format(Calendar.getInstance().getTime());

                                    date+=" ";
                                    date+=strDate;

                                    mapdate=date;

                                    DatabaseReference current_user;
                                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("lastseen");
                                    current_user.setValue(mapdate);

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                                            startActivity(intent);
                                            NavigationView navigation =  findViewById(R.id.nav_view);
                                            Menu nav_Menu = navigation.getMenu();
                                            nav_Menu.findItem(R.id.drawlogout).setVisible(false);
                                        }
                                    }, 50);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                    break;
                }

                else
                {
                    NavigationView navigation =findViewById(R.id.nav_view);
                    Menu nav_Menu = navigation.getMenu();
                    nav_Menu.findItem(R.id.drawlogout).setVisible(false);
                    Toast.makeText(getApplicationContext(),"You are not Logged in",Toast.LENGTH_LONG).show();
                }
            }

        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawer(GravityCompat.START);
            }
        }, 10);
        return false;
    }












    public void addproductad(final long start){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");

//         childListener=databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                String ad_no=dataSnapshot.getKey();
//
//                String adinst = dataSnapshot.child("institute").getValue(String.class);
//                String adsold = dataSnapshot.child("sold").getValue(String.class);
//                String usid = dataSnapshot.child("userid").getValue(String.class);
//
//                if (adsold == null || adsold.equals("1")){}
//
//                else if (fuser != null) {
//                    if (userid.equals(usid)){}
//                }
//
//                else if (userclg != null && !userclg.equals("") && !userclg.equals("0") && !userclg.equals(adinst)) {}
//
//                else{
//
//                    itm++;
//
//                    if(itm>=20)
//                    {
//                        FirebaseDatabase.getInstance().getReference().child("Ads").removeEventListener(childListener);
//                        itm=0;
//                    }
//
//                    String adtitle = dataSnapshot.child("ad_title").getValue(String.class);
//                    String model = dataSnapshot.child("model").getValue(String.class);
//                    String include = dataSnapshot.child("includes").getValue(String.class);
//                    String year = dataSnapshot.child("year").getValue(String.class);
//                    String condition = dataSnapshot.child("condition").getValue(String.class);
//                    String addetails = dataSnapshot.child("ad_details").getValue(String.class);
//                    String price = dataSnapshot.child("price").getValue(String.class);
//                    String address = dataSnapshot.child("address").getValue(String.class);
//                    String brand = dataSnapshot.child("brand").getValue(String.class);
//                    String email = dataSnapshot.child("email_id").getValue(String.class);
//                    String mobile = dataSnapshot.child("mobile").getValue(String.class);
//                    String name = dataSnapshot.child("name").getValue(String.class);
//                    String image1 = dataSnapshot.child("image1").getValue(String.class);
//                    String image2 = dataSnapshot.child("image2").getValue(String.class);
//                    String image3 = dataSnapshot.child("image3").getValue(String.class);
//                    String image4 = dataSnapshot.child("image4").getValue(String.class);
//
//                    price = "₹ " + price;
//
//                    AdModel fire = new AdModel();
//
//                    fire.setPrice(price);
//                    fire.setTitle(adtitle);
//                    fire.setModel(model);
//                    fire.setIncludes(include);
//                    fire.setYear(year);
//                    fire.setCondition(condition);
//                    fire.setDetails(addetails);
//                    fire.setAddress(address);
//                    fire.setBrand(brand);
//                    fire.setEmail(email);
//                    fire.setMobile(mobile);
//                    fire.setName(name);
//                    fire.setUrl1(image1);
//                    fire.setUrl2(image2);
//                    fire.setUrl3(image3);
//                    fire.setUrl4(image4);
//                    fire.setAdno(ad_no);
//                    fire.setActivity("2");
//                    fire.setSold("0");
//                    fire.setUser(usid);
//                    fire.setWish("0");
//
//                    listhome2.add(0,fire);
//                    if(listhome2.size()==1){
//                        recyclerAdapterbooks.notifyDataSetChanged();
//                    } else {
//
//                        recyclerAdapterbooks.notifyItemInserted(0);
//
//                    }
//
//                    //recyclerAdapterbooks.notifyItemInserted(listhome2.size() - 1);
//                    Adshowbooks();
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
         databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for(long i=start;i>=adend;i--) {

                    String ad_no=Long.toString(i);

                    itm++;

                    String adinst = dataSnapshot.child(ad_no).child("institute").getValue(String.class);
                    String adsold = dataSnapshot.child(ad_no).child("sold").getValue(String.class);
                    String usid = dataSnapshot.child(ad_no).child("userid").getValue(String.class);

                    if (adsold==null || adsold.equals("1"))
                    { if(adend>0){
                        adend--; } }

                    else if (fuser != null && userid.equals(usid)) {
                        if(adend>0){
                            adend--;
                            } }

//                    else if (userclg != null && !userclg.equals("") && !userclg.equals("0") && !userclg.equals(adinst))
//                    { if(adend>0){
//                        adend--;
//                        } }
//                 DatabaseReference current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(ad_no).child("posted");
//                 current_user.setValue("1");
                    else{
                        ad_no=Long.toString(i);
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
                    fire.setUrl1(image1);
                    fire.setActivity("2");
                    fire.setSold("0");
                    fire.setUser(usid);

                    if(listwish.contains(ad_no))
                        fire.setWish("1");

                    else
                        fire.setWish("0");

                        listAll.add(fire);

                    if(notify==1)
                        recyclerAdapterAll.notifyItemInserted(listAll.size() - 1);

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



    public void Adtraversal(){

        progbar.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adend=max(adstart-49,1);
                addproductad(adstart);
                progbar.setVisibility(View.GONE);
            }
        }, 2000);


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = scrollView.getChildAt(scrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView
                        .getScrollY()));

                if (diff == 0 && sclrct==1 && itm<adcount && scroll==1) {

                    scroll=0;
                    notify=1;

                    progbar.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adend=max(adstart-25,1);
                            addproductad(adstart);

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
                    if (sclrct==1 && itm<adcount && scroll==0) {
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

//    public void Adshowhome(){
//        // recycle.removeAllViewsInLayout();
//
//        UserAdsAdapter recyclerAdapter = new UserAdsAdapter(listhome,HomeActivity.this);
//        RecyclerView.LayoutManager recyce = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
//        recycle.setLayoutManager(recyce);
//        //recyce.setAutoMeasureEnabled(false);
    //nnerRv.setRecycledViewPool(mSharedPool);
//        recycle.setLayoutManager(recyce);
//        recycle.setAdapter(recyclerAdapter);
//
//        prog.setVisibility(View.GONE);
//    }
//
//    public void Adshowbooks(){
//        //recycle.removeAllViewsInLayout();
//
//        // recyclerAdapterbooks = new UserAdsAdapter(listhome2,HomeActivity.this);
//        RecyclerView.LayoutManager recyce = new GridLayoutManager(HomeActivity.this,2);
//        recycle.setLayoutManager(recyce);
//        //recyce.setAutoMeasureEnabled(false);
//        recycle.setItemAnimator( new DefaultItemAnimator());
//        recycle.setAdapter(recyclerAdapterbooks);
//
//        prog.setVisibility(View.GONE);
//    }
//
//    public void Adshowcycle(){
//        //recycle.removeAllViewsInLayout();
//
//        RecyclerView.LayoutManager recyce = new GridLayoutManager(Activity3.this,1);
//        recycle.setLayoutManager(recyce);
//        //recyce.setAutoMeasureEnabled(false);
//        recycle.setItemAnimator( new DefaultItemAnimator());
//        recycle.setAdapter(recyclerAdaptercycle);
//
//        prog.setVisibility(View.GONE);
//    }
//
//    public void Adshowmobile(){
//       // recycle.removeAllViewsInLayout();
//
//        RecyclerView.LayoutManager recyce = new GridLayoutManager(Activity3.this,1);
//        recycle.setLayoutManager(recyce);
//        //recyce.setAutoMeasureEnabled(false);
//        recycle.setItemAnimator( new DefaultItemAnimator());
//        recycle.setAdapter(recyclerAdaptermobile);
//
//        prog.setVisibility(View.GONE);
//    }
//
//    public void Adshowelectronics(){
//
//        RecyclerView.LayoutManager recyce = new GridLayoutManager(Activity3.this,1);
//        //recyce.setAutoMeasureEnabled(false);
//        recycle.setLayoutManager(recyce);
//        recycle.setItemAnimator( new DefaultItemAnimator());
//        recycle.setAdapter(recyclerAdapterelect);
//
//        prog.setVisibility(View.GONE);
//    }

    public void userwish(){

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
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

    }


    public void addbooksad(){

        final DatabaseReference booksreference = FirebaseDatabase.getInstance().getReference().child("Ads");
        Query query = booksreference.orderByChild("category").equalTo("   Books & Stationery");

        eventListener=query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                int bkct=0;

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()) {

                    String adinst = dataSnapshot1.child("institute").getValue(String.class);
                    String adsold = dataSnapshot1.child("sold").getValue(String.class);
                    String usid = dataSnapshot1.child("userid").getValue(String.class);

                    if (adsold == null || adsold.equals("1")){}

                    else if (fuser != null && userid.equals(usid)) {
                    }

                   // else if (userclg != null && !userclg.equals("") && !userclg.equals("0") && !userclg.equals(adinst)){}

                    else{

                        bkct++;

                        if(bkct==6)
                            break;

                        String adtitle = dataSnapshot1.child("ad_title").getValue(String.class);
                        String price = dataSnapshot1.child("price").getValue(String.class);
                        String brand = dataSnapshot1.child("brand").getValue(String.class);
                        String image1 = dataSnapshot1.child("image1").getValue(String.class);
                        String ad_no=dataSnapshot1.getKey();

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

                        if(listwish.contains(ad_no))
                            fire.setWish("1");

                        else
                            fire.setWish("0");

                        listBooks.add(fire);

                    }

                    recyclerAdapterBooks.notifyDataSetChanged();
                }

                container.setVisibility(View.GONE);
                container.stopShimmer();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                container.setVisibility(View.GONE);
                container.stopShimmer();
            }
        });

    }


    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
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

//    public void storefinal(int adno){
//
//        Map<String,String> newuser=new HashMap<>();
//
//        String brand_s="Google";
//        String model_s="Home mini";
//        String include_s="";
//        String year_s="2018";
//        String condition_s="good";
//        String address_s="H-13";
//        String addetails_s="Google home mini charcoal colour";
//        String price_s="3000";
//        String adtitle_s="Google home mini";
//        String postcat_s="";
//        String postsubcat_s="";
//        String email_s="";
//        String mobile_s="";
//        String name_s="";
//        String userid="";
//        String inst="";
//
//        newuser.put("address",address_s);
//        newuser.put("brand",brand_s);
//        newuser.put("ad_title",adtitle_s);
//        newuser.put("ad_details",addetails_s);
//        newuser.put("year",year_s);
//        newuser.put("includes",include_s);
//        newuser.put("condition",condition_s);
//        newuser.put("model",model_s);
//        newuser.put("price",price_s);
//        newuser.put("category",postcat_s);
//        newuser.put("sub_category",postsubcat_s);
//        newuser.put("email_id",email_s);
//        newuser.put("mobile",mobile_s);
//        newuser.put("name",name_s);
//        newuser.put("verified","0");
//        newuser.put("sold","0");
//        newuser.put("posted","0");
//        newuser.put("image1","https://firebasestorage.googleapis.com/v0/b/myproject-2872a.appspot.com/o/All%20Ads%2F1%2Fimage1.jpg?alt=media&token=2b186064-cb46-4a4e-a7f8-a3f2985a8cd1");
//        newuser.put("image2","0");
//        newuser.put("image3","0");
//        newuser.put("image4","0");
//        newuser.put("userid",userid);
//        newuser.put("institute",inst);
//
//        DatabaseReference current_user;
//
//        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(Integer.toString(adno));
//        current_user.setValue(newuser);
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
        mDemoSlider.startAutoCycle();
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        userclg=pref.getString("userinstitute", null);

        uclg=findViewById(R.id.uclg);
        uclg.setText(userclg);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDemoSlider.stopAutoCycle();
        container.setVisibility(View.GONE);
        container.stopShimmer();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDemoSlider.stopAutoCycle();
        container.setVisibility(View.GONE);
        container.stopShimmer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return true;
    }


    private void setUpSliderLayout() {

        for(String name : url_maps.keySet()){

            DefaultSliderView defaultSliderView = new DefaultSliderView(getApplicationContext());

            defaultSliderView
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            mDemoSlider.addSlider(defaultSliderView);
            mDemoSlider.setCustomIndicator(pagerIndicator);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setDuration(6000);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideimg.setVisibility(View.GONE);
            }
        }, 400);

    }

}