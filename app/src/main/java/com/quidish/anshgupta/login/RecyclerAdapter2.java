package com.quidish.anshgupta.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RecyclerAdapter2 extends RecyclerView.Adapter<RecyclerAdapter2.MyHoder>{

    List<FireModel> list;
    Context context;
    FirebaseAuth myfba;
    String userid;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser fuser;
    DatabaseReference databaseReference;
   // ProgressDialog progressDialog;

     RecyclerAdapter2(List<FireModel> list, Context context) {
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

        View view = LayoutInflater.from(context).inflate(R.layout.card2,parent,false);

        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {
        FireModel mylist = list.get(position);
        holder.price.setText(mylist.getPrice());
        holder.adtitle.setText(mylist.getTitle());

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
        holder.ad_no=(mylist.getAdno());

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

                                            DatabaseReference current_user= FirebaseDatabase.getInstance().getReference().child("Ads").child(holder.ad_no).child("sold");
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

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ActivityMyAds.class);
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
                context.startActivity(intent);
            }
        });

        Glide.with(context).load(mylist.getUrl1()).into(holder.image1);

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
        TextView price,adtitle;
        ImageView image1;
        String addetails_s,address,brand,email,name,mobile,price_s,pic1,pic2,pic3,pic4,adtitle_s,model,includes,year,condition,ad_no;
        LinearLayout card;
        Button sold;


         MyHoder(View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            adtitle= itemView.findViewById(R.id.addetails);
            image1 =itemView.findViewById(R.id.image);
            card=itemView.findViewById(R.id.card_view);
            sold=itemView.findViewById(R.id.sub);
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
