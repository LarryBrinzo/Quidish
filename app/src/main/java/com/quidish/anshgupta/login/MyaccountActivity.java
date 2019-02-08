package com.quidish.anshgupta.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyaccountActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    LinearLayout myads_layout,wish_layout,details_layout,mychat_layout;
    Button logout;
    TextView email_id;
    DatabaseReference databaseReference;
    FirebaseAuth myfba;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    String uid,mapdate,userid;
    LinearLayout back;
    int fblog=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);

        checkConnection();

        myfba=FirebaseAuth.getInstance();

        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            fblog=1;
        }

        myads_layout=findViewById(R.id.myads_layout);
        wish_layout=findViewById(R.id.wish_layout);
        details_layout=findViewById(R.id.profile_layout);
        mychat_layout=findViewById(R.id.chat_layout);
        logout=findViewById(R.id.logout);
        email_id=findViewById(R.id.email);
        progressBar=findViewById(R.id.prog2);
        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);

        uid=myfba.getCurrentUser().getUid();
        userid=myfba.getCurrentUser().getUid();

        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        linkaccount();

        details_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ProfileDetailsActivity.class);
                startActivity(intent);
            }
        });

        myads_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MyAdsActivity.class);
                startActivity(intent);
            }
        });

        wish_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MyWishlistActivity.class);
                startActivity(intent);
            }
        });

        mychat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MyChatActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MyaccountActivity.this);
                builder.setMessage("Are you sure you want to logout your account from this device.")
                        .setCancelable(false)
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog.setMessage("Logging you out");
                                progressDialog.show();

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

                                myfba.signOut();
                                if(fblog==1)
                                    LoginManager.getInstance().logOut();
                                Intent intent=new Intent(getApplicationContext(),Activity3.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    public void linkaccount() {

        progressBar.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String user_email = dataSnapshot.child("users").child(uid).child("email").getValue(String.class);
                String user_name = dataSnapshot.child("users").child(uid).child("username").getValue(String.class);
                email_id.setText(user_email);

                if(user_email==null || user_email.equals(""))
                    email_id.setText(user_name);

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
