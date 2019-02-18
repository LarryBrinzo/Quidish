package com.quidish.anshgupta.login.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quidish.anshgupta.login.AdDiscriptionActivity;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.LoginRegister.LoginSignupactivity;
import com.quidish.anshgupta.login.R;

import java.util.List;

public class UserAdsAdapter extends RecyclerView.Adapter<UserAdsAdapter.MyHoder>{

    private List<AdModel> list;
    private Context context;
    private FirebaseUser fuser;
    private DatabaseReference ref;
    private String userid;
    private String addetails_s,ad_no,address,brand,email,name,mobile,price_s,pic1,pic2,pic3,pic4,adtitle_s,model,includes,year,condition;
    private String activity,wish_no;
    private String soldno;
    private String uid;

    UserAdsAdapter(List<AdModel> list, Context context) {
        this.list = list;
        this.context = context;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        fuser = firebaseAuth.getCurrentUser();

        if(fuser!=null)
            userid= firebaseAuth.getCurrentUser().getUid();

    }

    @NonNull
    @Override
    public MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.vertical_adview_card,parent,false);
        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {
        final AdModel mylist = list.get(position);
        holder.price.setText(mylist.getPrice());
        holder.adtitle.setText(mylist.getTitle());
        holder.brand.setText(mylist.getBrand());
        //holder.yearc.setText(mylist.getYear());

        Glide.with(context).asBitmap().load(mylist.getUrl1()).into(holder.image1);

//        addetails_s=(mylist.getDetails());
//        address=(mylist.getAddress());
//        brand=(mylist.getBrand());
//        email=(mylist.getEmail());
//        adtitle_s=(mylist.getTitle());
//        model=(mylist.getModel());
//        includes=(mylist.getIncludes());
//        year=(mylist.getYear());
//        condition=(mylist.getCondition());
//        name=(mylist.getName());
//        mobile=(mylist.getMobile());
//        price_s=(mylist.getPrice());
//        pic1=(mylist.getUrl1());
//        pic2=(mylist.getUrl2());
//        pic3=(mylist.getUrl3());
//        pic4=(mylist.getUrl4());
//        ad_no=mylist.getAdno();
//        activity=mylist.getActivity();
//        wish_no =mylist.getWish();
//        soldno=mylist.getSold();
//        uid=mylist.getUser();

//        if(activity.equals("1") || wish_no.equals("1"))
//        {
//            holder.wishr.setVisibility(View.VISIBLE);
//            holder.wish.setVisibility(View.GONE);
//        }
//
//        else
//        {
//            holder.wish.setVisibility(View.VISIBLE);
//            holder.wishr.setVisibility(View.GONE);
//        }
//
//        if(activity.equals("3"))
//        {
//            holder.wish.setVisibility(View.GONE);
//            holder.wishr.setVisibility(View.GONE);
//        }
//
//
//        if(soldno.equals("1") && activity.equals("1")) {
//
//            holder.price.setTextColor(Color.rgb(192,192,192));
//            holder.price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//           // holder.sold.setVisibility(View.VISIBLE);
//        }


//        holder.card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(HomeActivity.listwish.contains(ad_no))
//                    wish_no ="1";
//
//                else
//                    wish_no ="0";
//
//                Intent intent=new Intent(context,AdDiscriptionActivity.class);
//                intent.putExtra("addetails", addetails_s);
//                intent.putExtra("address", address);
//                intent.putExtra("brand", brand);
//                intent.putExtra("email", email);
//                intent.putExtra("name", name);
//                intent.putExtra("mobile", mobile);
//                intent.putExtra("price", price_s);
//                intent.putExtra("adtitle", adtitle_s);
//                intent.putExtra("model", model);
//                intent.putExtra("includes", includes);
//                intent.putExtra("year", year);
//                intent.putExtra("condition", condition);
//                intent.putExtra("pic1", pic1);
//                intent.putExtra("pic2", pic2);
//                intent.putExtra("pic3", pic3);
//                intent.putExtra("pic4", pic4);
//                intent.putExtra("ad_no", ad_no);
//                intent.putExtra("wish_no", wish_no);
//                intent.putExtra("activity", activity);
//                intent.putExtra("sold", soldno);
//                intent.putExtra("userid", uid);
//                context.startActivity(intent);
//            }
//        });

//        holder.wish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(fuser==null)
//                {
//                    Intent intent=new Intent(context,LoginSignupactivity.class);
//                    context.startActivity(intent);
//                }
//
//                else
//                {
//                    ref=FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("wish").child(ad_no);
//
//                    holder.wishr.setVisibility(View.VISIBLE);
//                    holder.wish.setVisibility(View.GONE);
//                    Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show();
//                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//                    assert v != null;
//                    v.vibrate(400);
//
//                    ref.setValue(ad_no);
//                    wish_no="1";
//
//                }
//
//            }
//        });
//
//        holder.wishr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(fuser==null)
//                {
//                    Intent intent=new Intent(context,LoginSignupactivity.class);
//                    context.startActivity(intent);
//
//                }
//
//                else
//                {
//                    ref=FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("wish").child(ad_no);
//
//                    ref.setValue(null);
//                    holder.wish.setVisibility(View.VISIBLE);
//                    holder.wishr.setVisibility(View.GONE);
//                    wish_no="0";
//
//                    if(activity.equals("1"))
//                    {
//                        list.remove(position);
//                    }
//
//                }
//            }
//        });


    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }



        }catch (Exception ignored){



        }

        return arr;

    }

    class MyHoder extends RecyclerView.ViewHolder{
        TextView price,adtitle,brand;//,sold;
        ImageView image1;
        LinearLayout card;
        ImageButton wish,wishr;


        MyHoder(View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            adtitle= itemView.findViewById(R.id.addetails);
            brand=itemView.findViewById(R.id.brand);
            image1 =itemView.findViewById(R.id.image);
           // yearc=itemView.findViewById(R.id.year);
            card=itemView.findViewById(R.id.card_view);
            wish=itemView.findViewById(R.id.wish);
            wishr=itemView.findViewById(R.id.wishr);
           // sold=itemView.findViewById(R.id.sold);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
