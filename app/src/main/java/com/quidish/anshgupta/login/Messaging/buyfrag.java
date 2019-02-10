package com.quidish.anshgupta.login.Messaging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quidish.anshgupta.login.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link buyfrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link buyfrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class buyfrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public buyfrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment buyfrag.
     */
    // TODO: Rename and change types and number of parameters
    public static buyfrag newInstance(String param1, String param2) {
        buyfrag fragment = new buyfrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FirebaseAuth myfba;
    FirebaseDatabase firebaseDatabase;
    StorageReference mstoragrref;
    String userid;
    FirebaseUser fuser;
    LinearLayout emptyact;
    DatabaseReference databaseReference,reference;
    List<usermodel> buylist=new ArrayList<>();
    RecyclerView recycle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view= inflater.inflate(R.layout.fragment_buyfrag, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle =view.findViewById(R.id.recycle);
        emptyact=view.findViewById(R.id.emptyinact);

        recycle.setNestedScrollingEnabled(false);

        emptyact.setVisibility(View.GONE);

        userid=myfba.getCurrentUser().getUid();
        mstoragrref= FirebaseStorage.getInstance().getReference();

    //    progressDialog =new ProgressDialog(getContext(),R.style.AppCompatAlertDialogStyle);

        addbuy();

        return view;

    }



        public void addbuy() {

//        progressDialog.setTitle("Loading...");
//        progressDialog.show();

            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("Allmsg");

            Query query = databaseReference.orderByChild("chtime");
            query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                buylist.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String advalue=dataSnapshot1.getKey();

                    if(advalue==null)
                        continue;

                    String type=dataSnapshot1.child("type").getValue(String.class);

                    if(type != null && type.equals("seller"))
                        continue;

                    String[] splited = advalue.split(" ");

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    String toddate = df.format(Calendar.getInstance().getTime());

                    String date=dataSnapshot1.child("time").getValue(String.class);
                    assert date != null;
                    String date2=date.substring(0,10);

                    String timest;

                    if(toddate.equals(date2))
                        timest=date.substring(11,16)+" "+date.substring(20,22);

                    else
                        timest=date2;

                    usermodel fire=new usermodel();

                    fire.setMsg(dataSnapshot1.child("lastmsg").getValue(String.class));
                    fire.setMsgno(dataSnapshot1.child("msgno").getValue(String.class));
                    fire.setType(dataSnapshot1.child("type").getValue(String.class));
                    fire.setTime(timest);
                    fire.setImg(dataSnapshot1.child("img").getValue(String.class));
                    fire.setName(dataSnapshot1.child("name").getValue(String.class));
                    fire.setAd_no(splited[0]);
                    fire.setUserid(dataSnapshot1.child("selleruid").getValue(String.class));

                    buylist.add(fire);

                }

                Collections.reverse(buylist);
                Adshowbuy();
                //progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

              //  progressDialog.dismiss();
            }
        });
    }

        public void Adshowbuy(){
        recycle.removeAllViewsInLayout();

            if(buylist.size()==0)
                emptyact.setVisibility(View.VISIBLE);

            else {
                emptyact.setVisibility(View.GONE);
        selladapter recyclerAdapter = new selladapter(buylist,getContext());
        RecyclerView.LayoutManager recyce = new GridLayoutManager(getContext(),1);
        recycle.setLayoutManager(recyce);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);}

      //  progressDialog.dismiss();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
