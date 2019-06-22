package com.quidish.anshgupta.login.Home.Searching;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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
import com.quidish.anshgupta.login.Home.HomeActivity;
import com.quidish.anshgupta.login.LoginRegister.LoginSignupactivity;
import com.quidish.anshgupta.login.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyHoder>{

    private List<AdModel> list;
    private Context context;

    ItemAdapter(List<AdModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_card,parent,false);
        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {
        final AdModel mylist = list.get(position);

        if(list.get(position).getAdno().equals("-1")){
            holder.cardv.setVisibility(View.GONE);
        }

        else {

            holder.price.setText(mylist.getPrice());
            holder.adtitle.setText(mylist.getTitle());
            holder.brand.setText(mylist.getBrand());

            Glide.with(context).asBitmap().load(mylist.getUrl1()).into(holder.image1);

//        soldno=mylist.getSold();


//        if(soldno.equals("1") && activity.equals("1")) {
//
//            holder.price.setTextColor(Color.rgb(192,192,192));
//            holder.price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//           // holder.sold.setVisibility(View.VISIBLE);
//        }
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, AdDiscriptionActivity.class);
                    intent.putExtra("ad_no", list.get(position).getAdno());
                    context.startActivity(intent);
                }
            });

        }
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
        CardView cardv;

        MyHoder(View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            adtitle= itemView.findViewById(R.id.addetails);
            brand=itemView.findViewById(R.id.brand);
            image1 =itemView.findViewById(R.id.image);
            cardv=itemView.findViewById(R.id.card);
            card=itemView.findViewById(R.id.card_view);
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
