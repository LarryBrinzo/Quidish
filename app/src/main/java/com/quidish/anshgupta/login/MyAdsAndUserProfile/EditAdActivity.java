package com.quidish.anshgupta.login.MyAdsAndUserProfile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;

public class EditAdActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText brand,adtitle,price,model,includes,year,condition,des_layout;
    ProgressDialog progressDialog;
    Button edit;
    LinearLayout back;
    FirebaseDatabase firebaseDatabase;
    String ad_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);


        checkConnection();

        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);
        model=findViewById(R.id.model);
        includes=findViewById(R.id.includes);
        year=findViewById(R.id.year);
        condition=findViewById(R.id.condition);
        brand=findViewById(R.id.brand);
        price=findViewById(R.id.price);
        adtitle=findViewById(R.id.adtitle);
        edit=findViewById(R.id.edit);
        des_layout = findViewById(R.id.addetails);
        firebaseDatabase = FirebaseDatabase.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
                ad_no=bundle.getString("ad_no");
                addetailsset(ad_no);
        }

        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editdata();
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
                price.setText("₹"+dataSnapshot.child("price").getValue(String.class));
                brand.setText(dataSnapshot.child("brand").getValue(String.class));
                model.setText(dataSnapshot.child("model").getValue(String.class));
                includes.setText(dataSnapshot.child("includes").getValue(String.class));
                year.setText(dataSnapshot.child("year").getValue(String.class));
                condition.setText(dataSnapshot.child("condition").getValue(String.class));
                des_layout.setText(dataSnapshot.child("ad_details").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }



    public void editdata(){
        progressDialog.setMessage("Editing Ad...");
        progressDialog.show();

        String u_adtitle=adtitle.getText().toString();
        String u_addetails=des_layout.getText().toString();
        String u_price=price.getText().toString();
        String u_model=model.getText().toString();
        String u_brand=brand.getText().toString();
        String u_year=year.getText().toString();
        String u_cond=condition.getText().toString();
        String u_inc=includes.getText().toString();

        if(u_adtitle.isEmpty())
        {
            progressDialog.dismiss();
            adtitle.setError("Email Id is required");
            adtitle.requestFocus();
            return;
        }
        if(u_addetails.isEmpty())
        {
            progressDialog.dismiss();
            des_layout.setError("Email Id is required");
            des_layout.requestFocus();
            return;
        }
        if(u_price.isEmpty())
        {
            progressDialog.dismiss();
            price.setError("Email Id is required");
            price.requestFocus();
            return;
        }
        if(u_model.isEmpty())
        {
            progressDialog.dismiss();
            model.setError("Email Id is required");
            model.requestFocus();
            return;
        }
        if(u_brand.isEmpty())
        {
            progressDialog.dismiss();
            brand.setError("Email Id is required");
            brand.requestFocus();
            return;
        }
        if(u_year.isEmpty())
        {
            progressDialog.dismiss();
            year.setError("Email Id is required");
            year.requestFocus();
            return;
        }
        if(u_cond.isEmpty())
        {
            progressDialog.dismiss();
            condition.setError("Email Id is required");
            condition.requestFocus();
            return;
        }


        DatabaseReference current_user;

        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(ad_no).child("ad_details");
        current_user.setValue(u_addetails);
        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(ad_no).child("ad_title");
        current_user.setValue(u_adtitle);
        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(ad_no).child("brand");
        current_user.setValue(u_brand);
        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(ad_no).child("condition");
        current_user.setValue(u_cond);
        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(ad_no).child("includes");
        current_user.setValue(u_inc);
        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(ad_no).child("model");
        current_user.setValue(u_model);
        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(ad_no).child("price");
        current_user.setValue(u_price.substring(2));
        current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(ad_no).child("year");
        current_user.setValue(u_year);

        progressDialog.dismiss();
        finish();
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
