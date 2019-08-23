package com.quidish.anshgupta.login.PostYourAd.PostAd;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    EditText etext1,etext2,etext3,etext4,etext5,etext6;
    Button next;
    int passct=0,shownumber;
    String phone,code_sent=null;
    FirebaseUser fuser;
    FirebaseAuth firebaseAuth;
    String brand,condition,description,price,adtitle,mobile,name,userid,imagesUri=null;
    StorageReference refimg2;
    long totalAd_no=1,userad_no=1;
    CoordinatorLayout coordinatorLayout;
    TextView number,resend;
    LinearLayout back,layoutfir;
    View layoutsec;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        checkConnection();

        etext1=findViewById(R.id.etext1);
        etext2=findViewById(R.id.etext2);
        etext3=findViewById(R.id.etext3);
        etext4=findViewById(R.id.etext4);
        etext5=findViewById(R.id.etext5);
        etext6=findViewById(R.id.etext6);
        next=findViewById(R.id.next);
        number=findViewById(R.id.number);
        resend=findViewById(R.id.resend);
        layoutfir=findViewById(R.id.layoutorg);
        coordinatorLayout=findViewById(R.id.coordinator);
        layoutsec = findViewById(R.id.my_view);
        progressBar=findViewById(R.id.progressBar);

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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("number");
            name=bundle.getString("name");
            shownumber=bundle.getInt("shownumber");
            number.setText("+91"+phone);
           // email=bundle.getString("email");
        }

        Send_code();

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send_code();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                next.setVisibility(View.GONE);

                layoutfir.setBackgroundColor(Color.parseColor("#80000000"));
                slideUp(layoutsec);

                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0,20);
                progressAnimator.setDuration(1000);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();

                if(fuser!=null && code_sent!=null)
                    Verify_signincode();
                else if(code_sent!=null)
                    Verify_signincodeNewUser();
            }
        });


        etext1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()==1){
                    passct++;
                    etext2.requestFocus();
                }

                else
                    passct--;

                if(passct==6 && code_sent!=null){
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
        etext2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()==1){
                    passct++;
                    etext3.requestFocus();
                }

                else{
                    passct--;
                    etext1.requestFocus();
                }

                if(passct==6 && code_sent!=null){
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
        etext3.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()==1){
                    passct++;
                    etext4.requestFocus();
                }

                else{
                    passct--;
                    etext2.requestFocus();
                }

                if(passct==6 && code_sent!=null){
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
        etext4.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()==1){
                    passct++;
                    etext5.requestFocus();
                }

                else{
                    passct--;
                    etext3.requestFocus();
                }

                if(passct==6 && code_sent!=null){
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
        etext5.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()==1){
                    passct++;
                    etext6.requestFocus();
                }

                else{
                    passct--;
                    etext4.requestFocus();
                }

                if(passct==6 && code_sent!=null){
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
        etext6.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()==1){
                    passct++;
                }

                else{
                    passct--;
                    etext5.requestFocus();
                }

                if(passct==6 && code_sent!=null){
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

    }



    public void Send_code(){

        phone="+91"+phone;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            code_sent =s;
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //if(phoneAuthCredential!=null)
          //  phupdate(phoneAuthCredential,0);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            layoutfir.setBackgroundColor(Color.parseColor("#00ffffff"));
            slideDown(layoutsec);

            next.setVisibility(View.VISIBLE);

            Snackbar snackbar=Snackbar.make(coordinatorLayout,"Error in verification",Snackbar.LENGTH_LONG);

            View snackview=snackbar.getView();
            snackview.setBackgroundColor(Color.rgb(255, 99, 71));
            TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
        }

    };


    private void Verify_signincodeNewUser() {

        String code_en=etext1.getText().toString()+etext2.getText().toString()+etext3.getText().toString()+etext4.getText().toString()+etext5.getText().toString()+etext6.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code_sent, code_en);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(PhoneVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            fuser=firebaseAuth.getCurrentUser();

                            if(fuser!=null)
                                userid=fuser.getUid();

                            uploadImage(SelectCatagoryActivity.finalSelectedFilepath.get(0),0);

                            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressBar.getProgress()+20);
                            progressAnimator.setDuration(1000);
                            progressAnimator.setInterpolator(new LinearInterpolator());
                            progressAnimator.start();

                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                layoutfir.setBackgroundColor(Color.parseColor("#00ffffff"));
                                slideDown(layoutsec);

                                next.setVisibility(View.VISIBLE);

                                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Mobile Phone registered to another account.",Snackbar.LENGTH_LONG);

                                View snackview=snackbar.getView();
                                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                                TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);

                                snackbar.show();

                            }

                            else {
                                layoutfir.setBackgroundColor(Color.parseColor("#00ffffff"));
                                slideDown(layoutsec);

                                next.setVisibility(View.VISIBLE);

                            Snackbar snackbar=Snackbar.make(coordinatorLayout,"Incorrect verification code entered.",Snackbar.LENGTH_LONG);

                            View snackview=snackbar.getView();
                            snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                            TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);

                            snackbar.show();
                            }
                        }
                    }
                });
    }


    public void Verify_signincode(){

        if(code_sent==null || code_sent.isEmpty())
            return;

        String code_en=etext1.getText().toString()+etext2.getText().toString()+etext3.getText().toString()+etext4.getText().toString()+etext5.getText().toString()+etext6.getText().toString();

        PhoneAuthCredential credentials = PhoneAuthProvider.getCredential(code_sent,code_en);

        (firebaseAuth.getCurrentUser()).linkWithCredential(credentials).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                        layoutfir.setBackgroundColor(Color.parseColor("#00ffffff"));
                        slideDown(layoutsec);

                        next.setVisibility(View.VISIBLE);

                        Snackbar snackbar=Snackbar.make(coordinatorLayout,"Mobile Phone registered to another account",Snackbar.LENGTH_LONG);

                        View snackview=snackbar.getView();
                        snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                        TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);

                        snackbar.show();

                    }

                    else {

                        String code_en=etext1.getText().toString()+etext2.getText().toString()+etext3.getText().toString()+etext4.getText().toString();
                        PhoneAuthCredential credentials = PhoneAuthProvider.getCredential(code_sent,code_en);

                        phupdate(credentials,1);
                        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressBar.getProgress()+10);
                        progressAnimator.setDuration(1000);
                        progressAnimator.setInterpolator(new LinearInterpolator());
                        progressAnimator.start();
                    }

                } else {

                    userid=firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference current_user;

                    current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("verification");
                    current_user.setValue("1");

                    uploadImage(SelectCatagoryActivity.finalSelectedFilepath.get(0),0);
                    ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressBar.getProgress()+10);
                    progressAnimator.setDuration(1000);
                    progressAnimator.setInterpolator(new LinearInterpolator());
                    progressAnimator.start();
                }
            }

        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void phupdate(PhoneAuthCredential credentials, final int st){

        Objects.requireNonNull(firebaseAuth.getCurrentUser()).updatePhoneNumber(credentials).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    userid=firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference current_user;

                    current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Verification");
                    current_user.setValue("1");

                    if(st==0)
                        uploadImage(SelectCatagoryActivity.finalSelectedFilepath.get(0),0);

                    ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressBar.getProgress()+10);
                    progressAnimator.setDuration(1000);
                    progressAnimator.setInterpolator(new LinearInterpolator());
                    progressAnimator.start();

                } else {

                    layoutfir.setBackgroundColor(Color.parseColor("#00ffffff"));
                    slideDown(layoutsec);

                    next.setVisibility(View.VISIBLE);

                    Snackbar snackbar=Snackbar.make(coordinatorLayout,"Incorrect verification code entered.",Snackbar.LENGTH_LONG);

                    View snackview=snackbar.getView();
                    snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                    TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);

                    snackbar.show();
                }
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
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onSuccess(Uri uri) {

                                    if(imagesUri==null)
                                        imagesUri=uri.toString();
                                    else
                                        imagesUri=imagesUri+" "+uri.toString();

                                    getContentResolver().delete(filepath1, null, null);

                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, filepath1));

                                    if(index+1<SelectCatagoryActivity.finalSelectedFilepath.size())
                                        uploadImage(SelectCatagoryActivity.finalSelectedFilepath.get(index+1),index+1);
                                    else
                                        totalAd_numbergiver();

                                    ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressBar.getProgress()+5);
                                    progressAnimator.setDuration(1000);
                                    progressAnimator.setInterpolator(new LinearInterpolator());
                                    progressAnimator.start();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                    layoutfir.setBackgroundColor(Color.parseColor("#00ffffff"));
                                    slideDown(layoutsec);

                                    Snackbar snackbar=Snackbar.make(coordinatorLayout,"Failed to upload images.",Snackbar.LENGTH_LONG);

                                    View snackview=snackbar.getView();
                                    snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                                    TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                                    textView.setTextColor(Color.WHITE);

                                    snackbar.show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            layoutfir.setBackgroundColor(Color.parseColor("#00ffffff"));
                            slideDown(layoutsec);

                            next.setVisibility(View.VISIBLE);

                            Snackbar snackbar=Snackbar.make(coordinatorLayout,"Failed to upload images.",Snackbar.LENGTH_LONG);

                            View snackview=snackbar.getView();
                            snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                            TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);

                            snackbar.show();
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

        if(SavedAddressAdapter.adaddressmodel.getInstid()==null)
            return;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Ads").child(SavedAddressAdapter.adaddressmodel.getInstid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                totalAd_no=dataSnapshot.getChildrenCount()+1;
                userAd_numbergiver();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                layoutfir.setBackgroundColor(Color.parseColor("#00ffffff"));
                slideDown(layoutsec);

                next.setVisibility(View.VISIBLE);

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

        if(userid==null)
            return;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Posted Ad");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userad_no= dataSnapshot.getChildrenCount()+1;
                storefinal();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                layoutfir.setBackgroundColor(Color.parseColor("#00ffffff"));
                slideDown(layoutsec);

                next.setVisibility(View.VISIBLE);

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
        newAd.put("mobile",mobile);
        newAd.put("name",name);
        newAd.put("verified","0");
        newAd.put("sold","0");
        newAd.put("image",imagesUri);
        newAd.put("userid",userid);
        newAd.put("shownumber",Integer.toString(shownumber));
        newAd.put("condition",condition);
        newAd.put("views","0");
        newAd.put("likes","0");

        DatabaseReference current_user;

        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(SavedAddressAdapter.adaddressmodel.getInstid()).child(Long.toString(totalAd_no));
        current_user.setValue(newAd);

        current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Posted Ad").child(Long.toString(userad_no));
        String adid=SavedAddressAdapter.adaddressmodel.getInstid()+" "+Long.toString(totalAd_no);
        current_user.setValue(adid);

        current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Full_Name");
        current_user.setValue(name);

        current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Mobile");
        current_user.setValue(phone);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();

        searchhint.putString("Ad","0");

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), 100);
        progressAnimator.setDuration(6000);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Intent intent = new Intent(PhoneVerificationActivity.this, AdConfirmationActivity.class);
                intent.putExtra("ad_no", SavedAddressAdapter.adaddressmodel.getInstid()+" "+totalAd_no);
                startActivity(intent);
                finishAffinity();
            }
        }, 7500);

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
        mobile=phone;
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

}
