package com.quidish.anshgupta.login.LoginRegister;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quidish.anshgupta.login.Home.BottomNavifation.BottomNavigationDrawerActivity;
import com.quidish.anshgupta.login.Network.ConnectivityReceiver;
import com.quidish.anshgupta.login.Network.MyApplication;
import com.quidish.anshgupta.login.Network.No_InternetActivity;
import com.quidish.anshgupta.login.R;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginSignupactivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    LinearLayout facebook,gmail,email;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 234;
    FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    LoginButton loginButton;
    TextView login;
    FirebaseUser fuser;
    CoordinatorLayout coordinatorLayout;
    String femail,fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_loginsignup);
        coordinatorLayout=findViewById(R.id.coordinator);

        checkConnection();

        callbackManager = CallbackManager.Factory.create();
        loginButton =findViewById(R.id.login_button);

        facebook=findViewById(R.id.facebook);
        gmail=findViewById(R.id.google);
        email=findViewById(R.id.email2);
        firebaseAuth=FirebaseAuth.getInstance();
        coordinatorLayout=findViewById(R.id.coordinator);
        login=findViewById(R.id.login);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        loginButton.registerCallback(callbackManager,
                new FacebookCallback< LoginResult >() {@Override
                public void onSuccess(LoginResult loginResult) {

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {@Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                try {
                                    fname = object.getString("name");
                                    femail = object.getString("email");
                                } catch (JSONException e) {
                                }
                            }
                            });

                    handleFacebookAccessToken(loginResult.getAccessToken());
                    Bundle parameters = new Bundle();
                    parameters.putString("fields",
                            "id,name,email,gender,birthday");
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



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fuser = firebaseAuth.getCurrentUser();

                            if(fuser==null)
                                return;

                            String userid=firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            databaseReference.child("Email_ID").setValue(femail);
                            databaseReference.child("Full_Name").setValue(fname);

                            startActivity(new Intent(getApplicationContext(), BottomNavigationDrawerActivity.class));
                            finish();
                        } else {
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {
            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
            }
        }

        else
            callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            fuser = firebaseAuth.getCurrentUser();

                            if(fuser==null)
                                return;

                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            if (acct != null) {
                                String personName = acct.getDisplayName();
                                String personEmail = acct.getEmail();

                                String userid=firebaseAuth.getCurrentUser().getUid();
                                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                                databaseReference.child("Email_ID").setValue(personEmail);
                                databaseReference.child("Full_Name").setValue(personName);

                            }

                            Snackbar snackbar=Snackbar.make(coordinatorLayout,"User Signed In",Snackbar.LENGTH_LONG);

                            View snackview=snackbar.getView();
                            snackview.setBackgroundColor(Color.rgb(50, 50, 50));
                            TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);

                            snackbar.show();
                            startActivity(new Intent(getApplicationContext(), BottomNavigationDrawerActivity.class));
                            finish();
                        } else {

                            Snackbar snackbar=Snackbar.make(coordinatorLayout,"Authentication Failed",Snackbar.LENGTH_LONG);

                            View snackview=snackbar.getView();
                            snackview.setBackgroundColor(Color.rgb(255, 99, 71));
                            TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);

                            snackbar.show();

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
