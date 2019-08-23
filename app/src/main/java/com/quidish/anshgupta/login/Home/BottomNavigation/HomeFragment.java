package com.quidish.anshgupta.login.Home.BottomNavigation;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import com.facebook.shimmer.ShimmerFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.Home.AllCategoriesAdapter;
import com.quidish.anshgupta.login.Home.CategoryAdapter;
import com.quidish.anshgupta.login.Home.Location.GetUsersLocationActivity;
import com.quidish.anshgupta.login.Home.Searching.CompleteSearchActivity;
import com.quidish.anshgupta.login.Home.UserAdsAdapter;
import com.quidish.anshgupta.login.LoginRegister.LoginSignupactivity;
import com.quidish.anshgupta.login.Messaging.MyChatActivity;
import com.quidish.anshgupta.login.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Math.max;

public class HomeFragment extends Fragment {

    FirebaseAuth myfba;
    LinearLayout chat;
    private SliderLayout mDemoSlider;
    ValueEventListener eventListener;
    static public String userid,mapdate,userclg="0";
    FirebaseDatabase firebaseDatabase;
    HashSet<String> set = new HashSet<>();
    NestedScrollView scrollView;
    static public FirebaseUser fuser;
    HashMap<String,Integer> url_maps = new HashMap<>();
    NotificationChannel mChannel;
    NotificationManager notifManager;
    DatabaseReference databaseReference;
    ImageView hideimg;
    LinearLayout location;
    private static final int REQUEST_CODE = 1234;
    TextView useremail,uclg;
    List<AdModel> listBooks=new ArrayList<>();
    public static List<AdModel> listAll=new ArrayList<>();
    public static List<Pair<String,String>> catlist=new ArrayList<>();
    LinearLayout search;
    public static List<String> listwish=new ArrayList<>();
    int fblog=0;
    int sclrct=0,notify=1;
    int tunreadmsg=0,scroll=0;
    ProgressBar progbar;
    private int oldScrollYPostion = 0;
    RecyclerView recycle,horzontalrecycle,categoryrecycle;
    public static long adcount=0,adstart = StrictMath.max(adcount,1),adend,itm=0;
    UserAdsAdapter recyclerAdapterAll;
    CategoryAdapter recyclerAdapterBooks;
    ShimmerFrameLayout container;
    AllCategoriesAdapter allCategoriesAdapter;
    String userclgid;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.frag_home, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle =view.findViewById(R.id.recycle);
        categoryrecycle =view.findViewById(R.id.categoryrecycle);
        recycle.setNestedScrollingEnabled(false);
        hideimg=view.findViewById(R.id.hideimg);
        search=view.findViewById(R.id.search);
        location=view.findViewById(R.id.location);
        chat=view.findViewById(R.id.chat);
        scrollView=view.findViewById(R.id.scrollView2);
        mDemoSlider =view.findViewById(R.id.slider);
        progbar=view.findViewById(R.id.prog);
        horzontalrecycle=view.findViewById(R.id.horzontalrecycle);
        uclg=view.findViewById(R.id.uclg);

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        pref.getString("userinstituteid", null);

       recyclerSet();
       findAdCount();


        hideimg.setVisibility(View.VISIBLE);

        ImagesfromDatabase();

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getContext(), GetUsersLocationActivity.class);
                startActivity(intent);

            }
        });

        if(fuser!=null)
        {
            userid=myfba.getCurrentUser().getUid();
            DatabaseReference current_user;
            current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("lastseen");
            current_user.setValue("Online");
        }

        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            fblog=1;
        }

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


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser!=null)
                {
                    Intent intent=new Intent(getContext(), MyChatActivity.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent=new Intent(getContext(),LoginSignupactivity.class);
                    startActivity(intent);
                }
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getContext(), CompleteSearchActivity.class);
                intent.putExtra("active", "0");
                startActivity(intent);
            }
        });

        return view;
    }

    public void findAdCount(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads").child(userclgid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                adcount=dataSnapshot.getChildrenCount();
                adstart=max(adcount,1);
                adend=max(adstart-29,1);
                userwish();

                addbooksad();
                Adtraversal();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void recyclerSet(){

        listAll.clear();
        listwish.clear();
        listBooks.clear();
        set.clear();

        recyclerAdapterAll = new UserAdsAdapter(listAll, getContext());
        RecyclerView.LayoutManager recyceAll = new GridLayoutManager(getContext(),2);
        recycle.setLayoutManager(recyceAll);
        recyceAll.setAutoMeasureEnabled(false);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapterAll);

        recyclerAdapterBooks = new CategoryAdapter(listBooks,getContext());
        RecyclerView.LayoutManager recycebooks = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horzontalrecycle.setLayoutManager(recycebooks);
        horzontalrecycle.setLayoutManager(recycebooks);
        horzontalrecycle.setAdapter(recyclerAdapterBooks);

        allCategoriesAdapter = new AllCategoriesAdapter(catlist,getContext());
        GridLayoutManager recycecat = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        categoryrecycle.setLayoutManager(recycecat);
        categoryrecycle.setLayoutManager(recycecat);
        categoryrecycle.setAdapter(allCategoriesAdapter);

        addCategories();

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        userclgid=pref.getString("userinstituteid", null);

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

    public void ImagesfromDatabase(){

        url_maps.put("1", R.drawable.pic_3);
        url_maps.put("2", R.drawable.pic1);
        url_maps.put("3", R.drawable.pic4);
        url_maps.put("4", R.drawable.pic2);

        setUpSliderLayout();
    }


    public void onBackPressed() {
        backButtonHandler();
    }

    public void backButtonHandler() {

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
            current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("lastseen");
            current_user.setValue(mapdate);

        }

    }

    public void addproductad(final long start){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads").child(userclgid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                listAll.clear();

                for(long i=start;i>=adend;i--) {

                    scroll=0;
                    String ad_no=Long.toString(i);

                    itm++;

                    String adsold = dataSnapshot.child(ad_no).child("sold").getValue(String.class);
                    String usid = dataSnapshot.child(ad_no).child("userid").getValue(String.class);

                    if (adsold==null || adsold.equals("1"))
                    { if(adend>0){
                        adend--; } }

                    else if (fuser != null && userid.equals(usid)) {
                        if(adend>0){
                            adend--;
                        } }

                    else if(set.contains(ad_no))
                    { }


                    else{
                        ad_no=Long.toString(i);

                        String adtitle = dataSnapshot.child(ad_no).child("ad_title").getValue(String.class);
                        String price = dataSnapshot.child(ad_no).child("price").getValue(String.class);
                        String brand = dataSnapshot.child(ad_no).child("brand").getValue(String.class);
                        String image1 = dataSnapshot.child(ad_no).child("image").getValue(String.class);

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

                        set.add(ad_no);

                        if(listwish.contains(ad_no))
                            fire.setWish("1");

                        else
                            fire.setWish("0");

                        listAll.add(fire);

                        if(notify==1)
                            recyclerAdapterAll.notifyItemInserted(listAll.size() - 1);

                    }
                }

                adstart=adend-1;
                sclrct=1;
                scroll=1;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                adstart=adend-1;
                sclrct=1;
                scroll=1;
            }
        });

    }



    public void Adtraversal(){

        progbar.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adstart= StrictMath.max(adcount,1);
                itm=0;
                adend=max(adstart-49,1);
                scroll=0;
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

                if (diff == 0 && sclrct==1 && itm<=adcount && scroll==1) {

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
                    if (sclrct==1 && itm<=adcount && scroll==0) {
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


    public void userwish(){

        if(fuser!=null)

        {   databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("wish");

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


        SharedPreferences pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final String userclgid=pref.getString("userinstituteid", null);

        final DatabaseReference booksreference = FirebaseDatabase.getInstance().getReference().child("Ads").child(userclgid);
        Query query = booksreference.orderByChild("likes").limitToFirst(6);

        eventListener=query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                int bkct=0;
                listBooks.clear();

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()) {

                    String adsold = dataSnapshot1.child("sold").getValue(String.class);
                    String usid = dataSnapshot1.child("userid").getValue(String.class);

                    if (adsold == null || adsold.equals("1")){}

                    else if (fuser != null && userid.equals(usid)) {
                    }

                    else{

                        bkct++;

                        if(bkct==6)
                            break;

                        String adtitle = dataSnapshot1.child("ad_title").getValue(String.class);
                        String price = dataSnapshot1.child("price").getValue(String.class);
                        String brand = dataSnapshot1.child("brand").getValue(String.class);
                        String image1 = dataSnapshot1.child("image").getValue(String.class);
                        String ad_no=dataSnapshot1.getKey();

                        String[] splited = image1.split(" ");

                        AdModel fire = new AdModel();

                        fire.setPrice(price);
                        fire.setTitle(adtitle);
                        fire.setBrand(brand);
                        fire.setAdno(userclgid+" "+ad_no);
                        fire.setActivity("2");
                        fire.setSold("0");
                        fire.setUser(usid);
                        fire.setUrl1(splited[0]);

                        if(listwish.contains(userclgid+" "+ad_no))
                            fire.setWish("1");

                        else
                            fire.setWish("0");

                        listBooks.add(fire);

                    }

                    recyclerAdapterBooks.notifyDataSetChanged();
                }

                container.setVisibility(View.GONE);
                container.stopShimmer();

                mDemoSlider.startAutoCycle();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                container.setVisibility(View.GONE);
                container.stopShimmer();
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        container = view.findViewById(R.id.shimmer_view_container);

         container.startShimmer();
        mDemoSlider.startAutoCycle();
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        userclg=pref.getString("userinstitute", null);

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


    private void setUpSliderLayout() {

        for(String name : url_maps.keySet()){

            DefaultSliderView defaultSliderView = new DefaultSliderView(getContext());

            defaultSliderView
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            mDemoSlider.addSlider(defaultSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setDuration(6000);
        mDemoSlider.stopAutoCycle();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideimg.setVisibility(View.GONE);
            }
        }, 400);

    }

}