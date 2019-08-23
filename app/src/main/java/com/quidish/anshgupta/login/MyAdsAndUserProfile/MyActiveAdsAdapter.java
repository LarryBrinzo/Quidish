package com.quidish.anshgupta.login.MyAdsAndUserProfile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.quidish.anshgupta.login.R;

import java.util.List;

public class MyActiveAdsAdapter extends RecyclerView.Adapter<MyActiveAdsAdapter.MyHoder>{

    List<AdModel> list;
    Context context;
    FirebaseAuth myfba;
    String userid;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser fuser;

     public MyActiveAdsAdapter(List<AdModel> list, Context context) {
        this.list = list;
        this.context = context;

        firebaseDatabase = FirebaseDatabase.getInstance();
        myfba=FirebaseAuth.getInstance();
        fuser = myfba.getCurrentUser();
//        progressDialog =new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);

        if(fuser!=null)
            userid=myfba.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.active_ads_card,parent,false);

        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {
        final AdModel mylist = list.get(position);
        holder.price.setText(mylist.getPrice());
        holder.adtitle.setText(mylist.getTitle());
        holder.brand.setText(mylist.getBrand());
        holder.itmtagtext.setText(mylist.getCondition().toUpperCase());

        if(list.get(position).getSold().equals("1"))
        {
            holder.comment.setText("Product Sold");
        }

        Glide.with(context).asBitmap().load(mylist.getUrl1()).into(holder.image1);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, MyAdDiscriptionActivity.class);
                intent.putExtra("ad_no", list.get(position).getAdno());
                context.startActivity(intent);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context,EditAdActivity.class);
                intent.putExtra("ad_no", list.get(position).getAdno());
                context.startActivity(intent);
            }
        });

        holder.sold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure. You want to Delete this Ad")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                         //   progressDialog.setTitle("Loading...");
                                           // progressDialog.show();

                                            String[] splited2 = list.get(position).getAdno().split(" ");
                                            DatabaseReference current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(splited2[0]).child(splited2[1]).child("sold");
                                            current_user.setValue("1");

                                            list.remove(position);
                                          //  progressDialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //progressDialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

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
        TextView price,adtitle,brand,itmtagtext,comment,likes,views;//,sold;
        ImageView image1;
        LinearLayout card,sold,delete,edit;


         MyHoder(View itemView) {
            super(itemView);
             price = itemView.findViewById(R.id.price);
             adtitle= itemView.findViewById(R.id.title);
             brand=itemView.findViewById(R.id.brand);
             image1 =itemView.findViewById(R.id.image);
             card=itemView.findViewById(R.id.card_view);
             itmtagtext=itemView.findViewById(R.id.itmtagtext);
             sold=itemView.findViewById(R.id.sold);
             comment=itemView.findViewById(R.id.comment);
             likes=itemView.findViewById(R.id.likes);
             views=itemView.findViewById(R.id.views);
             delete=itemView.findViewById(R.id.del);
             edit=itemView.findViewById(R.id.edit);
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
