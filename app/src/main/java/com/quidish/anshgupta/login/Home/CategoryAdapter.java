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
import com.quidish.anshgupta.login.Home.BottomNavifation.HomeFragment;
import com.quidish.anshgupta.login.LoginRegister.LoginSignupactivity;
import com.quidish.anshgupta.login.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyHoder>{

    private List<AdModel> list;
    private Context context;
    private FirebaseUser fuser;
    private DatabaseReference ref;
    private String userid;

    public CategoryAdapter(List<AdModel> list, Context context) {
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

        View view = LayoutInflater.from(context).inflate(R.layout.horizontal_view_card,parent,false);
        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {
        final AdModel mylist = list.get(position);
        holder.price.setText(mylist.getPrice());
        holder.adtitle.setText(mylist.getTitle());
        holder.brand.setText(mylist.getBrand());

        Glide.with(context).asBitmap().load(mylist.getUrl1()).into(holder.image1);

//        soldno=mylist.getSold();

        if(list.get(position).getWish().equals("1"))
        {
            holder.wishr.setVisibility(View.VISIBLE);
            holder.wish.setVisibility(View.GONE);
        }

        else
        {
            holder.wish.setVisibility(View.VISIBLE);
            holder.wishr.setVisibility(View.GONE);
        }

        if(list.get(position).getActivity().equals("3"))
        {
            holder.wish.setVisibility(View.GONE);
            holder.wishr.setVisibility(View.GONE);
        }


//        if(soldno.equals("1") && activity.equals("1")) {
//
//            holder.price.setTextColor(Color.rgb(192,192,192));
//            holder.price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//           // holder.sold.setVisibility(View.VISIBLE);
//        }


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context,AdDiscriptionActivity.class);
                intent.putExtra("ad_no", list.get(position).getAdno());
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
                    ref=FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("wish").child(list.get(position).getAdno());

                    holder.wishr.setVisibility(View.VISIBLE);
                    holder.wish.setVisibility(View.GONE);
                    Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show();
//                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//                    assert v != null;
//                    v.vibrate(400);
                    ref.setValue(list.get(position).getAdno());
                    HomeFragment.listwish.add(list.get(position).getAdno());
                    list.get(position).setWish("1");
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
                    ref=FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("wish").child(list.get(position).getAdno());

                    ref.setValue(null);
                    holder.wish.setVisibility(View.VISIBLE);
                    holder.wishr.setVisibility(View.GONE);
                    HomeFragment.listwish.remove(list.get(position).getAdno());
                    list.get(position).setWish("0");

                    if(list.get(position).getActivity().equals("1"))
                        list.remove(position);

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
