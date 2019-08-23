package com.quidish.anshgupta.login.MyAccount;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quidish.anshgupta.login.AdDiscriptionActivity;
import com.quidish.anshgupta.login.AdModel;
import com.quidish.anshgupta.login.Home.BottomNavifation.HomeFragment;
import com.quidish.anshgupta.login.Messaging.MessageActivity;
import com.quidish.anshgupta.login.R;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyHoder>{

    private List<AdModel> list;
    private Context context;
    private FirebaseUser fuser;
    private DatabaseReference ref;
    private String userid;

    public WishlistAdapter(List<AdModel> list, Context context) {

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

        View view = LayoutInflater.from(context).inflate(R.layout.wishlistcard,parent,false);
        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {
        final AdModel mylist = list.get(position);
        holder.price.setText(mylist.getPrice());
        holder.adtitle.setText(mylist.getTitle());
        holder.brand.setText(mylist.getBrand());

        Glide.with(context).asBitmap().load(mylist.getUrl1()).into(holder.image1);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, AdDiscriptionActivity.class);
                intent.putExtra("ad_no", list.get(position).getAdno());
                context.startActivity(intent);
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref= FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("wish").child(list.get(position).getAdno());
                ref.setValue(null);

                HomeFragment.listwish.remove(list.get(position).getAdno());

                list.remove(position);
            }
        });

        holder.bag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("ad_no",list.get(position).getAdno());
                intent.putExtra("usertype","buyer");
                intent.putExtra("act","1");
                intent.putExtra("uid","0");
                intent.putExtra("name",list.get(position).getName());
                context.startActivity(intent);
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
        TextView price,adtitle,brand;//,sold;
        ImageView image1;
        LinearLayout card,cancel,bag;


        MyHoder(View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            adtitle= itemView.findViewById(R.id.addetails);
            brand=itemView.findViewById(R.id.brand);
            image1 =itemView.findViewById(R.id.image);
            card=itemView.findViewById(R.id.card_view);
            cancel=itemView.findViewById(R.id.cancel);
            bag=itemView.findViewById(R.id.bag);
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


