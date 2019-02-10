package com.quidish.anshgupta.login.Home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.quidish.anshgupta.login.PostYourAd.CategoryActivity;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.LoginRegister.LoginSignupactivity;
import com.quidish.anshgupta.login.Messaging.MessageActivity;
import com.quidish.anshgupta.login.MyAdsAndUserProfile.MyAdsActivity;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Messaging.MyChatActivity;
import com.quidish.anshgupta.login.MyAccount.MyWishlistActivity;
import com.quidish.anshgupta.login.MyAccount.MyaccountActivity;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;
import com.viewpagerindicator.CirclePageIndicator;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ConnectivityReceiver.ConnectivityReceiverListener {

    LinearLayout homel,bookl,cyclel,mobilel,electronicsl;
    FirebaseAuth myfba;
    DrawerLayout drawer;
    TextView home,electronics,mobile,cycle,book;
    ImageButton drawtog,wishlist,voices;
    FloatingActionButton sell;
    String userid,mapdate,userclg="0";
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    FirebaseDatabase firebaseDatabase;
    View homeline,bookline,cycleline,mobileline,electronicsline;
    NavigationView navigationView;
    StorageReference mstoragrref;
    HorizontalScrollView MyScrollView;
    SliderLayout sliderLayout;
    NestedScrollView scrollView;
    FirebaseUser fuser;
    //RecyclerAdapter recyclerAdapter,recyclerAdapterbooks,recyclerAdaptercycle,recyclerAdaptermobile,recyclerAdapterelect;
    NotificationChannel mChannel;
    NotificationManager notifManager;
    DatabaseReference databaseReference,reference;
    private ArrayList<String> ImagesArray = new ArrayList<>();
    ProgressBar prog;
    private static final int REQUEST_CODE = 1234;
    TextView useremail;
    EditText search;
//    List<FireModel> listhome=new ArrayList<>();
//    List<FireModel> listbooks=new ArrayList<>();
//    List<FireModel> listcycle=new ArrayList<>();
//    List<FireModel> listmobile=new ArrayList<>();
//    List<FireModel> listelectronics=new ArrayList<>();
    public static List<String> listwish=new ArrayList<>();
//    RecyclerView recycle;
    int j,homecat=1;
    int fblog=0;
    int tunreadmsg=0;
    private int oldScrollYPostion = 0;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }


    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_4);
        checkConnection();

        drawer=findViewById(R.id.drawer_layout);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        //recycle =findViewById(R.id.recycle);

        //recycle.setNestedScrollingEnabled(false);

        home=findViewById(R.id.home);
        electronics=findViewById(R.id.electronics);
        mobile=findViewById(R.id.mobile);
        cycle=findViewById(R.id.cycle);

        book=findViewById(R.id.books);
        sell=findViewById(R.id.sell);
        homel=findViewById(R.id.homel);
        prog=findViewById(R.id.progbar);
        bookl=findViewById(R.id.bookl);
        cyclel=findViewById(R.id.cyclel);
        mobilel=findViewById(R.id.mobilel);
        electronicsl=findViewById(R.id.electronicsl);
        homeline=findViewById(R.id.homeline);
        bookline=findViewById(R.id.bookline);
        cycleline=findViewById(R.id.cycleline);
        mobileline=findViewById(R.id.mobileline);
        electronicsline=findViewById(R.id.electronicsline);
        search=findViewById(R.id.search);
        drawtog=findViewById(R.id.drawtog);
        wishlist=findViewById(R.id.wishlist);
        voices=findViewById(R.id.voice);
        MyScrollView=findViewById(R.id.horizontalScrollView);
        sliderLayout=findViewById(R.id.homeSliderLayout);
        scrollView=findViewById(R.id.scrollView2);

//        recyclerAdapter = new RecyclerAdapter(listhome,Activity3.this);
//        recyclerAdapterbooks = new RecyclerAdapter(listbooks,Activity3.this);
//        recyclerAdaptermobile = new RecyclerAdapter(listmobile,Activity3.this);
//        recyclerAdaptercycle = new RecyclerAdapter(listcycle,Activity3.this);
//        recyclerAdapterelect = new RecyclerAdapter(listelectronics,Activity3.this);

        ImagesfromDatabase();
        search.setText("");

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

        mstoragrref= FirebaseStorage.getInstance().getReference();

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
       // progressDialog.dismiss();
        prog.setVisibility(View.GONE);


        if(fuser!=null) {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("Allmsg");

            ValueEventListener valueEventListener;
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("WrongConstant")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    tunreadmsg = 0;

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        if (dataSnapshot1.hasChild("msgno")) {
                            String x = dataSnapshot1.child("msgno").getValue(String.class);

                            if (x != null) {
                                int addv = Integer.parseInt(x);
                                tunreadmsg += addv;
                            }

                        }

                    }

                    if (tunreadmsg > 0 && MessageActivity.work==2) {

                        if (notifManager == null) {
                            notifManager = (NotificationManager) getSystemService
                                    (Context.NOTIFICATION_SERVICE);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationCompat.Builder builder;
                            Intent intent = new Intent(getApplicationContext(), MyChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent;
                            int importance = NotificationManager.IMPORTANCE_HIGH;
                            if (mChannel == null) {
                                mChannel = new NotificationChannel
                                        ("0", "Unread Messages", importance);
                                mChannel.setDescription("You have " + Integer.toString(tunreadmsg) + " unread Messages");
                                mChannel.enableVibration(true);
                                notifManager.createNotificationChannel(mChannel);
                            }
                            builder = new NotificationCompat.Builder(HomeActivity.this, "0");

                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            pendingIntent = PendingIntent.getActivity(HomeActivity.this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
                            builder.setContentTitle("Unread Messages")
                                    .setSmallIcon(R.drawable.quid2) // required
                                    .setContentText("You have " + Integer.toString(tunreadmsg) + " unread Messages")  // required
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setAutoCancel(true)
                                    .setLargeIcon(BitmapFactory.decodeResource
                                            (getResources(), R.drawable.quid2))
                                    .setBadgeIconType(R.drawable.quid2)
                                    .setContentIntent(pendingIntent)
                                    .setSound(RingtoneManager.getDefaultUri
                                            (RingtoneManager.TYPE_NOTIFICATION));
                            Notification notification = builder.build();
                            notifManager.notify(0, notification);
                        } else {

                            Intent intent = new Intent(getApplicationContext(), MyChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent = null;

                            pendingIntent = PendingIntent.getActivity(HomeActivity.this, 0, intent, 0);

                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(HomeActivity.this)
                                    .setContentTitle("Unread Messages")
                                    .setContentText("You have " + Integer.toString(tunreadmsg) + " unread Messages")
                                    .setAutoCancel(true)
                                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))
                                    .setSound(defaultSoundUri)
                                    .setSmallIcon(R.drawable.quid2)
                                    .setContentIntent(pendingIntent)
                                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle("Unread Messages").bigText("You have " + Integer.toString(tunreadmsg) + " unread Messages"));

                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            assert notificationManager != null;
                            notificationManager.notify(0, notificationBuilder.build());
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        intializelay();

        headview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prog.setVisibility(View.GONE);
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

                        prog.setVisibility(View.GONE);
                        if(fuser==null)
                        {
                            Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
                            startActivity(intent);
                        }

                        else{
                        Intent intent=new Intent(getApplicationContext(),CategoryActivity.class);
                        startActivity(intent);
                        }
                    }
                });

        drawtog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prog.setVisibility(View.GONE);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // progressDialog.dismiss();
                prog.setVisibility(View.GONE);
                if(fuser!=null)
                {
                   // progressDialog.dismiss();
                    prog.setVisibility(View.GONE);
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
               // progressDialog.dismiss();
                prog.setVisibility(View.GONE);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //progressDialog.dismiss();
                prog.setVisibility(View.GONE);
                Intent intent=new Intent(getApplicationContext(),CompleteSearchActivity.class);
                intent.putExtra("active", "0");
                startActivity(intent);
            }
        });





       // userwish();



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

        databaseReference = FirebaseDatabase.getInstance().getReference().child("SlidingImages");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                   ImagesArray.add(dataSnapshot1.getValue(String.class));
                }

                setUpSliderLayout();
                prog.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                prog.setVisibility(View.GONE);
            }
        });

    }

    public void linkaccount() {

        prog.setVisibility(View.VISIBLE);

        if(fuser==null)
        {
            prog.setVisibility(View.GONE);
            return;
        }

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

                userclg=dataSnapshot.child("users").child(userid).child("institute").getValue(String.class);

                prog.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                prog.setVisibility(View.GONE);
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

    public void homelist(){
        homecat=1;

        prog.setVisibility(View.VISIBLE);

        mobileline.setVisibility(View.GONE);
        electronicsline.setVisibility(View.GONE);
        homeline.setVisibility(View.VISIBLE);
        bookline.setVisibility(View.GONE);
        cycleline.setVisibility(View.GONE);

        home.setTextColor(Color.WHITE);
        mobile.setTextColor(Color.rgb(132,141,150));
        electronics.setTextColor(Color.rgb(132,141,150));
        cycle.setTextColor(Color.rgb(132,141,150));
        book.setTextColor(Color.rgb(132,141,150));

       // Adshowhome();
    }

    public void booklist(){
        homecat=2;

        prog.setVisibility(View.VISIBLE);

        mobileline.setVisibility(View.GONE);
        electronicsline.setVisibility(View.GONE);
        homeline.setVisibility(View.GONE);
        bookline.setVisibility(View.VISIBLE);
        cycleline.setVisibility(View.GONE);

        book.setTextColor(Color.WHITE);
        mobile.setTextColor(Color.rgb(132,141,150));
        electronics.setTextColor(Color.rgb(132,141,150));
        cycle.setTextColor(Color.rgb(132,141,150));
        home.setTextColor(Color.rgb(132,141,150));

       // Adshowbooks();
    }

    public void cyclelist(){

        homecat=3;

        prog.setVisibility(View.VISIBLE);

        mobileline.setVisibility(View.GONE);
        electronicsline.setVisibility(View.GONE);
        homeline.setVisibility(View.GONE);
        bookline.setVisibility(View.GONE);
        cycleline.setVisibility(View.VISIBLE);

        cycle.setTextColor(Color.WHITE);
        mobile.setTextColor(Color.rgb(132,141,150));
        electronics.setTextColor(Color.rgb(132,141,150));
        home.setTextColor(Color.rgb(132,141,150));
        book.setTextColor(Color.rgb(132,141,150));

       // Adshowcycle();
    }

    public void mobilelist(){
        homecat=4;

        prog.setVisibility(View.VISIBLE);

        mobileline.setVisibility(View.VISIBLE);
        electronicsline.setVisibility(View.GONE);
        homeline.setVisibility(View.GONE);
        bookline.setVisibility(View.GONE);
        cycleline.setVisibility(View.GONE);

        mobile.setTextColor(Color.WHITE);
        home.setTextColor(Color.rgb(132,141,150));
        electronics.setTextColor(Color.rgb(132,141,150));
        cycle.setTextColor(Color.rgb(132,141,150));
        book.setTextColor(Color.rgb(132,141,150));
       // Adshowmobile();
    }

    public void electronicslist(){
        homecat=5;

        prog.setVisibility(View.VISIBLE);

        mobileline.setVisibility(View.GONE);
        electronicsline.setVisibility(View.VISIBLE);
        homeline.setVisibility(View.GONE);
        bookline.setVisibility(View.GONE);
        cycleline.setVisibility(View.GONE);

        electronics.setTextColor(Color.WHITE);
        mobile.setTextColor(Color.rgb(132,141,150));
        home.setTextColor(Color.rgb(132,141,150));
        cycle.setTextColor(Color.rgb(132,141,150));
        book.setTextColor(Color.rgb(132,141,150));


       // Adshowelectronics();
    }

    public void intializelay(){
        homel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               homelist();
            }
        });

        bookl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                booklist();
            }
        });

        cyclel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cyclelist();
            }
        });

        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobilelist();
            }
        });

        electronicsl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                electronicslist();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.drawhome:
            {
               // progressDialog.dismiss();
                prog.setVisibility(View.GONE);
                MyScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                    }
                },100);
                //progressDialog.setMessage("Loading...");
                //progressDialog.show();
                prog.setVisibility(View.VISIBLE);
                homelist();
                break;
            }
            case R.id.drawbooks:
            {
               // progressDialog.dismiss();
                prog.setVisibility(View.GONE);
                MyScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                    }
                },100);
               // progressDialog.setMessage("Loading...");
                //progressDialog.show();
                prog.setVisibility(View.VISIBLE);
                booklist();
                break;}
            case R.id.drawcycle:
            {
               // progressDialog.dismiss();
                prog.setVisibility(View.GONE);
                MyScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                    }
                },100);
                //progressDialog.setMessage("Loading...");
                //progressDialog.show();
                prog.setVisibility(View.VISIBLE);
                cyclelist();
                break;}
            case R.id.drawmobile:
            {
               // progressDialog.dismiss();
                prog.setVisibility(View.GONE);
                MyScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                },100);
               // progressDialog.setMessage("Loading...");
                //progressDialog.show();
                prog.setVisibility(View.VISIBLE);
                mobilelist();
                break;}
            case R.id.drawelec:
            {
                prog.setVisibility(View.GONE);
                MyScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                },100);
                prog.setVisibility(View.VISIBLE);
                electronicslist();
                break;}
            case R.id.drawwish:
            {
                prog.setVisibility(View.GONE);

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

                prog.setVisibility(View.GONE);
                break;
            }
            case R.id.drawmyad:
            {
                prog.setVisibility(View.GONE);

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
                prog.setVisibility(View.GONE);

            break;
            }

            case R.id.chat:
            {
                prog.setVisibility(View.GONE);

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
                prog.setVisibility(View.GONE);
                break;
            }

            case R.id.drawacc:
            {
                prog.setVisibility(View.GONE);
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
                                    prog.setVisibility(View.VISIBLE);

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

                                    prog.setVisibility(View.GONE);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    prog.setVisibility(View.GONE);
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
                    prog.setVisibility(View.GONE);
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












//    public void addproductad(){
//
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//
//               //int item=0;
//
//                j=listwish.size();
//
////                listhome.clear();
////                listelectronics.clear();
////                listmobile.clear();
////                listcycle.clear();
////                listbooks.clear();
//
//
//                   final Handler handler = new Handler();
//                   handler.postDelayed(new Runnable() {
//                       @Override
//                       public void run() {
//                           for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()) {
//
//                               String wish_no = "0";
//                               //item++;
//
//                               if (j > 0)
//                                   wish_no = listwish.get(j - 1);
//
//                               String ad_no=dataSnapshot1.getKey();
//
//                               String adinst = dataSnapshot1.child("institute").getValue(String.class);
//                               String adsold = dataSnapshot1.child("sold").getValue(String.class);
//                               String usid = dataSnapshot1.child("userid").getValue(String.class);
//
//                               if (adsold == null || adsold.equals("1"))
//                                   continue;
//
//                               if (fuser != null) {
//                                   if (userid.equals(usid))
//                                       continue;
//                               }
//
//                               if (userclg != null && !userclg.equals("") && !userclg.equals("0") && !userclg.equals(adinst))
//                                   continue;
//
////                 DatabaseReference current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(ad_no).child("posted");
////                 current_user.setValue("1");
//
//                               String adtitle = dataSnapshot1.child("ad_title").getValue(String.class);
//                               String model = dataSnapshot1.child("model").getValue(String.class);
//                               String include = dataSnapshot1.child("includes").getValue(String.class);
//                               String year = dataSnapshot1.child("year").getValue(String.class);
//                               String condition = dataSnapshot1.child("condition").getValue(String.class);
//                               String addetails = dataSnapshot1.child("ad_details").getValue(String.class);
//                               String price = dataSnapshot1.child("price").getValue(String.class);
//                               String category = dataSnapshot1.child("category").getValue(String.class);
//                               String address = dataSnapshot1.child("address").getValue(String.class);
//                               String brand = dataSnapshot1.child("brand").getValue(String.class);
//                               String email = dataSnapshot1.child("email_id").getValue(String.class);
//                               String mobile = dataSnapshot1.child("mobile").getValue(String.class);
//                               String name = dataSnapshot1.child("name").getValue(String.class);
//                               String image1 = dataSnapshot1.child("image1").getValue(String.class);
//                               String image2 = dataSnapshot1.child("image2").getValue(String.class);
//                               String image3 = dataSnapshot1.child("image3").getValue(String.class);
//                               String image4 = dataSnapshot1.child("image4").getValue(String.class);
//
//                               price = "₹ " + price;
//
//                               FireModel fire = new FireModel();
//
//                               fire.setPrice(price);
//                               fire.setTitle(adtitle);
//                               fire.setModel(model);
//                               fire.setIncludes(include);
//                               fire.setYear(year);
//                               fire.setCondition(condition);
//                               fire.setDetails(addetails);
//                               fire.setAddress(address);
//                               fire.setBrand(brand);
//                               fire.setEmail(email);
//                               fire.setMobile(mobile);
//                               fire.setName(name);
//                               fire.setUrl1(image1);
//                               fire.setUrl2(image2);
//                               fire.setUrl3(image3);
//                               fire.setUrl4(image4);
//                               fire.setAdno(ad_no);
//                               fire.setActivity("2");
//                               fire.setSold("0");
//                               fire.setUser(usid);
//
//                               if (wish_no.equals(ad_no) && j > 0) {
//                                   j--;
//                                   fire.setWish("1");
//                               } else
//                                   fire.setWish("0");
//
//                               listhome.add(0,fire);
//
//                               prog.setVisibility(View.GONE);
//
//                               assert category != null;
//                               switch (category) {
//                                   case "   Cycle":
//                                       listcycle.add(0,fire);
//                                       break;
//                                   case "   Books & Stationery":
//                                       listbooks.add(0,fire);
//                                       break;
//                                   case "   Mobile & Tablets":
//                                       listmobile.add(0,fire);
//                                       break;
//                                   case "   Electronics & Appliances":
//                                       listelectronics.add(0,fire);
//                                       break;
//                               }
//
//
//                           }
//                       }
//                   }, 2000);
//
//
//               if(homecat==1)
//                   Adshowhome();
//               else if(homecat==2)
//                   Adshowbooks();
//               else if(homecat==3)
//                   Adshowcycle();
//               else if(homecat==4)
//                   Adshowmobile();
//               else if(homecat==5)
//                   Adshowelectronics();
//
// }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                prog.setVisibility(View.GONE);
//            }
//        });
//
//}
//
//    public void Adshowhome(){
//       // recycle.removeAllViewsInLayout();
//
//        RecyclerView.LayoutManager recyce = new GridLayoutManager(Activity3.this,1);
//        recycle.setLayoutManager(recyce);
//        //recyce.setAutoMeasureEnabled(false);
//        recycle.setItemAnimator( new DefaultItemAnimator());
//        recycle.setAdapter(recyclerAdapter);
//
//        prog.setVisibility(View.GONE);
//    }
//
//    public void Adshowbooks(){
//        //recycle.removeAllViewsInLayout();
//
//        RecyclerView.LayoutManager recyce = new GridLayoutManager(Activity3.this,1);
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
//
//
//    public void userwish(){
//
//        prog.setVisibility(View.VISIBLE);
//
//        if(fuser!=null)
//
//        {   databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("wish");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                listwish.clear();
//                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
//
//                    String advalue=dataSnapshot1.getValue(String.class);
//
//                    listwish.add(advalue);
//
//                }
//                addproductad();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                prog.setVisibility(View.GONE);
//            }
//        });
//        }
//
//        else
//            addproductad();
//
//    }


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
        sliderLayout.startAutoCycle(4000, 4000, true);

        MyApplication.getInstance().setConnectivityListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        sliderLayout.stopAutoCycle();
    }


    private void setUpSliderLayout() {

        for(int i=0;i<ImagesArray.size();i++){

            TextSliderView sliderpic = new TextSliderView(getApplicationContext());
            sliderpic.image(ImagesArray.get(i));
            sliderpic.description("");
            //sliderpic.setOnSliderClickListener(slider -> Activity);

            sliderLayout.addSlider(sliderpic);

        }


        sliderLayout.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setDuration(4000);
        sliderLayout.setVisibility(View.GONE);
    }


}