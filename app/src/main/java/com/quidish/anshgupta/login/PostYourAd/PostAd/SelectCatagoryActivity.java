package com.quidish.anshgupta.login.PostYourAd.PostAd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quidish.anshgupta.login.PostYourAd.ImageEditor.ImageCropActivity;
import com.quidish.anshgupta.login.PostYourAd.ImageEditor.ImageEditorActivity;
import com.quidish.anshgupta.login.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCatagoryActivity extends AppCompatActivity {

    RecyclerView horzontalrecycle,categories;
    ImageAdapter imageAdapter;
    public static Map< String,List<String>> listofcategories = new HashMap<>();
    CategoriesAdapter categoriesAdapter;
    int H,W;
    EditText search;
    ProgressBar searchprog;
    ImageView cancel;
    public static ArrayList<Bitmap> finalSelectedFilepath =new ArrayList<>();
    public static ArrayList<String> searchlist =new ArrayList<>();
    List<String> list;
    LinearLayout back;

    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_catagory);

        fa = this;

        horzontalrecycle=findViewById(R.id.images);
        categories=findViewById(R.id.categories);
        search=findViewById(R.id.search);
        cancel=findViewById(R.id.cancel);
        searchprog=findViewById(R.id.searchprog);
        back=findViewById(R.id.backbt);

        listofcategories.clear();
        searchlist.clear();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setText("");
            }
        });

        finalSelectedFilepath.clear();

        categories.setNestedScrollingEnabled(false);

        horzontalrecycle.setNestedScrollingEnabled(false);

        H=this.getWindow().getDecorView().getHeight();
        W=this.getWindow().getDecorView().getWidth();

        addAllCategories();

        list=listofcategories.get("Categories");

        finalSelectedFilepath.clear();

        for(int i=0;i<SelectPictureActivity.SelectedFilepath.size();i++){
            finalSelectedFilepath.add(BitmapFactory.decodeFile(SelectPictureActivity.SelectedFilepath.get(i)));
            finalSelectedFilepath.set(i,getResizedBitmap(finalSelectedFilepath.get(i), 500));
        }

        categoriesAdapter = new CategoriesAdapter(list,SelectCatagoryActivity.this);
        RecyclerView.LayoutManager recyceSugg2 = new GridLayoutManager(SelectCatagoryActivity.this,1);
        categories.setLayoutManager(recyceSugg2);
        recyceSugg2.setAutoMeasureEnabled(false);
        categories.setItemAnimator( new DefaultItemAnimator());
        categories.setAdapter(categoriesAdapter);

        imageAdapter = new ImageAdapter(finalSelectedFilepath,SelectCatagoryActivity.this);
        RecyclerView.LayoutManager recyce = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        horzontalrecycle.setLayoutManager(recyce);
        horzontalrecycle.setLayoutManager(recyce);
        horzontalrecycle.setAdapter(imageAdapter);

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence st, int start, int before, int count) {

                String newText = st.toString();

                if(newText.length()==0){

                    list.clear();

                    list.add("Electronics");
                    list.add("Mobile Phones & Tablets");
                    list.add("Women's Fashion");
                    list.add("Men's Fashion");

                    categories.removeAllViewsInLayout();
                    categoriesAdapter.notifyDataSetChanged();
                    searchprog.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                }

                newText=newText.trim();
                newText=newText.toLowerCase();

                if(newText.length()>0) {

                    int ct=0;

                    searchprog.setVisibility(View.VISIBLE);

                    for (int i=0;i<searchlist.size();i++) {

                            if(searchlist.get(i).length()> newText.length() && (searchlist.get(i).toLowerCase().startsWith(newText))){

                                if(ct==0){
                                    ct=1;

                                    list.clear();
                                    categories.removeAllViewsInLayout();
                                    categoriesAdapter.notifyDataSetChanged();
                                }

                                String itm=searchlist.get(i);

                                if(list.size()==0)
                                    list.add(itm);

                                else
                                    list.add(0,itm);
                            }

                            else if(searchlist.get(i).length()> newText.length() && (searchlist.get(i).toLowerCase().contains(newText))){

                                if(ct==0){
                                    ct=1;

                                    list.clear();
                                    categories.removeAllViewsInLayout();
                                    categoriesAdapter.notifyDataSetChanged();
                                }

                                String itm=searchlist.get(i);

                                list.add(itm);
                            }

                            else {
                                String[] splited = newText.split(" ");

                                for (String aSplited : splited) {

                                    if (searchlist.get(i).length() > aSplited.length() && searchlist.get(i).toLowerCase().contains(aSplited)){

                                        if(ct==0){
                                            ct=1;

                                            list.clear();
                                            categories.removeAllViewsInLayout();
                                            categoriesAdapter.notifyDataSetChanged();
                                        }

                                        String itm=searchlist.get(i);

                                       list.add(itm);
                                    }
                                }
                            }
                    }

                    searchprog.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);

                }

            }


        });

    }


    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyHoder>{

        private List<Bitmap> list;
        private Context context;

        ImageAdapter(List<Bitmap> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public ImageAdapter.MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(context).inflate(R.layout.category_image_layout,parent,false);
            return new MyHoder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("CheckResult")
        @Override
        public void onBindViewHolder(@NonNull final ImageAdapter.MyHoder holder, @SuppressLint("RecyclerView") final int position) {

            RequestOptions options = new RequestOptions();
            options.centerCrop();

            holder.image.setImageBitmap(list.get(position));
            holder.image.setClipToOutline(true);

            holder.card.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(getApplicationContext(),ImageCropActivity.class);
                    i.putExtra("height",H);
                    i.putExtra("width",W);
                    i.putExtra("position",position);
                    context.startActivity(i);
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
            ImageView image;
            ConstraintLayout card;

            MyHoder(View itemView) {
                super(itemView);

                image =itemView.findViewById(R.id.image);
                card=itemView.findViewById(R.id.card_view);
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

     @Override
    protected void onResume() {
        super.onResume();
         imageAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("SeletedFilepaths",SelectPictureActivity.SelectedFilepath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        SelectPictureActivity.SelectedFilepath = savedInstanceState.getStringArrayList("SeletedFilepaths");

        for(int i=0;i<SelectPictureActivity.SelectedFilepath.size();i++){
            finalSelectedFilepath.add(BitmapFactory.decodeFile(SelectPictureActivity.SelectedFilepath.get(i)));
            finalSelectedFilepath.set(i,getResizedBitmap(finalSelectedFilepath.get(i), 500));
        }
    }


    public void addAllCategories(){

        List<String> list = new ArrayList<>();
        list.add("Audio");
        list.add("Computers");
        list.add("Computer Parts and Accessories");
        list.add("Electronics & Gadgets - Others");
        listofcategories.put("Electronics",list);

        searchlist.add("Audio");
        searchlist.add("Computers");
        searchlist.add("Computer Parts and Accessories");
        searchlist.add("Electronics & Gadgets - Others");

        list=new ArrayList<>();
        list.add("Electronics");
        list.add("Mobile Phones & Tablets");
        list.add("Women's Fashion");
        list.add("Men's Fashion");
        listofcategories.put("Categories",list);

        searchlist.add("Electronics");
        searchlist.add("Mobile Phones & Tablets");
        searchlist.add("Women's Fashion");
        searchlist.add("Men's Fashion");

        list=new ArrayList<>();
        list.add("Laptops");
        list.add("Desktops");
        list.add("Others");
        listofcategories.put("Computers",list);

        searchlist.add("Laptops");
        searchlist.add("Desktops");
        searchlist.add("Others");

        list=new ArrayList<>();
        list.add("iphones");
        list.add("Android Phones");
        list.add("Tablets");
        list.add("Mobile & Tablet Accessories");
        list.add("Mobile Phones & Tablets - Others");
        listofcategories.put("Mobile Phones & Tablets",list);

        searchlist.add("iphones");
        searchlist.add("Android Phones");
        searchlist.add("Tablets");
        searchlist.add("Mobile & Tablet Accessories");
        searchlist.add("Mobile Phones & Tablets - Others");

        list=new ArrayList<>();
        list.add("iphones X series");
        list.add("iphones 8 series");
        list.add("iphones 7 series");
        list.add("iphones 6 series");
        list.add("Others");
        listofcategories.put("iphones",list);

        searchlist.add("iphones X series");
        searchlist.add("iphones 8 series");
        searchlist.add("iphones 7 series");
        searchlist.add("iphones 6 series");
        searchlist.add("Others");

        list=new ArrayList<>();
        list.add("Samsung");
        list.add("Sony");
        list.add("Xiaomi");
        list.add("LG");
        list.add("OPPO");
        list.add("Google Pixel");
        list.add("Others");
        listofcategories.put("Android Phones",list);

        searchlist.add("Samsung");
        searchlist.add("Sony");
        searchlist.add("Xiaomi");
        searchlist.add("LG");
        searchlist.add("OPPO");
        searchlist.add("Google Pixel");
        searchlist.add("Others");

        list=new ArrayList<>();
        list.add("Cases & Sleeves");
        list.add("Power Banks & Chargers");
        list.add("Mobile Accessories");
        listofcategories.put("Mobile & Tablet Accessories",list);

        searchlist.add("Cases & Sleeves");
        searchlist.add("Power Banks & Chargers");
        searchlist.add("Mobile Accessories");

        list=new ArrayList<>();
        list.add("Women's Watches");
        list.add("Women's Bags & Wallets");
        list.add("Women's Shoes");
        list.add("Women's Jewellery");
        list.add("Women's Accessories");
        list.add("Women's Clothes");
        listofcategories.put("Women's Fashion",list);

        searchlist.add("Women's Watches");
        searchlist.add("Women's Bags & Wallets");
        searchlist.add("Women's Shoes");
        searchlist.add("Women's Jewellery");
        searchlist.add("Women's Accessories");
        searchlist.add("Women's Clothes");

        list=new ArrayList<>();
        list.add("Handbags");
        list.add("Backpacks");
        list.add("Sling Bags");
        list.add("Clutches");
        list.add("Wallets");
        list.add("Others");
        listofcategories.put("Women's Bags & Wallets",list);

        searchlist.add("Handbags");
        searchlist.add("Backpacks");
        searchlist.add("Sling Bags");
        searchlist.add("Clutches");
        searchlist.add("Wallets");
        searchlist.add("Others");

        list=new ArrayList<>();
        list.add("Earrings");
        list.add("Bracelets");
        list.add("Necklaces");
        list.add("Rings");
        list.add("Brooches");
        list.add("Others");
        listofcategories.put("Women's Jewellery",list);

        searchlist.add("Earrings");
        searchlist.add("Bracelets");
        searchlist.add("Necklaces");
        searchlist.add("Rings");
        searchlist.add("Brooches");
        searchlist.add("Others");

        list=new ArrayList<>();
        list.add("Belts");
        list.add("Scarves & Shawls");
        list.add("Eyewear & Sunglasses");
        list.add("Caps & Hats");
        list.add("Hair Accessories");
        list.add("Socks & Hosiery");
        list.add("Others");
        listofcategories.put("Women's Accessories",list);

        searchlist.add("Belts");
        searchlist.add("Scarves & Shawls");
        searchlist.add("Eyewear & Sunglasses");
        searchlist.add("Caps & Hats");
        searchlist.add("Hair Accessories");
        searchlist.add("Socks & Hosiery");
        searchlist.add("Others");

        list=new ArrayList<>();
        list.add("Outerwear");
        list.add("Dresses & Skirts");
        list.add("Pants, Jeans & Shorts");
        list.add("Tops");
        list.add("Rompers & Jumpsuits");
        list.add("Others");
        listofcategories.put("Women's Clothes",list);

        searchlist.add("Outerwear");
        searchlist.add("Dresses & Skirts");
        searchlist.add("Pants, Jeans & Shorts");
        searchlist.add("Tops");
        searchlist.add("Rompers & Jumpsuits");
        searchlist.add("Others");

        list=new ArrayList<>();
        list.add("Men's Watches");
        list.add("Men's Footwear");
        list.add("Men's Bags & Wallets");
        list.add("Men's Accessories");
        list.add("Men's Clothes");
        listofcategories.put("Men's Fashion",list);

        searchlist.add("Men's Watches");
        searchlist.add("Men's Footwear");
        searchlist.add("Men's Bags & Wallets");
        searchlist.add("Men's Accessories");
        searchlist.add("Men's Clothes");

        list=new ArrayList<>();
        list.add("Formal Shoes");
        list.add("Sneakers");
        list.add("Slippers & Sandals");
        list.add("Boots");
        list.add("Others");
        listofcategories.put("Men's Footwear",list);

        searchlist.add("Formal Shoes");
        searchlist.add("Sneakers");
        searchlist.add("Slippers & Sandals");
        searchlist.add("Boots");
        searchlist.add("Others");

        list=new ArrayList<>();
        list.add("Backpacks");
        list.add("Briefcases");
        list.add("Sling Bags");
        list.add("Wallets");
        list.add("Others");
        listofcategories.put("Men's Bags & Wallets",list);

        searchlist.add("Backpacks");
        searchlist.add("Briefcases");
        searchlist.add("Sling Bags");
        searchlist.add("Wallets");
        searchlist.add("Others");

        list=new ArrayList<>();
        list.add("Belts");
        list.add("Ties & Formals");
        list.add("Eyewear & sunglasses");
        list.add("Caps & Hats");
        list.add("Socks");
        list.add("Others");
        listofcategories.put("Men's Accessories",list);

        searchlist.add("Belts");
        searchlist.add("Ties & Formals");
        searchlist.add("Eyewear & sunglasses");
        searchlist.add("Caps & Hats");
        searchlist.add("Socks");
        searchlist.add("Others");

        list=new ArrayList<>();
        list.add("Tops");
        list.add("Bottoms");
        list.add("Outerwear");
        list.add("Others");
        listofcategories.put("Men's Clothes",list);

        searchlist.add("Tops");
        searchlist.add("Bottoms");
        searchlist.add("Outerwear");
        searchlist.add("Others");
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
