package com.quidish.anshgupta.login.Home.Searching;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.quidish.anshgupta.login.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.MyHoder>{

    private List<String> list;
    private Context context;

    SuggestionAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.suggestionlayout,parent,false);
        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {

        holder.suggestion.setText(list.get(position));

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor searchhint = pref.edit();

                int serachnumber=pref.getInt("serachnumber", 0)+1;
                String srchitm="recent"+Integer.toString(serachnumber);
                searchhint.putString(srchitm,list.get(position));

                Intent intent=new Intent(context,SearchShowActivity.class);
                intent.putExtra("searchstring", list.get(position));
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
        TextView suggestion;
        LinearLayout cardview;

        MyHoder(View itemView) {
            super(itemView);
            suggestion = itemView.findViewById(R.id.sugg);
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

