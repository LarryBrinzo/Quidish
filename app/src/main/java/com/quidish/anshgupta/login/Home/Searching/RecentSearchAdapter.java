package com.quidish.anshgupta.login.Home.Searching;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.quidish.anshgupta.login.R;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.MyHoder>{

    private List<Pair<String,Pair<String,String>>> list;
    private Context context;

    RecentSearchAdapter(List<Pair<String,Pair<String,String>>> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recentsearchcard,parent,false);
        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {

        char ch[] = list.get(position).second.first.toCharArray();
        for (int i = 0; i < list.get(position).second.first.length(); i++) {

            if (i == 0 && ch[i] != ' ' ||
                    ch[i] != ' ' && ch[i - 1] == ' ') {

                if (ch[i] >= 'a' && ch[i] <= 'z') {
                    ch[i] = (char)(ch[i] - 'a' + 'A');
                }

                else
                    ch[i]=ch[i];
            }

        }

        String st = new String(ch);

        holder.suggestion.setText(st);
        Glide.with(context)
                .load(list.get(position).second.second)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.img);


        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent=new Intent(context,SearchShowActivity.class);
                    intent.putExtra("searchstring", list.get(position).second.first);
                    intent.putExtra("click", "1");
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
        ImageView img;
        LinearLayout cardview;

        MyHoder(View itemView) {
            super(itemView);
            suggestion = itemView.findViewById(R.id.text);
            img=itemView.findViewById(R.id.img);
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

