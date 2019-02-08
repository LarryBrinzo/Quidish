package com.quidish.anshgupta.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginSignupactivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    TextView login,signup;
    LinearLayout back;
    FirebaseAuth firebaseAuth,myfba;
    ImageButton gmail,fbook;
    private static final int RC_SIGN_IN = 234;
    CoordinatorLayout coordinatorLayout;
    private CallbackManager callbackManager;
    ProgressDialog progressDialog;
    GoogleSignInClient mGoogleSignInClient;
    LoginButton loginButton;
    String id,email,name;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_login_signupactivity);
        coordinatorLayout=findViewById(R.id.coordinator);

        checkConnection();

        fbook=findViewById(R.id.fbook);
        fbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);

        firebaseAuth=FirebaseAuth.getInstance();
        myfba=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),Activity3.class));
        }
        callbackManager = CallbackManager.Factory.create();

        loginButton =findViewById(R.id.login_button);

       // List< String > permissionNeeds = Arrays.asList("user_photos", "email",
         //       "user_birthday", "public_profile", "AccessToken");
        loginButton.registerCallback(callbackManager,
                new FacebookCallback < LoginResult > () {@Override
                public void onSuccess(LoginResult loginResult) {

               //     String accessToken = loginResult.getAccessToken()
                 //           .getToken();

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {@Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                try {
                                    id = object.getString("id");
                                    name = object.getString("name");
                                    email = object.getString("email");

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

        login=findViewById(R.id.login);
        signup=findViewById(R.id.signup);
        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSignupactivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSignupactivity.this, Activity2.class);
                startActivity(intent);
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
                                current_user.setValue(email);
                                current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("username");
                                current_user.setValue(name);

                                usercheck();


                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), Activity3.class));
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

                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), Activity3.class));
                            finish();
                        } else {
                            progressDialog.dismiss();

                        }

                        // ...
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
