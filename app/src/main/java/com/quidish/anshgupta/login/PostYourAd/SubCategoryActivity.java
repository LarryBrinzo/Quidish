package com.quidish.anshgupta.login.PostYourAd;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.quidish.anshgupta.login.R;


public class SubCategoryActivity extends AppCompatActivity {

    LinearLayout mobilesub, tabletsub, accessoriessub, laptopcomsub, peripheralssub;
    LinearLayout speakersub, camerasub, cameraaccsub, othersub, booksub, stationerysub, gearsub, nongearsub;
    String category;
    LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            category = bundle.getString("catname");
        }

        assert category != null;
        if(category.equals("Cycle"))
        {
            setContentView(R.layout.cycle);

            gearsub = findViewById(R.id.gearsub_layout);
            nongearsub = findViewById(R.id.nongearsub_layout);

            cyclelistner();
        }

        else if(category.equals("Book"))
        {
            setContentView(R.layout.books);

            booksub = findViewById(R.id.textbooksub_layout);
            stationerysub = findViewById(R.id.stationerysub_layout);

            booklistener();
        }

        else if(category.equals("Mobile"))
        {
            setContentView(R.layout.mobileph);

            mobilesub = findViewById(R.id.mobilesub_layout);
            tabletsub = findViewById(R.id.tabletsub_layout);
            accessoriessub = findViewById(R.id.accessoriessub_layout);

            mobilelistener();
        }

        else if(category.equals("Electronics"))
        {
            setContentView(R.layout.electronics);

            laptopcomsub = findViewById(R.id.laptopcompsub_layout);
            peripheralssub = findViewById(R.id.peripheralssub_layout);
            speakersub = findViewById(R.id.speakersub_layout);
            cameraaccsub = findViewById(R.id.cameraaccsub_layout);
            camerasub = findViewById(R.id.camerasub_layout);
            othersub = findViewById(R.id.othersub_layout);

            elclistener();
        }

        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

        // Calling all categories and sub categories





    public void cyclelistner(){

        gearsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Cycle");
                intent.putExtra("subcatname", "   Gear");
                startActivity(intent);
            }
        });

        nongearsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Cycle");
                intent.putExtra("subcatname", "   Non-Gear");
                startActivity(intent);
            }
        });

    }


    public void booklistener(){


        booksub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Books & Stationery");
                intent.putExtra("subcatname", "   Text Book");
                startActivity(intent);

            }
        });

        stationerysub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Books & Stationery");
                intent.putExtra("subcatname", "   Stationery");
                startActivity(intent);
            }
        });
    }


    public void mobilelistener(){

        mobilesub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Mobile & Tablets");
                intent.putExtra("subcatname", "   Mobile Phones");
                startActivity(intent);
            }
        });

        tabletsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Mobile & Tablets");
                intent.putExtra("subcatname", "   Tablets");
                startActivity(intent);
            }
        });

        accessoriessub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Mobile & Tablets");
                intent.putExtra("subcatname", "   Accessories");
                startActivity(intent);
            }
        });
    }


    public void elclistener(){

        laptopcomsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Electronics & Appliances");
                intent.putExtra("subcatname", "   Laptop-Computer");
                startActivity(intent);
            }
        });

        peripheralssub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Electronics & Appliances");
                intent.putExtra("subcatname", "   Computer Peripherals");
                startActivity(intent);
            }
        });

        speakersub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Electronics & Appliances");
                intent.putExtra("subcatname", "   Speaker");
                startActivity(intent);
            }
        });

        camerasub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Electronics & Appliances");
                intent.putExtra("subcatname", "   Camera");
                startActivity(intent);
            }
        });

        cameraaccsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Electronics & Appliances");
                intent.putExtra("subcatname", "   Camera Accessories");
                startActivity(intent);
            }
        });

        othersub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Intent intent = new Intent(SubCategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Electronics & Appliances");
                intent.putExtra("subcatname", "   Others");
                startActivity(intent); }
        });

        }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}