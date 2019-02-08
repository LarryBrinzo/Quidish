package com.quidish.anshgupta.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Random;

public class selladapter extends RecyclerView.Adapter<selladapter.MyHoder>{

    List<usermodel> list;
    Context context;
    FirebaseUser fuser;
    FirebaseDatabase firebaseDatabase;
    String userid;

    selladapter(List<usermodel> list, Context context) {
        this.list = list;
        this.context = context;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fuser = firebaseAuth.getCurrentUser();

        if(fuser!=null)
            userid= firebaseAuth.getCurrentUser().getUid();

    }

    @NonNull
    @Override
    public MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.usercard,parent,false);

        return new MyHoder(view);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, final int position) {
        final usermodel mylist = list.get(position);

        holder.msgnos=mylist.getMsgno();

        Random rand = new Random();
        int x=rand.nextInt(4);

        if(x==0)
            holder.profile.setBackgroundResource(R.drawable.cir_borderimg);
        else if(x==1)
            holder.profile.setBackgroundResource(R.drawable.cir_borderimg2);
        else if(x==2)
            holder.profile.setBackgroundResource(R.drawable.cir_borderimg3);
        else if(x==3)
            holder.profile.setBackgroundResource(R.drawable.cir_borderimg4);

        if(holder.msgnos.equals("0"))
            holder.nmsgno.setVisibility(View.GONE);

        else
        {
            holder.nmsgno.setText(mylist.getMsgno());
            holder.time.setTextColor(Color.parseColor("#23C5A0"));
        }

        holder.imgic.setVisibility(View.GONE);

        if(mylist.getMsg().equals("Image"))
            holder.imgic.setVisibility(View.VISIBLE);

        if(mylist.getMsg().length()>=23 && mylist.getMsg().substring(0,23).equals("https://firebasestorage"))
            holder.imgic.setVisibility(View.VISIBLE);

        holder.time.setText(mylist.getTime());
        holder.msg.setText(mylist.getMsg());
        holder.name.setText(mylist.getName());

        if(mylist.getMsg().length()>=23 && mylist.getMsg().substring(0,23).equals("https://firebasestorage"))
            holder.msg.setText("Image");

        holder.ad_no=mylist.getAdno();
        holder.usertype=mylist.getType();
        holder.uid=mylist.getUserid();

        holder.uname=mylist.getName();
        String fname=holder.uname.substring(0,1);

        holder.profile.setText(fname);

        Glide.with(context).load(mylist.getImg()).into(holder.adpic);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,MessageActivity.class);
                intent.putExtra("ad_no", holder.ad_no);
                intent.putExtra("usertype", holder.usertype);
                intent.putExtra("act", "0");
                intent.putExtra("uid", holder.uid);
                intent.putExtra("name", holder.uname);
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

        ImageView adpic,imgic;
        TextView name,time,msg,nmsgno,profile;
        LinearLayout card;
        String ad_no,usertype,uid,uname,msgnos;

        MyHoder(View itemView) {
            super(itemView);

            adpic=itemView.findViewById(R.id.pic);
            card=itemView.findViewById(R.id.card);
            name=itemView.findViewById(R.id.name);
            time=itemView.findViewById(R.id.time);
            msg=itemView.findViewById(R.id.msg);
            nmsgno=itemView.findViewById(R.id.newmsg);
            profile=itemView.findViewById(R.id.profile_image);
            imgic=itemView.findViewById(R.id.imgic);

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


