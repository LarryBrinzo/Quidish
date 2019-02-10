package com.quidish.anshgupta.login.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Home.HomeActivity;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Button signin,forgpass;
    EditText email,pass;
    TextView signup,skip;
    FirebaseAuth firebaseAuth,myfba;
    ProgressBar progressBar;
    CoordinatorLayout coordinatorLayout;
    ProgressDialog progressDialog;
    ImageButton gmail,fbook;
    private static final int RC_SIGN_IN = 234;
    private CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    LoginButton loginButton;
    String fid,femail,fname;
    LinearLayout back;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_main);

        coordinatorLayout=findViewById(R.id.coordinator);
        checkConnection();

        fbook=findViewById(R.id.fbook);
        fbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        loginButton =findViewById(R.id.login_button);

       // List< String > permissionNeeds = Arrays.asList("user_photos", "email",
         //       "user_birthday", "public_profile", "AccessToken");
        loginButton.registerCallback(callbackManager,
                new FacebookCallback < LoginResult > () {@Override
                public void onSuccess(LoginResult loginResult) {

                  //  String accessToken = loginResult.getAccessToken()
                    //        .getToken();

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {@Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                try {
                                    fid = object.getString("id");
                                    fname = object.getString("name");
                                    femail = object.getString("email");

                                } catch (JSONException e) {
                                }
                            }
                            });

                    handleFacebookAccessToken(loginResult.getAccessToken());
                    Bundle parameters = new Bundle();
                    parameters.putString("fields",
                            "id,name,email,gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });


        firebaseAuth=FirebaseAuth.getInstance();
        myfba=FirebaseAuth.getInstance();


       if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        email=findViewById(R.id.email);
        pass=findViewById(R.id.pass);

        signin=findViewById(R.id.signin);
        signup=findViewById(R.id.signup);
        forgpass=findViewById(R.id.forget);
        progressBar=findViewById(R.id.prog1);
        back=findViewById(R.id.backbt);
        skip=findViewById(R.id.skip);
        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);

        gmail=findViewById(R.id.gmail);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        forgpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(LoginActivity.this, ForgetPass.class);
                startActivity(intent);
                progressBar.setVisibility(View.GONE);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersignin();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openA();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        progressDialog.setMessage("Signing In...");
        progressDialog.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fuser = myfba.getCurrentUser();

                            if(fuser==null)
                                return;

                            String userid=firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference current_user;

                            current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("email");
                            current_user.setValue(femail);
                            current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("username");
                            current_user.setValue(fname);

                            usercheck();


                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {

                            progressDialog.dismiss();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            //if the requestCode is the Google Sign In code that we defined at starting
            if (requestCode == RC_SIGN_IN) {

                //Getting the GoogleSignIn Task
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    //Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    progressDialog.setMessage("Signing In...");
                    progressDialog.show();

                    //authenticating with firebase
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    progressDialog.dismiss();
                }
            }

            else
                callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                          //  FirebaseUser user = firebaseAuth.getCurrentUser();

                            fuser = myfba.getCurrentUser();

                            if(fuser==null)
                                return;

                            String userid=firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference current_user;

                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            if (acct != null) {
                                String personName = acct.getDisplayName();
                                String personEmail = acct.getEmail();

                                current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("email");
                                current_user.setValue(personEmail);
                                current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("username");
                                current_user.setValue(personName);

                                usercheck();
                            }

                            Snackbar snackbar=Snackbar.make(coordinatorLayout,"User Signed In",Snackbar.LENGTH_LONG);

                            progressDialog.dismiss();
                            View snackview=snackbar.getView();
                            snackview.setBackgroundColor(Color.rgb(50, 50, 50));
                            TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);

                            snackbar.show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar snackbar=Snackbar.make(coordinatorLayout,"Authentication Failed",Snackbar.LENGTH_LONG);

                            progressDialog.dismiss();
                            View snackview=snackbar.getView();
                            snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                            TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);

                            snackbar.show();

                        }

                        // ...
                    }
                });
    }

    public void openA(){

        progressBar.setVisibility(View.VISIBLE);

        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
        finish();
        progressBar.setVisibility(View.GONE);
    }

    public void usersignin(){

        progressDialog.setMessage("Logging In");
        progressDialog.show();

         String mail_id=email.getText().toString();
         String pass_wd=pass.getText().toString();

         if(mail_id.isEmpty()){
             progressDialog.dismiss();
             email.setError("Email ID is required");
             email.requestFocus();
             return;
         }

        if(pass_wd.isEmpty()){
            progressDialog.dismiss();
            pass.setError("Email ID is required");
            pass.requestFocus();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(mail_id,pass_wd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();

                        }

                        else {
                            try
                            {
                                throw Objects.requireNonNull(task.getException());

                            }

                            catch (FirebaseAuthInvalidUserException invalidEmail)
                            {
                                progressDialog.dismiss();
                                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Invalid Email ID",Snackbar.LENGTH_LONG);


                                View snackview=snackbar.getView();
                                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                                TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);

                                snackbar.show();
                            }

                            catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                            {
                                progressDialog.dismiss();
                                Snackbar snackbar=Snackbar.make(coordinatorLayout,"Wrong Password",Snackbar.LENGTH_LONG);


                                View snackview=snackbar.getView();
                                snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                                TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);

                                snackbar.show();
                            }
                            catch (Exception e)
                            {
                                progressDialog.dismiss();
                               showSnackbar();
                            }
                        }
                    }
                });

    }

    public void showSnackbar(){
        Snackbar snackbar=Snackbar.make(coordinatorLayout,"Invalid Email ID",Snackbar.LENGTH_LONG);


        View snackview=snackbar.getView();
        snackview.setBackgroundColor(Color.rgb(255, 99, 71));
        TextView  textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();

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


    public void usercheck(){

        fuser = myfba.getCurrentUser();

        if(fuser==null)
            return;

        String userid=firebaseAuth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(userid).child("verification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                } else {

                    DatabaseReference current_user;
                    String userid=firebaseAuth.getCurrentUser().getUid();

                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("verification");
                    current_user.setValue("0");
                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("Posted Ad").child("ad_no");
                    current_user.setValue("1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
