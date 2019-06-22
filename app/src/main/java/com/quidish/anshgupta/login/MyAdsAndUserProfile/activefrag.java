package com.quidish.anshgupta.login.MyAdsAndUserProfile;

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
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.Home.HomeActivity;
import com.quidish.anshgupta.login.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link activefrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link activefrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class activefrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public activefrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment activefrag.
     */
    // TODO: Rename and change types and number of parameters
    public static activefrag newInstance(String param1, String param2) {
        activefrag fragment = new activefrag();
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
    FirebaseUser fuser;
    String userid;
    DatabaseReference databaseReference;
    List<AdModel> listactive=new ArrayList<>();
    List<String> userads=new ArrayList<>();
    RecyclerView recycle;
    LinearLayout emptyin;
    int adaptersh;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view= inflater.inflate(R.layout.fragment_activefrag, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
        recycle =view.findViewById(R.id.recycle);
        emptyin=view.findViewById(R.id.emptyinact);

        recycle.setNestedScrollingEnabled(false);

        userid= UserProfile.GetData();

        if(userid==null){

        userid= MyAdsActivity.GetData();

        if(userid.equals("0"))
        {
            adaptersh=1;
            userid=myfba.getCurrentUser().getUid();
            listactive.clear();
            addproductad();
        }

        }

        else
        {
            adaptersh=2;
            listactive.clear();
            addproductad();
        }

        return view;
    }

    public void addproductad(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("Posted Ad");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String ad_no = dataSnapshot.child("ad_no").getValue(String.class);
                int x = Integer.parseInt(ad_no);
                x--;
                userads.clear();

                for(int i=x;i>0;i--) {

                    ad_no = Integer.toString(i);

                    String tad_no=dataSnapshot.child(ad_no).getValue(String.class);
                    userads.add(tad_no);

                }

                useradsort();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void useradsort(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int si=userads.size();
                listactive.clear();

                for(int i=si-1;i>=0;i--) {

                    String ad_no = userads.get(i);

                    String adsold = dataSnapshot.child(ad_no).child("sold").getValue(String.class);

                    if(adsold==null || adsold.equals("1"))
                        continue;

                    String adtitle = dataSnapshot.child(ad_no).child("ad_title").getValue(String.class);
                    String model = dataSnapshot.child(ad_no).child("model").getValue(String.class);
                    String include = dataSnapshot.child(ad_no).child("includes").getValue(String.class);
                    String year = dataSnapshot.child(ad_no).child("year").getValue(String.class);
                    String condition = dataSnapshot.child(ad_no).child("condition").getValue(String.class);
                    String addetails = dataSnapshot.child(ad_no).child("ad_details").getValue(String.class);
                    String price = dataSnapshot.child(ad_no).child("price").getValue(String.class);
                    String address = dataSnapshot.child(ad_no).child("address").getValue(String.class);
                    String brand = dataSnapshot.child(ad_no).child("brand").getValue(String.class);
                    String email = dataSnapshot.child(ad_no).child("email_id").getValue(String.class);
                    String mobile = dataSnapshot.child(ad_no).child("mobile").getValue(String.class);
                    String name = dataSnapshot.child(ad_no).child("name").getValue(String.class);
                    String image1 = dataSnapshot.child(ad_no).child("image1").getValue(String.class);
                    String image2 = dataSnapshot.child(ad_no).child("image2").getValue(String.class);
                    String image3 = dataSnapshot.child(ad_no).child("image3").getValue(String.class);
                    String image4 = dataSnapshot.child(ad_no).child("image4").getValue(String.class);
                    String usid = dataSnapshot.child(ad_no).child("userid").getValue(String.class);

                    price = "₹ " + price;

                    AdModel fire = new AdModel();

                    fire.setPrice(price);
                    fire.setTitle(adtitle);
                    fire.setModel(model);
                    fire.setIncludes(include);
                    fire.setYear(year);
                    fire.setCondition(condition);
                    fire.setDetails(addetails);
                    fire.setAddress(address);
                    fire.setBrand(brand);
                    fire.setEmail(email);
                    fire.setMobile(mobile);
                    fire.setName(name);
                    fire.setUrl1(image1);
                    fire.setUrl2(image2);
                    fire.setUrl3(image3);
                    fire.setUrl4(image4);
                    fire.setAdno(ad_no);
                    fire.setActivity("3");
                    fire.setSold("0");
                    fire.setUser(usid);

                    if(HomeActivity.listwish.contains(ad_no))
                        fire.setWish("1");

                    else
                        fire.setWish("0");

                    listactive.add(fire);

                }

                Adshowactive();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void Adshowactive(){

        emptyin.setVisibility(View.GONE);
        recycle.removeAllViewsInLayout();

        if(listactive.size()==0)
        {
            emptyin.setVisibility(View.VISIBLE);
        }

        else
        {
            emptyin.setVisibility(View.GONE);

            if(adaptersh==1){
            MyActiveAdsAdapter recyclerAdapter = new MyActiveAdsAdapter(listactive,getContext());
            RecyclerView.LayoutManager recyce = new GridLayoutManager(getContext(),1);
            recycle.setLayoutManager(recyce);
            recycle.setItemAnimator( new DefaultItemAnimator());
            recycle.setAdapter(recyclerAdapter);}

            else{
                UserAdsAdapter recyclerAdapter = new UserAdsAdapter(listactive,getContext());
                RecyclerView.LayoutManager recyce = new GridLayoutManager(getContext(),1);
                recycle.setLayoutManager(recyce);
                recycle.setItemAnimator( new DefaultItemAnimator());
                recycle.setAdapter(recyclerAdapter);
            }
        }

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
