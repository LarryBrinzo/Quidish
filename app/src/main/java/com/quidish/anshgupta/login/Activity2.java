package com.quidish.anshgupta.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;


public class Activity2 extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    Button submit;
    EditText email,roll,mobile,fullname,confirmpass,pass;
    FirebaseAuth myfba;
    ProgressBar progressBar;
    LinearLayout simpletext,clicklay;
    CheckBox terms;
    TextView login,skip;
    CoordinatorLayout coordinatorLayout;
    ProgressDialog progressDialog;
    private static final int RC_SIGN_IN = 234;
    private CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    ImageButton gmail,fbook;
    LoginButton loginButton;
    String fid,femail,fname;
    TextView tandc;
    LinearLayout back;
    List<String> clg = new ArrayList<>();
    AutoCompleteTextView autoTextView;
    FirebaseUser fuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_2);

        fbook=findViewById(R.id.fbook);

        coordinatorLayout=findViewById(R.id.coordinator);
        checkConnection();

        clgset();

        fbook=findViewById(R.id.fbook);
        fbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        loginButton =findViewById(R.id.login_button);

        //List< String > permissionNeeds = Arrays.asList("user_photos", "email",
               // "user_birthday", "public_profile", "AccessToken");
        loginButton.registerCallback(callbackManager,
                new FacebookCallback < LoginResult > () {@Override
                public void onSuccess(LoginResult loginResult) {

                   //String accessToken = loginResult.getAccessToken()
                     //       .getToken();

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {@Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                try {
                                    fid = object.getString("id");
                                    fname = object.getString("name");
                                    femail = object.getString("email");

                                } catch (JSONException ignored) {
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







        myfba=FirebaseAuth.getInstance();
        coordinatorLayout=findViewById(R.id.coordinator);

        if (myfba.getCurrentUser() != null) {
            startActivity(new Intent(this, Activity3.class));
            finish();
        }

        email=findViewById(R.id.email);
        roll=findViewById(R.id.roll);
        mobile=findViewById(R.id.mobile);
        fullname=findViewById(R.id.fullname);
        confirmpass=findViewById(R.id.confirmpass);
        pass=findViewById(R.id.pass);
        progressBar=findViewById(R.id.prog);
        simpletext=findViewById(R.id.simpletext);
        clicklay=findViewById(R.id.clicklay);
        back=findViewById(R.id.backbt);
        skip=findViewById(R.id.skip);
        login=findViewById(R.id.login);
        progressDialog =new ProgressDialog(this,R.style.MyAlertDialogStyle);
        gmail=findViewById(R.id.gmail);
        tandc=findViewById(R.id.tandc);
        autoTextView = findViewById(R.id.autocompleteEditTextView);

        tandc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                openURL.setData(Uri.parse("http://quidish.blogspot.com/p/quidish.html"));
                startActivity(openURL);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        submit=findViewById(R.id.sub);
        terms=findViewById(R.id.terms);

        progressBar.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Activity3.class);
                startActivity(intent);
                finish();
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                  Create_user();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
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
        myfba.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fuser = myfba.getCurrentUser();

                            if(fuser==null)
                                return;

                            String userid=myfba.getCurrentUser().getUid();
                            DatabaseReference current_user;

                            current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("email");
                            current_user.setValue(femail);
                            current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("username");
                            current_user.setValue(fname);

                            usercheck();


                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), Activity3.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                        }

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
        myfba.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            fuser = myfba.getCurrentUser();

                            if(fuser==null)
                                return;

                            String userid=myfba.getCurrentUser().getUid();
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
                            startActivity(new Intent(getApplicationContext(), Activity3.class));
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

    public void Create_user() {

        progressDialog.setMessage("Creating Account");
        progressDialog.show();


        String mail_id = email.getText().toString();
        String pass_wd = pass.getText().toString();
        String roll_no = roll.getText().toString();
        String contact = mobile.getText().toString();
        String confirm_pass = confirmpass.getText().toString();
        String full_name = fullname.getText().toString();
        String inst=autoTextView.getText().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(mail_id).matches()){
            progressDialog.dismiss();
            email.setError("Enter valid Email ID");
            email.requestFocus();
            return;
        }

        if(pass_wd.length()<6)
        {
            progressDialog.dismiss();
            pass.setError("Password is too short");
            pass.requestFocus();
            return;
        }

        if(contact.length()!=10)
        {
            progressDialog.dismiss();
            mobile.setError("Enter valid Mobile no.");
            mobile.requestFocus();
            return;

        }

        if(roll_no.isEmpty())
        {
            progressDialog.dismiss();
            roll.setError("Address is required");
            roll.requestFocus();
            return;
        }

        if(contact.isEmpty())
        {
            progressDialog.dismiss();
            mobile.setError("Mobile is required");
            mobile.requestFocus();
            return;
        }

        if(full_name.isEmpty())
        {
            progressDialog.dismiss();
            fullname.setError("UserName is required");
            fullname.requestFocus();
            return;
        }

        if(inst.isEmpty())
        {
            progressDialog.dismiss();
            autoTextView.setError("Name of Institute is required");
            autoTextView.requestFocus();
            return;
        }

        if(!clg.contains(inst))
        {
            progressDialog.dismiss();
            autoTextView.setError("Institute does not exists");
            autoTextView.requestFocus();
            return;
        }

        if(confirm_pass.isEmpty())
        {
            progressDialog.dismiss();
            confirmpass.setError("Password is required");
            confirmpass.requestFocus();
            return;
        }

        if(!confirm_pass.equals(pass_wd))
        {
            progressDialog.dismiss();
            confirmpass.setError("Confirm Password did not match ");
            confirmpass.requestFocus();
            return;
        }

        if(mail_id.isEmpty())
        {
            progressDialog.dismiss();
            email.setError("Email ID is required");
            email.requestFocus();
            return;
        }

        if(pass_wd.isEmpty())
        {
            progressDialog.dismiss();
            email.setError("Password is required");
            email.requestFocus();
            return;
        }

        if(terms.isChecked())
        {

        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(Activity2.this, "Checkbox not marked", Toast.LENGTH_LONG).show();
            return;
        }

        myfba.createUserWithEmailAndPassword(mail_id,pass_wd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    fuser = myfba.getCurrentUser();

                    if(fuser==null)
                        return;

                    String userid=myfba.getCurrentUser().getUid();
                    DatabaseReference current_user;

                    String mail_id = email.getText().toString();
                    String pass_wd = pass.getText().toString();
                    String roll_no = roll.getText().toString();
                    String contact = mobile.getText().toString();
                    String full_name = fullname.getText().toString();
                    String inst=autoTextView.getText().toString();

                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("email");
                    current_user.setValue(mail_id);
                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("password");
                    current_user.setValue(pass_wd);
                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("roll");
                    current_user.setValue(roll_no);
                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("mobile");
                    current_user.setValue(contact);
                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("institute");
                    current_user.setValue(inst);
                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("username");
                    current_user.setValue(full_name);
                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("verification");
                    current_user.setValue("0");
                    current_user= FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("Posted Ad").child("ad_no");
                    current_user.setValue("1");

                    Snackbar snackbar=Snackbar.make(coordinatorLayout,"User Signed In",Snackbar.LENGTH_LONG);

                    View snackview=snackbar.getView();
                    snackview.setBackgroundColor(Color.rgb(50, 50, 50));
                    TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);

                    snackbar.show();

                    Intent intent = new Intent(Activity2.this, Activity3.class);
                    startActivity(intent);
                    finish();
                   progressDialog.dismiss();


                } else {

                    try {
                        throw Objects.requireNonNull(task.getException());
                    }

                     catch (FirebaseAuthWeakPasswordException weakPassword)
                    {
                        progressDialog.dismiss();
                        pass.setError("Weak Password");
                        pass.requestFocus();
                        return;
                    }

                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                    {
                        progressDialog.dismiss();
                        email.setError("Invalid Email ID");
                        email.requestFocus();
                        return;
                    }
                    catch (FirebaseAuthUserCollisionException existEmail)
                    {
                        progressDialog.dismiss();
                        showSnackbar();
                    }
                    catch (Exception e)
                    {
                        progressDialog.dismiss();
                        email.setError("Invalid Email ID");
                        email.requestFocus();
                        return;
                    }

                }

                progressDialog.dismiss();
            }
        });

    }

    public void showSnackbar(){
        Snackbar snackbar=Snackbar.make(coordinatorLayout,"Email ID already exists",Snackbar.LENGTH_LONG);


        View snackview=snackbar.getView();
        snackview.setBackgroundColor(Color.rgb(255, 99, 71));
        TextView textView=snackview.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void usercheck(){

        fuser = myfba.getCurrentUser();

        if(fuser==null)
            return;

        String userid=myfba.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(userid).child("verification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                } else {

                    DatabaseReference current_user;
                    String userid=myfba.getCurrentUser().getUid();

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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.hint_completion_layout,R.id.tvHintCompletion,clg);

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