package com.quidish.anshgupta.login.LoginRegister;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.quidish.anshgupta.login.Home.BottomNavigation.BottomNavigationDrawerActivity;
import com.quidish.anshgupta.login.R;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button submit;
    FirebaseAuth firebaseAuth;
    CoordinatorLayout coordinatorLayout;
    ProgressBar progress;
    int emailactive=0,passactive=0;
    TextView signup,forget;
    LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.frag_login);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        submit=findViewById(R.id.next);
        coordinatorLayout=findViewById(R.id.coordinator);
        signup=findViewById(R.id.signup);
        forget=findViewById(R.id.forget);
        back=findViewById(R.id.backbt);
        progress=findViewById(R.id.progress);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, ForgetPass.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, LoginSignupactivity.class);
                startActivity(intent);
            }
        });

        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()>0){
                    emailactive=1;
                }

                else
                    emailactive=0;

                if(emailactive==1 && passactive==1){
                    submit.setEnabled(true);
                    submit.setBackgroundResource(R.drawable.border3);
                    submit.setTextColor(Color.parseColor("#ffffff"));
                }

                else {
                    submit.setEnabled(false);
                    submit.setBackgroundResource(R.drawable.border8);
                    submit.setTextColor(Color.parseColor("#aeaeae"));
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
        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()>0){
                    passactive=1;
                }

                else
                    passactive=0;

                if(emailactive==1 && passactive==1){
                    submit.setEnabled(true);
                    submit.setBackgroundResource(R.drawable.border3);
                    submit.setTextColor(Color.parseColor("#ffffff"));
                }

                else {
                    submit.setEnabled(false);
                    submit.setBackgroundResource(R.drawable.border8);
                    submit.setTextColor(Color.parseColor("#aeaeae"));
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                userLogin(email.getText().toString(),password.getText().toString());
            }
        });

    }
    public void userLogin(String mail_id,String pass_wd){

        firebaseAuth=FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(mail_id,pass_wd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            Intent intent = new Intent(LoginActivity.this, BottomNavigationDrawerActivity.class);
                            startActivity(intent);
                            progress.setVisibility(View.GONE);
                            finish();

                        }

                        else {
                            try
                            {
                                throw Objects.requireNonNull(task.getException());

                            }

                            catch (FirebaseAuthInvalidUserException invalidEmail)
                            {

                                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Invalid Email ID",Snackbar.LENGTH_LONG);

                                progress.setVisibility(View.GONE);

                                View snackview=snackbar.getView();
                                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                                TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);

                                snackbar.show();
                            }

                            catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                            {
                                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Wrong Password",Snackbar.LENGTH_LONG);

                                progress.setVisibility(View.GONE);

                                View snackview=snackbar.getView();
                                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                                TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);

                                snackbar.show();
                            }
                            catch (Exception e)
                            {
                                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Invalid Email ID",Snackbar.LENGTH_LONG);
                                progress.setVisibility(View.GONE);

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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}