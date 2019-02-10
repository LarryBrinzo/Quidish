package com.quidish.anshgupta.login.MyAccount;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetailsActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText username,mobile,rollno,email;
    FirebaseAuth firebaseAuth;
    FirebaseUser fuser;
    StorageReference mstoragrref;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    TextView userid;
    Button save;
    String uid,umobile;
    List<String> clg = new ArrayList<>();
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    LinearLayout back;
    CoordinatorLayout coordinatorLayout;
    AutoCompleteTextView autoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        coordinatorLayout=findViewById(R.id.coordinator);
        checkConnection();

        clgset();

        username=findViewById(R.id.username);
        mobile=findViewById(R.id.mobile);
        rollno=findViewById(R.id.rollno);
        email=findViewById(R.id.email);
        userid=findViewById(R.id.userid);
        save=findViewById(R.id.save);
        progressBar=findViewById(R.id.prog2);
        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);
        autoTextView =  findViewById(R.id.autocompleteEditTextView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        fuser = firebaseAuth.getCurrentUser();
        mstoragrref= FirebaseStorage.getInstance().getReference();

        uid=firebaseAuth.getCurrentUser().getUid();

        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        linkaccount();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeaccount();
            }
        });


    }

    public void linkaccount() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_email = dataSnapshot.child("users").child(uid).child("email").getValue(String.class);
                String user_mobile = dataSnapshot.child("users").child(uid).child("mobile").getValue(String.class);
                String user_name = dataSnapshot.child("users").child(uid).child("username").getValue(String.class);
                String user_roll = dataSnapshot.child("users").child(uid).child("roll").getValue(String.class);
                String user_inst = dataSnapshot.child("users").child(uid).child("institute").getValue(String.class);

                umobile = user_mobile;

                username.setText(user_name);
                mobile.setText(user_mobile);
                rollno.setText(user_roll);
                email.setText(user_email);
                userid.setText(user_email);
                autoTextView.setText(user_inst);

                if (user_email == null || user_email.equals(""))
                    userid.setText(user_name);

                if (user_email != null && !user_email.equals(""))
                    email.setEnabled(false);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }

    public void changeaccount(){

        progressDialog.setMessage("Saving Details");
        progressDialog.show();

        String u_mobile=mobile.getText().toString();
        String u_roll=rollno.getText().toString();
        String u_name=username.getText().toString();
        String u_inst=autoTextView.getText().toString();
        String u_email=email.getText().toString();

        if(u_email.isEmpty())
        {
            progressDialog.dismiss();
            email.setError("Email Id is required");
            email.requestFocus();
            return;
        }

        if(!clg.contains(u_inst))
        {
            progressDialog.dismiss();
            autoTextView.setError("Institute does not exists");
            autoTextView.requestFocus();
            return;
        }

        if(u_inst.isEmpty())
        {
            progressDialog.dismiss();
            autoTextView.setError("Name of Institute is required");
            autoTextView.requestFocus();
            return;
        }

        if(u_mobile.length()!=10)
        {
            progressDialog.dismiss();
            mobile.setError("Enter valid Mobile no.");
            mobile.requestFocus();
            return;
        }

        if(u_roll.isEmpty())
        {
            progressDialog.dismiss();
            rollno.setError("Address is Required");
            rollno.requestFocus();
            return;
        }

        if(u_name.isEmpty())
        {
            progressDialog.dismiss();
            username.setError("UserName is Required");
            username.requestFocus();
            return;
        }

        DatabaseReference current_user;

        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("mobile");
        current_user.setValue(u_mobile);
        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("roll");
        current_user.setValue(u_roll);
        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("username");
        current_user.setValue(u_name);
        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("institute");
        current_user.setValue(u_inst);
        current_user= FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("email");
        current_user.setValue(u_email);

        if(!umobile.equals(u_mobile))
        {
            current_user= FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("verification");
            current_user.setValue("0");
        }

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
       // int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}
