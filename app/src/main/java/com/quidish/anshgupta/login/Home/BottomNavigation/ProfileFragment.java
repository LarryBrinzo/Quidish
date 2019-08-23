package com.quidish.anshgupta.login.Home.BottomNavigation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.Home.ContactUsActivity;
import com.quidish.anshgupta.login.Messaging.MyChatActivity;
import com.quidish.anshgupta.login.MyAccount.MyWishlistActivity;
import com.quidish.anshgupta.login.MyAdsAndUserProfile.MyAdsActivity;
import com.quidish.anshgupta.login.PostYourAd.PostAd.LocationSetActivity;
import com.quidish.anshgupta.login.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ProfileFragment extends Fragment {

    LinearLayout myads,wishlist,chats,support,rateus,addresses,logout;
    TextView email_id,name,mobile;
    DatabaseReference databaseReference;
    FirebaseAuth myfba;
    String mapdate,userid;
    int fblog=0;
    FirebaseUser fuser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;
        view= inflater.inflate(R.layout.frag_profile, container, false);


        myfba=FirebaseAuth.getInstance();
        fuser=myfba.getCurrentUser();


        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            fblog=1;
        }

        myads=view.findViewById(R.id.listings);
        wishlist=view.findViewById(R.id.wishlist);
        chats=view.findViewById(R.id.chats);
        support=view.findViewById(R.id.contact);
        rateus=view.findViewById(R.id.rate);
        email_id=view.findViewById(R.id.email);
        name=view.findViewById(R.id.name);
        mobile=view.findViewById(R.id.mobile);
        addresses=view.findViewById(R.id.savedadd);
        logout=view.findViewById(R.id.logout);

        userid=myfba.getCurrentUser().getUid();

        linkaccount();

        myads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), MyAdsActivity.class);
                startActivity(intent);
            }
        });

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), MyWishlistActivity.class);
                startActivity(intent);
            }
        });

        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), MyChatActivity.class);
                startActivity(intent);
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), ContactUsActivity.class);
                startActivity(intent);
            }
        });

        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                openURL.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.quidish.anshgupta.login"));
                startActivity(openURL);
            }
        });

        addresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), LocationSetActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to logout your account from this device.")
                        .setCancelable(false)
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

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
                                current_user= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("lastseen");
                                current_user.setValue(mapdate);

                                myfba.signOut();
                                if(fblog==1)
                                    LoginManager.getInstance().logOut();


                                Intent intent=new Intent(getContext(), BottomNavigationDrawerActivity.class);
                                startActivity(intent);
                                getActivity().finish();
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

        return view;
    }

    public void linkaccount() {

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String user_email = dataSnapshot.child("Users").child(userid).child("Email_ID").getValue(String.class);
                String user_name = dataSnapshot.child("Users").child(userid).child("Full_Name").getValue(String.class);
                String user_mobile = dataSnapshot.child("Users").child(userid).child("Mobile").getValue(String.class);


                email_id.setText(user_email);
                name.setText(user_name);
                mobile.setText(user_mobile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

}
