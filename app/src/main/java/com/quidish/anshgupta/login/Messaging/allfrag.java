package com.quidish.anshgupta.login.Messaging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link allfrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link allfrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class allfrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public allfrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment soldfrag.
     */
    // TODO: Rename and change types and number of parameters
    public static allfrag newInstance(String param1, String param2) {
        allfrag fragment = new allfrag();
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
   // ProgressDialog progressDialog;
    DatabaseReference databaseReference,reference;
    List<usermodel> alllist=new ArrayList<>();
    RecyclerView recycle;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view= inflater.inflate(R.layout.fragment_allfrag, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle =view.findViewById(R.id.recycle);
        emptyact=view.findViewById(R.id.emptyinact);

        recycle.setNestedScrollingEnabled(false);

        emptyact.setVisibility(View.GONE);

        userid= Objects.requireNonNull(myfba.getCurrentUser()).getUid();
        mstoragrref= FirebaseStorage.getInstance().getReference();


        addall();

        return view;
    }

    public void addall() {

//        progressDialog.setTitle("Loading...");
//        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Allmsg");

        Query query = databaseReference.orderByChild("chtime");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                alllist.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String advalue=dataSnapshot1.getKey();

                    if(advalue==null)
                        continue;

                    String type=dataSnapshot1.child("type").getValue(String.class);

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

                    if(type!=null && type.equals("seller"))
                        fire.setUserid(splited[2]);
                    else
                        fire.setUserid(dataSnapshot1.child("selleruid").getValue(String.class));

                    fire.setAdno(splited[0]+" "+splited[1]);

                    alllist.add(fire);

                }

                Collections.reverse(alllist);
                Adshowall();
                // progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                //   progressDialog.dismiss();
            }
        });
    }

    public void Adshowall(){
        recycle.removeAllViewsInLayout();

        if(alllist.size()==0)
            emptyact.setVisibility(View.VISIBLE);

        else {
            emptyact.setVisibility(View.GONE);
        selladapter recyclerAdapter = new selladapter(alllist,getContext());
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
