package com.quidish.anshgupta.login.Home.BottomNavifation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quidish.anshgupta.login.LoginRegister.ForgetPass;
import com.quidish.anshgupta.login.LoginRegister.LoginActivity;
import com.quidish.anshgupta.login.LoginRegister.LoginSignupactivity;
import com.quidish.anshgupta.login.LoginRegister.RegisterActivity;
import com.quidish.anshgupta.login.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.Executor;


public class LoginFragment extends Fragment {

    EditText email,password;
    Button submit;
    FirebaseAuth firebaseAuth;
    CoordinatorLayout coordinatorLayout;
    ProgressBar progress;
    int emailactive=0,passactive=0;
    TextView signup,forget;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(this.getContext());

        View view;
        view= inflater.inflate(R.layout.frag_login, container, false);

        email=view.findViewById(R.id.email);
        password=view.findViewById(R.id.password);
        submit=view.findViewById(R.id.next);
        coordinatorLayout=view.findViewById(R.id.coordinator);
        signup=view.findViewById(R.id.signup);
        forget=view.findViewById(R.id.forget);
        progress=view.findViewById(R.id.progress);

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), ForgetPass.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginSignupactivity.class);
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

        return view;
    }

    public void userLogin(String mail_id,String pass_wd){

        firebaseAuth=FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(mail_id,pass_wd)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            Intent intent = new Intent(getContext(), BottomNavigationDrawerActivity.class);
                            startActivity(intent);
                            progress.setVisibility(View.GONE);

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


}


