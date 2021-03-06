package com.quidish.anshgupta.login.PostYourAd.PostAd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quidish.anshgupta.login.R;
import com.quidish.anshgupta.login.SavedLocationModel;

import java.util.List;

public class SavedAddressAdapter extends RecyclerView.Adapter<SavedAddressAdapter.MyHoder>{

    private List<SavedLocationModel> list;
    private Context context;
    public static SavedLocationModel adaddressmodel;

    public SavedAddressAdapter(List<SavedLocationModel> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.saved_address_layout,parent,false);
        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {

        holder.nick.setText(list.get(position).getNickname());
        holder.address.setText(list.get(position).getCompadd());

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adaddressmodel=list.get(position);

                Intent intent=new Intent(context,DetailsVerificationActivity.class);
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
        TextView nick,address;
        LinearLayout cardview;

        MyHoder(View itemView) {
            super(itemView);
            nick = itemView.findViewById(R.id.nick);
            address = itemView.findViewById(R.id.address);
            cardview=itemView.findViewById(R.id.card_view);

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

