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

        View view = LayoutInflater.from(context).inflate(R.layout.card,parent,false);
        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {
        final AdModel mylist = list.get(position);
        holder.price.setText(mylist.getPrice());
        holder.adtitle.setText(mylist.getTitle());
        holder.yearc.setText(mylist.getYear());

        Glide.with(context).asBitmap().load(mylist.getUrl1()).into(holder.image1);

        holder.addetails_s=(mylist.getDetails());
        holder.address=(mylist.getAddress());
        holder.brand=(mylist.getBrand());
        holder.email=(mylist.getEmail());
        holder.adtitle_s=(mylist.getTitle());
        holder.model=(mylist.getModel());
        holder.includes=(mylist.getIncludes());
        holder.year=(mylist.getYear());
        holder.condition=(mylist.getCondition());
        holder.name=(mylist.getName());
        holder.mobile=(mylist.getMobile());
        holder.price_s=(mylist.getPrice());
        holder.pic1=(mylist.getUrl1());
        holder.pic2=(mylist.getUrl2());
        holder.pic3=(mylist.getUrl3());
        holder.pic4=(mylist.getUrl4());
        holder.ad_no=mylist.getAdno();
        holder.activity=mylist.getActivity();
        holder.wish_no=mylist.getWish();
        holder.soldno=mylist.getSold();
        holder.uid=mylist.getUser();

        if(holder.activity.equals("1") || holder.wish_no.equals("1"))
        {
            holder.wishr.setVisibility(View.VISIBLE);
            holder.wish.setVisibility(View.GONE);
        }

        else
        {
            holder.wish.setVisibility(View.VISIBLE);
            holder.wishr.setVisibility(View.GONE);
        }

        if(holder.activity.equals("3"))
        {
            holder.wish.setVisibility(View.GONE);
            holder.wishr.setVisibility(View.GONE);
        }


        if(holder.soldno.equals("1") && holder.activity.equals("1")) {

            holder.price.setTextColor(Color.rgb(192,192,192));
            holder.price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.sold.setVisibility(View.VISIBLE);
        }


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(HomeActivity.listwish.contains(holder.ad_no))
                    holder.wish_no="1";

                else
                    holder.wish_no="0";

                Intent intent=new Intent(context,AdDiscriptionActivity.class);
                intent.putExtra("addetails", holder.addetails_s);
                intent.putExtra("address", holder.address);
                intent.putExtra("brand", holder.brand);
                intent.putExtra("email", holder.email);
                intent.putExtra("name", holder.name);
                intent.putExtra("mobile", holder.mobile);
                intent.putExtra("price", holder.price_s);
                intent.putExtra("adtitle", holder.adtitle_s);
                intent.putExtra("model", holder.model);
                intent.putExtra("includes", holder.includes);
                intent.putExtra("year", holder.year);
                intent.putExtra("condition", holder.condition);
                intent.putExtra("pic1", holder.pic1);
                intent.putExtra("pic2", holder.pic2);
                intent.putExtra("pic3", holder.pic3);
                intent.putExtra("pic4", holder.pic4);
                intent.putExtra("ad_no", holder.ad_no);
                intent.putExtra("wish_no", holder.wish_no);
                intent.putExtra("activity", holder.activity);
                intent.putExtra("sold", holder.soldno);
                intent.putExtra("userid", holder.uid);
                context.startActivity(intent);
            }
        });

        holder.wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser==null)
                {
                    Intent intent=new Intent(context,LoginSignupactivity.class);
                    context.startActivity(intent);
                }

                else
                {
                    ref=FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("wish").child(holder.ad_no);

                    holder.wishr.setVisibility(View.VISIBLE);
                    holder.wish.setVisibility(View.GONE);
                    Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    assert v != null;
                    v.vibrate(400);

                    ref.setValue(holder.ad_no);
                    holder.wish_no="1";

                }

            }
        });

        holder.wishr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fuser==null)
                {
                    Intent intent=new Intent(context,LoginSignupactivity.class);
                    context.startActivity(intent);

                }

                else
                {
                    ref=FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("wish").child(holder.ad_no);

                    ref.setValue(null);
                    holder.wish.setVisibility(View.VISIBLE);
                    holder.wishr.setVisibility(View.GONE);
                    holder.wish_no="0";

                    if(holder.activity.equals("1"))
                    {
                        list.remove(position);
                    }

                }
            }
        });


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
        TextView price,adtitle,yearc,sold;
        ImageView image1;
        String addetails_s,ad_no,address,brand,email,name,mobile,price_s,pic1,pic2,pic3,pic4,adtitle_s,model,includes,year,condition;
        LinearLayout card;
        String activity,wish_no,soldno,uid;
        ImageButton wish,wishr;


        MyHoder(View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            adtitle= itemView.findViewById(R.id.addetails);
            image1 =itemView.findViewById(R.id.image);
            yearc=itemView.findViewById(R.id.year);
            card=itemView.findViewById(R.id.card_view);
            wish=itemView.findViewById(R.id.wish);
            wishr=itemView.findViewById(R.id.wishr);
            sold=itemView.findViewById(R.id.sold);
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
