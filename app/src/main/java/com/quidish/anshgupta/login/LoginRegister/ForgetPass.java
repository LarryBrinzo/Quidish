package com.quidish.anshgupta.login.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;

public class ForgetPass extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText email;
    Button reset;
    FirebaseAuth firebaseauth;
    CoordinatorLayout coordinatorLayout;
    ProgressDialog progressDialog;
    LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        firebaseauth=FirebaseAuth.getInstance();
        coordinatorLayout=findViewById(R.id.coordinator);

        coordinatorLayout=findViewById(R.id.coordinator);
        checkConnection();

        email=findViewById(R.id.email);
        back=findViewById(R.id.backbt);
        reset=findViewById(R.id.reset);
        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail_id=email.getText().toString();

                progressDialog.setMessage("Sending Reset Link");
                progressDialog.show();

                if(mail_id.isEmpty())
                {
                    progressDialog.dismiss();
                    email.setError("Enter valid Email ID");
                    email.requestFocus();
                }

                else {

                    firebaseauth.sendPasswordResetEmail(mail_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Password Reset Link has been sent to your mail",Snackbar.LENGTH_LONG);


                                View snackview=snackbar.getView();
                                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                                TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);

                                snackbar.show();
                            }
                            else{
                                progressDialog.dismiss();
                                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Account not registered",Snackbar.LENGTH_LONG);


                                View snackview=snackbar.getView();
                                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                                TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);

                                snackbar.show();
                            }
                        }
                    });
                }

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
