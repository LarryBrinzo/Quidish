package com.quidish.anshgupta.login.PostYourAd.PostAd;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DetailsVerificationActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    FirebaseAuth firebaseAuth;
    FirebaseUser fuser;
    String userid,useremail;
    DatabaseReference databaseReference;
    EditText name,number;
    TextView phnumber,text1,text2;
    LinearLayout verified,notverified,back,layoutfir;
    String phone;
    StorageReference refimg2;
    Switch switch1;
    String brand,condition,description,price,adtitle,imagesUri=null;
    Button next;
    View layoutsec;
    int shownumber=0,phtext=0,nametext=0,verify=0;
    long totalAd_no=1,userad_no=1;
    ImageView circle;
    ProgressBar progressBar;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_verification);

        checkConnection();

        name=findViewById(R.id.name);
        phnumber=findViewById(R.id.phnumber);
        text1=findViewById(R.id.text1);
        text2=findViewById(R.id.text2);
        verified=findViewById(R.id.verified);
        notverified=findViewById(R.id.notverified);
        number=findViewById(R.id.number);
        switch1=findViewById(R.id.switch1);
        next=findViewById(R.id.next);
        circle=findViewById(R.id.circle);
        progressBar=findViewById(R.id.progressBar);
        layoutfir=findViewById(R.id.layoutorg);
        coordinatorLayout=findViewById(R.id.coordinator);
        layoutsec = findViewById(R.id.my_view);

        layoutsec.setVisibility(View.INVISIBLE);

        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        fuser = firebaseAuth.getCurrentUser();

        if(fuser!=null)
            userid=fuser.getUid();

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    shownumber=1;
                } else {
                    shownumber=0;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verify==0){
                    Intent intent = new Intent(DetailsVerificationActivity.this, PhoneVerificationActivity.class);
                    intent.putExtra("number",number.getText().toString());
                    intent.putExtra("name",name.getText().toString());
                    intent.putExtra("shownumber",shownumber);
                   // intent.putExtra("email",useremail);
                    startActivity(intent);
                }

                else {

                    next.setVisibility(View.GONE);

                    layoutfir.setVisibility(View.VISIBLE);
                    slideUp(layoutsec);

                    ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0,20);
                    progressAnimator.setDuration(1000);
                    progressAnimator.setInterpolator(new LinearInterpolator());
                    progressAnimator.start();

                    uploadImage(SelectCatagoryActivity.finalSelectedFilepath.get(0),0);
                }
            }
        });

        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()>0){
                   nametext=1;
                }

                else
                    nametext=0;

                if((nametext==1 && phtext==1) || (verify==1 && nametext==1)){
                    next.setEnabled(true);
                    next.setBackgroundResource(R.drawable.border3);
                    next.setTextColor(Color.parseColor("#ffffff"));
                }

                else {
                    next.setEnabled(false);
                    next.setBackgroundResource(R.drawable.border8);
                    next.setTextColor(Color.parseColor("#aeaeae"));
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        number.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()==10){
                    phtext=1;
                }

                else
                    phtext=0;

                if(nametext==1 && phtext==1){
                    next.setEnabled(true);
                    next.setBackgroundResource(R.drawable.border3);
                    next.setTextColor(Color.parseColor("#ffffff"));
                }

                else {
                    next.setEnabled(false);
                    next.setBackgroundResource(R.drawable.border8);
                    next.setTextColor(Color.parseColor("#aeaeae"));
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if(fuser!=null) {
            checknumberpresent();
            linkaccount();
        }

    }


    public void checknumberpresent(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Verification");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String num=dataSnapshot.getValue(String.class);

                if("1".equals(num))
                {
                    notverified.setVisibility(View.GONE);
                    text1.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);
                    verified.setVisibility(View.VISIBLE);
                    phtext=1;
                    verify=1;
                    next.setText("LIST IT!");

                    if(name.getText().toString().length()>0)
                        nametext=1;

                    if((verify==1 && nametext==1)){
                        next.setEnabled(true);
                        next.setBackgroundResource(R.drawable.border3);
                        next.setTextColor(Color.parseColor("#ffffff"));
                    }

                    else {
                        next.setEnabled(false);
                        next.setBackgroundResource(R.drawable.border8);
                        next.setTextColor(Color.parseColor("#aeaeae"));
                    }
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressBar.setVisibility(View.GONE);

                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Error...",Snackbar.LENGTH_LONG);

                View snackview=snackbar.getView();
                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);

                snackbar.show();
            }
        });

    }


    public void linkaccount() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                useremail = dataSnapshot.child("Email_ID").getValue(String.class);
                String usermobile = dataSnapshot.child("Mobile").getValue(String.class);
                String username = dataSnapshot.child("Full_Name").getValue(String.class);

                if(username != null && !username.isEmpty())
                    name.setText(username);
                else
                    name.setText(useremail);

                if(usermobile != null && !usermobile.isEmpty()){
                    phnumber.setText(usermobile);
                    number.setText(usermobile);
                    phone=usermobile;
                }

                if(name.getText().toString().length()>0)
                    nametext=1;

                if((verify==1 && nametext==1)){
                    next.setEnabled(true);
                    next.setBackgroundResource(R.drawable.border3);
                    next.setTextColor(Color.parseColor("#ffffff"));
                }

                else {
                    next.setEnabled(false);
                    next.setBackgroundResource(R.drawable.border8);
                    next.setTextColor(Color.parseColor("#aeaeae"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);

                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Error...",Snackbar.LENGTH_LONG);

                View snackview=snackbar.getView();
                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);

                snackbar.show();
            }
        });

    }


    private void uploadImage(Bitmap bitmap, final int index) {

        final Uri filepath1=getImageUri(getApplicationContext(),bitmap);

        if(filepath1 != null)
        {
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm:ss");
            String strDate =mdformat.format(calendar.getTime());

            if (calendar.get(Calendar.AM_PM) == 0) {
                strDate+="AM";
            } else {
                strDate+="PM";
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd:MM:yyyy");
            String date = df.format(Calendar.getInstance().getTime());

            date+=strDate;

            refimg2 = FirebaseStorage.getInstance().getReference().child("AdsImage").child(date);

            refimg2.putFile(filepath1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            refimg2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if(imagesUri==null)
                                        imagesUri=uri.toString();
                                    else
                                        imagesUri=imagesUri+" "+uri.toString();

                                    getContentResolver().delete(filepath1, null, null);

                                    ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressBar.getProgress()+5);
                                    progressAnimator.setDuration(1000);
                                    progressAnimator.setInterpolator(new LinearInterpolator());
                                    progressAnimator.start();

                                    if(index+1<SelectCatagoryActivity.finalSelectedFilepath.size())
                                        uploadImage(SelectCatagoryActivity.finalSelectedFilepath.get(index+1),index+1);
                                    else
                                        totalAd_numbergiver();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressBar.setVisibility(View.GONE);

                                    Snackbar snackbar=Snackbar.make(coordinatorLayout,"Failed to upload images.",Snackbar.LENGTH_LONG);

                                    View snackview=snackbar.getView();
                                    snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                                    TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                                    textView.setTextColor(Color.WHITE);

                                    snackbar.show();

                                    layoutfir.setVisibility(View.GONE);
                                    slideDown(layoutsec);

                                    next.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);

                            Snackbar snackbar=Snackbar.make(coordinatorLayout,"Failed to upload images.",Snackbar.LENGTH_LONG);

                            View snackview=snackbar.getView();
                            snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                            TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);

                            snackbar.show();

                            layoutfir.setVisibility(View.GONE);
                            slideDown(layoutsec);
                            next.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void totalAd_numbergiver() {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Ads").child(SavedAddressAdapter.adaddressmodel.getInstid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressBar.getProgress()+20);
                progressAnimator.setDuration(1000);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();

                totalAd_no=dataSnapshot.getChildrenCount()+1;
                userAd_numbergiver();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                layoutfir.setVisibility(View.GONE);
                slideDown(layoutsec);
                next.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.GONE);

                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Error...",Snackbar.LENGTH_LONG);

                View snackview=snackbar.getView();
                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);

                snackbar.show();
            }
        });
    }

    public void userAd_numbergiver() {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Posted Ad");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressBar.getProgress()+10);
                progressAnimator.setDuration(1000);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();

                userad_no= dataSnapshot.getChildrenCount()+1;
                storefinal();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);

                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Error...",Snackbar.LENGTH_LONG);

                View snackview=snackbar.getView();
                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);

                snackbar.show();
            }
        });

    }


    public void storefinal(){

        Map<String,String> newAd=new HashMap<>();

        newAd.put("Address_Complete",SavedAddressAdapter.adaddressmodel.getCompadd());
        newAd.put("Address_Lat",SavedAddressAdapter.adaddressmodel.getLat());
        newAd.put("Address_Lng",SavedAddressAdapter.adaddressmodel.getLng());
        newAd.put("Address_Landmark",SavedAddressAdapter.adaddressmodel.getLandmark());
        newAd.put("Address_Nick",SavedAddressAdapter.adaddressmodel.getNickname());
        newAd.put("brand",brand);
        newAd.put("ad_title",adtitle);
        newAd.put("ad_details",description);
        newAd.put("price",price);
        newAd.put("category",SelectPictureActivity.Ad_category);
        newAd.put("mobile",phone);
        newAd.put("name",name.getText().toString());
        newAd.put("verified","0");
        newAd.put("sold","0");
        newAd.put("image",imagesUri);
        newAd.put("userid",userid);
        newAd.put("condition",condition);
        newAd.put("shownumber",Integer.toString(shownumber));
        newAd.put("views","0");
        newAd.put("likes","0");


        DatabaseReference current_user;

        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(SavedAddressAdapter.adaddressmodel.getInstid()).child(Long.toString(totalAd_no));
        current_user.setValue(newAd);

        current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Posted Ad").child(Long.toString(userad_no));
        String adid=SavedAddressAdapter.adaddressmodel.getInstid()+" "+Long.toString(totalAd_no);
        current_user.setValue(adid);

        current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Full_Name");
        current_user.setValue(name.getText().toString());


        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), 100);
        progressAnimator.setDuration(6000);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Intent intent = new Intent(DetailsVerificationActivity.this, AdConfirmationActivity.class);
                intent.putExtra("ad_no", SavedAddressAdapter.adaddressmodel.getInstid()+" "+totalAd_no);
                startActivity(intent);
                finishAffinity();
            }
        }, 7500);


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

    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        adtitle=(pref.getString("adtitle", null));
        description=(pref.getString("addescription", null));
        price=(pref.getString("price", null));
        brand=(pref.getString("brand", null));
        condition=(pref.getString("condition", null));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
