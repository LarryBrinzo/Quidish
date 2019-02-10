package com.quidish.anshgupta.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class SoldAdDiscriptionActivity extends AppCompatActivity {

    LinearLayout specification, description, spec_layout;
    View dline, sline;
    TextView des_layout, spectext, destext,adtitle,price,address;
    TextView brand,model,includes,year,condition,save;
    String pic1,pic2,pic3,pic4;
    ImageView image1,image2,image3,image4;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold);

        coordinatorLayout=findViewById(R.id.coordinator);

        des_layout = findViewById(R.id.des_layout);
        spec_layout = findViewById(R.id.spec_layout);
        specification = findViewById(R.id.specifications);
        description = findViewById(R.id.description);
        dline = findViewById(R.id.descriptionline);
        sline = findViewById(R.id.specificationsline);
        destext = findViewById(R.id.destext);
        spectext = findViewById(R.id.spectext);
        adtitle=findViewById(R.id.adtitle);
        price=findViewById(R.id.price);
        brand=findViewById(R.id.brand);
        model=findViewById(R.id.model);
        includes=findViewById(R.id.includes);
        year=findViewById(R.id.year);
        condition=findViewById(R.id.condition);
        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);
        image4=findViewById(R.id.image4);
        save=findViewById(R.id.save);
        address=findViewById(R.id.address);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            adtitle.setText(bundle.getString("adtitle"));
            price.setText(bundle.getString("price"));
            brand.setText(bundle.getString("brand"));
            model.setText(bundle.getString("model"));
            address.setText(bundle.getString("address"));
            includes.setText(bundle.getString("includes"));
            year.setText(bundle.getString("year"));
            condition.setText(bundle.getString("condition"));
            des_layout.setText(bundle.getString("addetails"));
            pic1=bundle.getString("pic1");
            pic2=bundle.getString("pic2");
            pic3=bundle.getString("pic3");
            pic4=bundle.getString("pic4");
        }

        final CollapsingToolbarLayout collapsingToolbarLayout =  findViewById(R.id.collapsing_container);
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD);

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(adtitle.getText().toString());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //SpannableStringBuilder str = new SpannableStringBuilder(ename);
            //str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        Glide.with(getApplicationContext()).load(pic1).into(image1);

        if(!pic2.equals("0"))
        {
            image2.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(pic2).into(image2);
        }
        if(!pic3.equals("0"))
        {
            image3.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(pic3).into(image3);
        }
        if(!pic4.equals("0"))
        {
            image4.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(pic4).into(image4);
        }

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                intent.putExtra("pic",pic1);
                startActivity(intent);
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                intent.putExtra("pic",pic2);
                startActivity(intent);
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                intent.putExtra("pic",pic3);
                startActivity(intent);
            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ZoomActivity.class);
                intent.putExtra("pic",pic4);
                startActivity(intent);
            }
        });

        specification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sline.setVisibility(View.VISIBLE);
                dline.setVisibility(View.GONE);
                des_layout.setVisibility(View.GONE);
                spec_layout.setVisibility(View.VISIBLE);

                destext.setTextColor(Color.BLACK);
                spectext.setTextColor(Color.rgb(35, 197, 160));
            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dline.setVisibility(View.VISIBLE);
                sline.setVisibility(View.GONE);
                spec_layout.setVisibility(View.GONE);
                des_layout.setVisibility(View.VISIBLE);

                spectext.setTextColor(Color.BLACK);
                destext.setTextColor(Color.rgb(35, 197, 160));
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
