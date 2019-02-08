package com.quidish.anshgupta.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class CategoryActivity extends AppCompatActivity {

    LinearLayout cycle_layout, book_layout, electronics_layout, mobile_layout,other_layout;
    LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        cycle_layout = findViewById(R.id.cycle_layout);
        book_layout = findViewById(R.id.books_layout);
        electronics_layout = findViewById(R.id.electronics_layout);
        mobile_layout = findViewById(R.id.mobile_layout);
        other_layout = findViewById(R.id.other_layout);
        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        layoutreferences();
    }

    public void layoutreferences(){

        cycle_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),SellActivity.class);
                intent.putExtra("catname", "Cycle");
                startActivity(intent);

            }
        });

        book_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),SellActivity.class);
                intent.putExtra("catname", "Book");
                startActivity(intent);
            }
        });

        electronics_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),SellActivity.class);
                intent.putExtra("catname", "Electronics");
                startActivity(intent);

            }
        });

        mobile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),SellActivity.class);
                intent.putExtra("catname", "Mobile");
                startActivity(intent);

            }
        });

        other_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CategoryActivity.this, AdpostActivity.class);
                intent.putExtra("catname", "   Others");
                intent.putExtra("subcatname", "   Others");
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}


