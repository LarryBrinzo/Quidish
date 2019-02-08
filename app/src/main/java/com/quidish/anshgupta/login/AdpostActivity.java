package com.quidish.anshgupta.login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class AdpostActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    LinearLayout after_layout,before_layout,image_layout,addpic_layout,back;
    EditText brand,adtitle,price,name,address,verificationcode,email_id, mobile_no,model,includes,year,condition,addetails;
    int usersigned = 0,verph=0,image_no=0;
    TextView postcat, postsubcat,verifycode,otptxt;
    FirebaseAuth firebaseAuth,myfba;
    FirebaseUser fuser;
    StorageReference mstoragrref;
    Button posturad;
    DatabaseReference databaseReference,refer,ref2,ref;
    FirebaseDatabase firebaseDatabase;
    ImageButton addimage;
    ImageView image1,image2,image3,image4;
    CoordinatorLayout coordinatorLayout;
    Uri filepath1,filepath2,filepath3,filepath4;
    StorageReference refimg2;
    ImageButton addimageafter;
    ProgressDialog progressDialog;
    String userid,uid,code_sent,category,sub_category,totalAd_no,userad_no;
    ProgressBar progressBar;
    String imageurl1="0",imageurl2="0",imageurl3="0",imageurl4="0";
    List<String> clg = new ArrayList<>();
    AutoCompleteTextView autoTextView;
    Bitmap bmp;
    ConstraintLayout cst1,cst2,cst3,cst4;
    ImageView cancel1,cancel2,cancel3,cancel4;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    public static final String GALLERY_DIRECTORY_NAME = "Quidish";
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";
    private static final int PICK_FROM_GALLERY = 2;

    private static String imageStoragePath="0",imageStoragePath2="0",imageStoragePath3="0",imageStoragePath4="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adpost);

        coordinatorLayout=findViewById(R.id.coordinator);
        checkConnection();

        progressBar=findViewById(R.id.progbar);
        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);
        email_id = findViewById(R.id.email);
        mobile_no = findViewById(R.id.mobile);
        name=findViewById(R.id.name);
        model=findViewById(R.id.model);
        includes=findViewById(R.id.includes);
        year=findViewById(R.id.year);
        condition=findViewById(R.id.condition);
        addetails=findViewById(R.id.addetails);
        brand=findViewById(R.id.brand);
        address=findViewById(R.id.address);
        price=findViewById(R.id.price);
        adtitle=findViewById(R.id.adtitle);
        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);
        image4=findViewById(R.id.image4);
        image_layout=findViewById(R.id.image_layout);
        addimageafter=findViewById(R.id.addimageafter);
        addpic_layout=findViewById(R.id.addpic_layout);
        after_layout=findViewById(R.id.after_layout);
        before_layout=findViewById(R.id.before_layout);
        otptxt=findViewById(R.id.otptxt);
        addimage = findViewById(R.id.addimage);
        autoTextView = findViewById(R.id.autocompleteEditTextView);
        cst1=findViewById(R.id.cst1);
        cst2=findViewById(R.id.cst2);
        cst3=findViewById(R.id.cst3);
        cst4=findViewById(R.id.cst4);
        cancel1=findViewById(R.id.cancel1);
        cancel2=findViewById(R.id.cancel2);
        cancel3=findViewById(R.id.cancel3);
        cancel4=findViewById(R.id.cancel4);

        clgset();

        restoreFromBundle(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        myfba= FirebaseAuth.getInstance();

        fuser = firebaseAuth.getCurrentUser();

        if(fuser==null)
        {
            Intent intent=new Intent(getApplicationContext(),LoginSignupactivity.class);
            startActivity(intent);
            finish();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        userid=firebaseAuth.getCurrentUser().getUid();
        refer = firebaseDatabase.getReference().child("users").child(userid).child("verification");
        ref2 = firebaseDatabase.getReference().child("Ads").child("tad_no");
        ref=firebaseDatabase.getReference().child("users").child(userid).child("Posted Ad").child("ad_no");


        postcat = findViewById(R.id.postcat);
        postsubcat = findViewById(R.id.postsubcat);

        posturad=findViewById(R.id.posturad);
        verifycode=findViewById(R.id.verifycode);
        verificationcode=findViewById(R.id.verficationcode);
        verificationcode.setVisibility(View.GONE);
        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            category = bundle.getString("catname");
            sub_category = bundle.getString("subcatname");
        }


        addimageafter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(image_no==4)
                {
                    Toast.makeText(getApplicationContext(), "Can't select more than 4 image", Toast.LENGTH_LONG).show();
                    return;
                }

                selectImage();

            }
        });

        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(image_no==4)
                {
                    Toast.makeText(getApplicationContext(), "Can't select more than 4 image", Toast.LENGTH_LONG).show();
                    return;
                }

                selectImage();

            }
        });

        postcat.setText(category);
        postsubcat.setText(sub_category);

        ad_numbergiver();

        uid = fuser.getUid();

        if (fuser != null) {
            usersigned = 1;
        }

        linkaccount();
        checknumberpresent();
        mobileverification();
        Postad();

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


    //checking if account already exists and setting email name nad mobile of user


    public void linkaccount() {

        if (usersigned == 0)
            return;

        progressBar.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String useremail = dataSnapshot.child("users").child(uid).child("email").getValue(String.class);
                String usermobile = dataSnapshot.child("users").child(uid).child("mobile").getValue(String.class);
                String username = dataSnapshot.child("users").child(uid).child("username").getValue(String.class);
                String address_s = dataSnapshot.child("users").child(uid).child("roll").getValue(String.class);
                String inst = dataSnapshot.child("users").child(uid).child("institute").getValue(String.class);

                email_id.setTextColor(Color.BLACK);
                mobile_no.setTextColor(Color.BLACK);
                name.setTextColor(Color.BLACK);
                address.setTextColor(Color.BLACK);
                autoTextView.setTextColor(Color.BLACK);

                if(useremail != null && !useremail.isEmpty())
                {
                    email_id.setEnabled(false);
                    email_id.setText(useremail);
                }

                if(inst != null && !inst.isEmpty())
                {
                    autoTextView.setEnabled(false);
                    autoTextView.setText(inst);
                }

                if(username != null && !username.isEmpty())
                {
                    name.setText(username);
                    name.setEnabled(false);
                }

                if(usermobile != null && !usermobile.isEmpty())
                {
                    mobile_no.setEnabled(false);
                    mobile_no.setText(usermobile);
                }

                if(address_s != null && !address_s.isEmpty())
                {
                    address.setEnabled(false);
                    address.setText(address_s);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressBar.setVisibility(View.GONE);
            }
        });

    }

    //on click listener for mobile verification

    public void mobileverification(){

        verifycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numbercheking();
            }
        });
    }

    //check if number already exists

    public void checknumberpresent(){
        refer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String num=dataSnapshot.getValue(String.class);

                if("1".equals(num))
                {
                    verph=1;
                    verifycode.setVisibility(View.GONE);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void numbercheking(){
        if(verph==0)
            Send_code();

        else {
            Snackbar snackbar=Snackbar.make(findViewById(R.id.coordinate),"Mobile Phone already verified",Snackbar.LENGTH_LONG);

            View snackview=snackbar.getView();
            snackview.setBackgroundColor(Color.rgb(50, 50, 50));
            TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
        }
    }


    //send code to mobile phone

    public void Send_code(){

        progressDialog.setMessage("Sending Code...");
        progressDialog.show();

        String phone=mobile_no.getText().toString();

        if(phone.isEmpty())
        {
            progressDialog.dismiss();
            mobile_no.setError("Mobile number is required");
            mobile_no.requestFocus();
            return;
        }
        if(phone.length()!=10)
        {
            progressDialog.dismiss();
            mobile_no.setError("Enter valid Phone Number");
            mobile_no.requestFocus();
            return;
        }

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

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Code Sent",Toast.LENGTH_SHORT).show();
            verificationcode.setVisibility(View.VISIBLE);
            otptxt.setVisibility(View.VISIBLE);
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            phupdate(phoneAuthCredential,0);
            progressDialog.dismiss();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressDialog.dismiss();
        }

    };

    public void Verify_signincode(){

        if(code_sent==null || code_sent.isEmpty())
        {
            Snackbar snackbar=Snackbar.make(findViewById(R.id.coordinate),"Verify your Mobile",Snackbar.LENGTH_LONG);

            View snackview=snackbar.getView();
            snackview.setBackgroundColor(Color.rgb(255, 99, 71));
            TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
            return;
        }

        String code_en=verificationcode.getText().toString();

        progressDialog.setMessage("Posting your Ad...");
        progressDialog.show();

        PhoneAuthCredential credentials = PhoneAuthProvider.getCredential(code_sent,code_en);

        firebaseAuth.getCurrentUser().linkWithCredential(credentials).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        progressDialog.dismiss();
                        Snackbar snackbar=Snackbar.make(findViewById(R.id.coordinate),"Mobile Phone Registered to another Account",Snackbar.LENGTH_LONG);

                        View snackview=snackbar.getView();
                        snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                        TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);

                        snackbar.show();
                    }

                    else {

                        String code_en=verificationcode.getText().toString();
                        PhoneAuthCredential credentials = PhoneAuthProvider.getCredential(code_sent,code_en);

                        phupdate(credentials,1);
                    }

                } else {

                    verph=1;

                    String user_id=myfba.getCurrentUser().getUid();
                    DatabaseReference current_user;

                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("verification");
                    current_user.setValue("1");

                    progressDialog.dismiss();
                    Storedata();
                }
            }

        });

    }

    public void phupdate(PhoneAuthCredential credentials, final int st){

        firebaseAuth.getCurrentUser().updatePhoneNumber(credentials).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    verph=1;

                    String user_id=myfba.getCurrentUser().getUid();
                    DatabaseReference current_user;

                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("verification");
                    current_user.setValue("1");

                    progressDialog.dismiss();

                    if(st==0)
                    Storedata();

                } else {
                    progressDialog.dismiss();
                    Snackbar snackbar=Snackbar.make(findViewById(R.id.coordinate),"Incorrect Verification Code",Snackbar.LENGTH_LONG);

                    View snackview=snackbar.getView();
                    snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                    TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);

                    snackbar.show();
                }
            }
        });

    }

    //image upload


    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey("image1")) {
                String imgpath=savedInstanceState.getString("image1");
                if(imgpath!=null && !imgpath.isEmpty() && !imgpath.equals("0"))
                {
                    imageStoragePath=imgpath;
                    previewCapturedImage(imageStoragePath);
                }
            }

            if (savedInstanceState.containsKey("image2")) {
                String imgpath=savedInstanceState.getString("image2");
                if(imgpath!=null && !imgpath.isEmpty() && !imgpath.equals("0"))
                {
                    imageStoragePath2=imgpath;
                    previewCapturedImage(imageStoragePath2);
                }
            }

            if (savedInstanceState.containsKey("image3")) {
                String imgpath=savedInstanceState.getString("image3");
                if(imgpath!=null && !imgpath.isEmpty() && !imgpath.equals("0"))
                {
                    imageStoragePath3=imgpath;
                    previewCapturedImage(imageStoragePath3);
                }
            }

            if (savedInstanceState.containsKey("image4")) {
                String imgpath=savedInstanceState.getString("image4");
                if(imgpath!=null && !imgpath.isEmpty() && !imgpath.equals("0"))
                {
                    imageStoragePath4=imgpath;
                    previewCapturedImage(imageStoragePath4);
                }
            }

        }
    }

    private void selectImage() {

        final CharSequence[] options = { "Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AdpostActivity.this);

        String titleText = "Add Image";
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.rgb(45, 62, 78));
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        builder.setTitle(ssBuilder);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Camera"))
                {

                    if (CameraUtils.checkPermissions(getApplicationContext())) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }

                }
                else if (options[item].equals("Gallery"))
                {
                    try {
                        if (ActivityCompat.checkSelfPermission(AdpostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AdpostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                        } else {
                            Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PICK_FROM_GALLERY:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                break;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {

            if(image_no==0)
                imageStoragePath = file.getAbsolutePath();

            else if(image_no==1)
                imageStoragePath2 = file.getAbsolutePath();

            else if(image_no==2)
                imageStoragePath3 = file.getAbsolutePath();

            else if(image_no==3)
                imageStoragePath4 = file.getAbsolutePath();

        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("image1",imageStoragePath);
        outState.putString("image2",imageStoragePath2);
        outState.putString("image3",imageStoragePath3);
        outState.putString("image4",imageStoragePath4);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        imageStoragePath = savedInstanceState.getString("image1");
        imageStoragePath2 =savedInstanceState.getString("image2");
        imageStoragePath3 = savedInstanceState.getString("image3");
        imageStoragePath4 = savedInstanceState.getString("image4");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {

                    if(image_no==0)
                    {
                        CameraUtils.refreshGallery(getApplicationContext(),imageStoragePath);
                        previewCapturedImage(imageStoragePath);
                    }

                    else if(image_no==1)
                    {
                        CameraUtils.refreshGallery(getApplicationContext(),imageStoragePath2);
                        previewCapturedImage(imageStoragePath2);
                    }

                    else if(image_no==2)
                    {
                        CameraUtils.refreshGallery(getApplicationContext(),imageStoragePath3);
                        previewCapturedImage(imageStoragePath3);
                    }

                    else if(image_no==3)
                    {
                        CameraUtils.refreshGallery(getApplicationContext(),imageStoragePath4);
                        previewCapturedImage(imageStoragePath4);
                    }

                } else if (resultCode == RESULT_CANCELED) {

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            else if (requestCode == 2) {

                if (resultCode == RESULT_OK) {

                bmp = ImagePicker.getImageFromResult(this, resultCode, data);
                Uri selectedImageUri = data.getData( );
                String picturePath = getPath(AdpostActivity.this.getApplicationContext( ), selectedImageUri );

                    if(image_no==0)
                        imageStoragePath = picturePath;

                    else if(image_no==1)
                        imageStoragePath2 = picturePath;

                    else if(image_no==2)
                        imageStoragePath3 = picturePath;

                    else if(image_no==3)
                        imageStoragePath4 = picturePath;

                getImageUri(getApplicationContext(),bmp);

                    }
            }

    }

    public static String getPath( Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    private void previewCapturedImage(String imgpath) {

        try {
            bmp= CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imgpath);
            getImageUri(getApplicationContext(),bmp);

        } catch (NullPointerException ignored) {
        }
    }

    public void imgset(){

            image_layout.setVisibility(View.VISIBLE);
            addpic_layout.setVisibility(View.GONE);
            before_layout.setVisibility(View.GONE);
            after_layout.setVisibility(View.VISIBLE);

        image_no++;

        if(image_no==0)
        {
            image_layout.setVisibility(View.GONE);
            addpic_layout.setVisibility(View.VISIBLE);
            before_layout.setVisibility(View.VISIBLE);
            after_layout.setVisibility(View.GONE);
        }

        if(image_no==1)
        {
            cst1.setVisibility(View.VISIBLE);
            image1.setImageURI(filepath1);

            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                    intent.putExtra("pic",filepath1.toString());
                    startActivity(intent);
                }
            });

            cancel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageStoragePath=imageStoragePath2;
                    imageStoragePath2=imageStoragePath3;
                    imageStoragePath3=imageStoragePath4;
                    imageStoragePath4="0";
                    filepath1=filepath2;
                    filepath2=filepath3;
                    filepath3=filepath4;
                    filepath4=null;

                    if(imageStoragePath.equals("0"))
                        cst1.setVisibility(View.GONE);
                    if(imageStoragePath2.equals("0"))
                        cst2.setVisibility(View.GONE);
                    if(imageStoragePath3.equals("0"))
                        cst3.setVisibility(View.GONE);
                    if(imageStoragePath4.equals("0"))
                        cst4.setVisibility(View.GONE);

                    image_no=image_no-2;
                    imgset();
                }
            });

        }

        else if(image_no==2)
        {
            cst1.setVisibility(View.VISIBLE);
            cst2.setVisibility(View.VISIBLE);
            image2.setImageURI(filepath2);

            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                    intent.putExtra("pic",filepath2.toString());
                    startActivity(intent);
                }
            });

            cancel2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageStoragePath2=imageStoragePath3;
                    imageStoragePath3=imageStoragePath4;
                    imageStoragePath4="0";
                    filepath2=filepath3;
                    filepath3=filepath4;
                    filepath4=null;

                    if(imageStoragePath.equals("0"))
                        cst1.setVisibility(View.GONE);
                    if(imageStoragePath2.equals("0"))
                        cst2.setVisibility(View.GONE);
                    if(imageStoragePath3.equals("0"))
                        cst3.setVisibility(View.GONE);
                    if(imageStoragePath4.equals("0"))
                        cst4.setVisibility(View.GONE);

                    image_no=image_no-2;
                    imgset();
                }
            });

        }

        else if(image_no==3)
        {
            cst1.setVisibility(View.VISIBLE);
            cst2.setVisibility(View.VISIBLE);
            cst3.setVisibility(View.VISIBLE);
            image3.setImageURI(filepath3);

            image3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                    intent.putExtra("pic",filepath3.toString());
                    startActivity(intent);
                }
            });

            cancel3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageStoragePath3=imageStoragePath4;
                    imageStoragePath4="0";
                    filepath3=filepath4;
                    filepath4=null;

                    if(imageStoragePath.equals("0"))
                        cst1.setVisibility(View.GONE);
                    if(imageStoragePath2.equals("0"))
                        cst2.setVisibility(View.GONE);
                    if(imageStoragePath3.equals("0"))
                        cst3.setVisibility(View.GONE);
                    if(imageStoragePath4.equals("0"))
                        cst4.setVisibility(View.GONE);

                    image_no=image_no-2;
                    imgset();
                }
            });
        }

        else if(image_no==4)
        {
            cst1.setVisibility(View.VISIBLE);
            cst2.setVisibility(View.VISIBLE);
            cst3.setVisibility(View.VISIBLE);
            cst4.setVisibility(View.VISIBLE);
            image4.setImageURI(filepath4);

            image4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                    intent.putExtra("pic",filepath4.toString());
                    startActivity(intent);
                }
            });

            cancel4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imageStoragePath4="0";
                    filepath4=null;

                    if(imageStoragePath.equals("0"))
                        cst1.setVisibility(View.GONE);
                    if(imageStoragePath2.equals("0"))
                        cst2.setVisibility(View.GONE);
                    if(imageStoragePath3.equals("0"))
                        cst3.setVisibility(View.GONE);
                    if(imageStoragePath4.equals("0"))
                        cst4.setVisibility(View.GONE);

                    image_no=image_no-2;
                    imgset();
                }
            });
        }

    }


    private void getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 95, bytes);

        byte[] imageInByte = bytes.toByteArray();
        int lengthbmp = imageInByte.length;

        if(lengthbmp>300000)
            inImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);

        if(image_no==0)
        filepath1= Uri.parse(path);
        else if(image_no==1)
            filepath2= Uri.parse(path);
        else if(image_no==2)
            filepath3= Uri.parse(path);
        else if(image_no==3)
            filepath4= Uri.parse(path);

        imgset();
    }


    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                selectImage();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }



    //storing the images in firebase storage

    private void uploadImage1() {

        if(image_no<1)
            storefinal();

        else{
        if(filepath1 != null)
        {
            refimg2 = mstoragrref.child("image1");

            refimg2.putFile(filepath1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            refimg2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageurl1= uri.toString();
                                        uploadImage2();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(AdpostActivity.this, "Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AdpostActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
        }
    }


    private void uploadImage2() {

        if(image_no<2)
            storefinal();

        else {

        if(filepath2 != null)
        {
            refimg2 = mstoragrref.child("image2");

            refimg2.putFile(filepath2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            refimg2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageurl2= uri.toString();
                                        uploadImage3();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(AdpostActivity.this, "Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AdpostActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
        }
    }

    private void uploadImage3() {

        if(image_no<3)
            storefinal();

        else {
        if(filepath3 != null)
        {
            refimg2 = mstoragrref.child("image3");

            refimg2.putFile(filepath3)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            refimg2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageurl3= uri.toString();
                                    uploadImage4();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(AdpostActivity.this, "Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AdpostActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
        }
    }

    private void uploadImage4() {

        if(image_no<4)
            storefinal();

        else
        {
        if(filepath4 != null)
        {
            refimg2 = mstoragrref.child("image4");

            refimg2.putFile(filepath4)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            refimg2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageurl4= uri.toString();
                                    storefinal();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(AdpostActivity.this, "Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AdpostActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
        }
    }



    //post ad

    public void Postad(){
        posturad=findViewById(R.id.posturad);

        posturad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verph==0)
                    Verify_signincode();

                else
                    Storedata();

            }
        });
    }

    //storing the data in firebase database

    public void Storedata(){

        progressDialog.setMessage("Posting Ad...");
        progressDialog.show();


        String brand_s=brand.getText().toString();
        String model_s=model.getText().toString();
        String year_s=year.getText().toString();
        String condition_s=condition.getText().toString();
        String address_s=address.getText().toString();
        String addetails_s=addetails.getText().toString();
        String price_s=price.getText().toString();
        String adtitle_s=adtitle.getText().toString();
        String inst=autoTextView.getText().toString();
        String email_s=email_id.getText().toString();
        String mobile_s=mobile_no.getText().toString();
        String name_s=name.getText().toString();

        if(email_s.isEmpty())
        {
            progressDialog.dismiss();
            email_id.setError("Email Id is required");
            email_id.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(mobile_s.isEmpty())
        {
            progressDialog.dismiss();
            mobile_no.setError("Mobile is required");
            mobile_no.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(name_s.isEmpty())
        {
            progressDialog.dismiss();
            name.setError("Username is required");
            name.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(brand_s.isEmpty())
        {
            progressDialog.dismiss();
            brand.setError("Product Name is required");
            brand.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(inst.isEmpty())
        {
            progressDialog.dismiss();
            autoTextView.setError("Name of Institute is required");
            autoTextView.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(!clg.contains(inst))
        {
            progressDialog.dismiss();
            autoTextView.setError("Institute does not exists");
            autoTextView.requestFocus();
            return;
        }

        if(model_s.isEmpty())
        {
            progressDialog.dismiss();
            model.setError("Model Name is required");
            model.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(addetails_s.isEmpty())
        {
            progressDialog.dismiss();
            addetails.setError("Ad Details is required");
            addetails.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(year_s.isEmpty())
        {
            progressDialog.dismiss();
            year.setError("Year of Purchase is required");
            year.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(condition_s.isEmpty())
        {
            progressDialog.dismiss();
            condition.setError("Condition is required");
            condition.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(address_s.isEmpty())
        {
            progressDialog.dismiss();
            address.setError("Address is required");
            address.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(price_s.isEmpty())
        {
            progressDialog.dismiss();
            price.setError("Price is required");
            price.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(adtitle_s.isEmpty())
        {
            progressDialog.dismiss();
            adtitle.setError("Ad Title is required");
            adtitle.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(image_no==0)
        {
            Snackbar snackbar=Snackbar.make(findViewById(R.id.coordinate),"Add an Image",Snackbar.LENGTH_LONG);

            View snackview=snackbar.getView();
            snackview.setBackgroundColor(Color.rgb(255, 99, 71));
            TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
            progressDialog.dismiss();
            return;
        }

        ad_numbergiver2();

    }



    public void ad_numbergiver2() {

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                totalAd_no= dataSnapshot.getValue(String.class);
                int x = Integer.parseInt(totalAd_no);
                x++;

                DatabaseReference current_user;
                String y=Integer.toString(x);

                current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child("tad_no");
                current_user.setValue(y);
                mstoragrref= FirebaseStorage.getInstance().getReference().child("All Ads").child(totalAd_no);

                uploadImage1();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }

    public void storefinal(){

        Map<String,String> newuser=new HashMap<>();

        String brand_s=brand.getText().toString();
        String model_s=model.getText().toString();
        String include_s=includes.getText().toString();
        String year_s=year.getText().toString();
        String condition_s=condition.getText().toString();
        String address_s=address.getText().toString();
        String addetails_s=addetails.getText().toString();
        String price_s=price.getText().toString();
        String adtitle_s=adtitle.getText().toString();
        String postcat_s=postcat.getText().toString();
        String postsubcat_s=postsubcat.getText().toString();
        String email_s=email_id.getText().toString();
        String mobile_s=mobile_no.getText().toString();
        String name_s=name.getText().toString();
        String userid=firebaseAuth.getCurrentUser().getUid();
        String inst=autoTextView.getText().toString();

        newuser.put("address",address_s);
        newuser.put("brand",brand_s);
        newuser.put("ad_title",adtitle_s);
        newuser.put("ad_details",addetails_s);
        newuser.put("year",year_s);
        newuser.put("includes",include_s);
        newuser.put("condition",condition_s);
        newuser.put("model",model_s);
        newuser.put("price",price_s);
        newuser.put("category",postcat_s);
        newuser.put("sub_category",postsubcat_s);
        newuser.put("email_id",email_s);
        newuser.put("mobile",mobile_s);
        newuser.put("name",name_s);
        newuser.put("verified","0");
        newuser.put("sold","0");
        newuser.put("posted","0");
        newuser.put("image1",imageurl1);
        newuser.put("image2",imageurl2);
        newuser.put("image3",imageurl3);
        newuser.put("image4",imageurl4);
        newuser.put("userid",userid);
        newuser.put("institute",inst);

        DatabaseReference current_user;
        int x;
        String y;

        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(totalAd_no);
        current_user.setValue(newuser);

        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("Posted Ad").child(userad_no);
        current_user.setValue(totalAd_no);

        x=Integer.parseInt(userad_no);
        x++;

        y=Integer.toString(x);

        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("Posted Ad").child("ad_no");
        current_user.setValue(y);

        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("roll");
        current_user.setValue(address_s);

        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("mobile");
        current_user.setValue(mobile_s);

        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("institute");
        current_user.setValue(inst);

        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("email");
        current_user.setValue(email_s);

        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("username");
        current_user.setValue(name_s);

        Toast.makeText(getApplicationContext(), "Ad Posted", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(AdpostActivity.this, Activity3.class);
        startActivity(intent);
        progressDialog.dismiss();
        finish();

    }

    public void ad_numbergiver() {

        progressBar.setVisibility(View.VISIBLE);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userad_no= dataSnapshot.getValue(String.class);
                progressBar.setVisibility(View.GONE);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onBackPressed() {

        imageStoragePath="0";
        imageStoragePath2="0";
        imageStoragePath3="0";
        imageStoragePath4="0";

        super.onBackPressed();
    }

    public void clgset(){

        DatabaseReference ref;
        ref=FirebaseDatabase.getInstance().getReference().child("Institutes");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                    clg.add(dataSnapshot1.getValue(String.class));
                }

                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.hint_completion_layout,R.id.tvHintCompletion,clg);

                autoTextView.setThreshold(1);
                autoTextView.setAdapter(adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(AdpostActivity.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

}
