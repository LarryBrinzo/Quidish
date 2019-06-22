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
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyHoder>{

    private List<String> list;
    private Context context;

    public CategoriesAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.category_item_layout,parent,false);
        return new MyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {

        holder.suggestion.setText(list.get(position));

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<String> list2;
                list2=SelectCatagoryActivity.listofcategories.get(list.get(position));

                if(SelectPictureActivity.Ad_category==null)
                    SelectPictureActivity.Ad_category=list.get(position);
                else
                    SelectPictureActivity.Ad_category=SelectPictureActivity.Ad_category+" "+list.get(position);

                if(list2!=null && !list2.isEmpty()){
                    Intent intent=new Intent(context,SubCategoryActivity.class);
                    intent.putExtra("category",list.get(position));
                    context.startActivity(intent);
                }

                else {
                    Intent intent=new Intent(context,AdDiscriptionActivity.class);
                    context.startActivity(intent);
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

