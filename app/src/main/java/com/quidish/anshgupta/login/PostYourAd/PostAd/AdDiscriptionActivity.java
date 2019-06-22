package com.quidish.anshgupta.login.PostYourAd.PostAd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quidish.anshgupta.login.R;

public class AdDiscriptionActivity extends AppCompatActivity {

    EditText adtitle,description,price,brand;
    TextView descriptiondes,desct,titlect,titledes,newtext,usedtext;;
    LinearLayout newitm,useditm;
    int titct=0,dect=0,pricetext=0;
    int newit=0,usedit=0;
    Button next;
    LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_discription);

        descriptiondes=findViewById(R.id.descriptiondes);
        desct=findViewById(R.id.desct);
        titlect=findViewById(R.id.titlect);
        titledes=findViewById(R.id.titledes);
        newtext=findViewById(R.id.newtext);
        newitm=findViewById(R.id.newitm);
        usedtext=findViewById(R.id.usedtext);
        useditm=findViewById(R.id.useditm);
        next=findViewById(R.id.next);
        adtitle=findViewById(R.id.adtitle);
        description=findViewById(R.id.description2);
        price=findViewById(R.id.price);
        brand=findViewById(R.id.brand);
        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        price.setText("₹ ");
        Selection.setSelection(price.getText(), price.getText().length());

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor searchhint = pref.edit();

                searchhint.putString("adtitle",adtitle.getText().toString());
                searchhint.putString("addescription",description.getText().toString());
                searchhint.putString("price",price.getText().toString());
                searchhint.putString("brand",brand.getText().toString());

                if (newit==1)
                    searchhint.putString("condition","new");
                else
                    searchhint.putString("condition","used");

                searchhint.putString("Ad","1");

                searchhint.apply();

                Intent intent = new Intent(AdDiscriptionActivity.this, LocationSetActivity.class);
                startActivity(intent);
            }
        });


        newitm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(newit==0){
                    newitm.setBackgroundResource(R.drawable.nicknameborder2);
                    newtext.setTextColor(Color.parseColor("#ffffff"));
                    newit=1;
                }
                else{
                    newitm.setBackgroundResource(R.drawable.nicknameborder);
                    newtext.setTextColor(Color.parseColor("#10a115"));
                    newit=0;
                }

                usedtext.setTextColor(Color.parseColor("#10a115"));
                useditm.setBackgroundResource(R.drawable.nicknameborder);
                usedit=0;

                if(pricetext==1 && dect==1 && titct==1  && (newit==1 || usedit==1)){
                    next.setEnabled(true);
                    next.setBackgroundResource(R.drawable.border3);
                    next.setTextColor(Color.parseColor("#ffffff"));
                }

                else {
                    next.setEnabled(false);
                    next.setBackgroundResource(R.drawable.border8);
                    next.setTextColor(Color.parseColor("#aeaeae"));
                }

            }
        });

        useditm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usedit==0){
                    useditm.setBackgroundResource(R.drawable.nicknameborder2);
                    usedtext.setTextColor(Color.parseColor("#ffffff"));
                    usedit=1;
                }
                else{
                    useditm.setBackgroundResource(R.drawable.nicknameborder);
                    usedtext.setTextColor(Color.parseColor("#10a115"));
                    usedit=0;
                }

                newtext.setTextColor(Color.parseColor("#10a115"));
                newitm.setBackgroundResource(R.drawable.nicknameborder);
                newit=0;

                if(pricetext==1 && dect==1 && titct==1  && (newit==1 || usedit==1)){
                    next.setEnabled(true);
                    next.setBackgroundResource(R.drawable.border3);
                    next.setTextColor(Color.parseColor("#ffffff"));
                }

                else {
                    next.setEnabled(false);
                    next.setBackgroundResource(R.drawable.border8);
                    next.setTextColor(Color.parseColor("#aeaeae"));
                }
            }
        });


        price.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pr=s.toString();

                if(pr.length()>2){
                    pricetext=1;
                }

                else
                    pricetext=0;

                if(pricetext==1 && dect==1 && titct==1  && (newit==1 || usedit==1)){
                    next.setEnabled(true);
                    next.setBackgroundResource(R.drawable.border3);
                    next.setTextColor(Color.parseColor("#ffffff"));
                }

                else {
                    next.setEnabled(false);
                    next.setBackgroundResource(R.drawable.border8);
                    next.setTextColor(Color.parseColor("#aeaeae"));
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("₹ ")){
                    price.setText("₹ ");
                    Selection.setSelection(price.getText(), price.getText().length());

                }

            }
        });

        adtitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length()>0){
                    titlect.setText(s.toString().length() + " / 4096");
                    titlect.setVisibility(View.VISIBLE);
                }

                else {
                    titledes.setText("Mention the key features of your item (e.g. brand, model, age, type)");
                    titledes.setTextColor(Color.parseColor("#5f6077"));
                    titlect.setVisibility(View.GONE);
                }

                if(s.toString().length()>=10)
                    titct=1;
                else{
                    titct=0;
                    titledes.setTextColor(Color.parseColor("#fa2600"));
                    titledes.setText("It has minimum length of 10. Please edit it.");
                }

                if(s.toString().length()>70){
                    titct=0;
                    titledes.setTextColor(Color.parseColor("#fa2600"));
                    titledes.setText("It has maximum length of 70. Please edit it.");
                }

                if(titct==1)
                    titledes.setText(" ");


                if(pricetext==1 && dect==1 && titct==1  && (newit==1 || usedit==1)){
                    next.setEnabled(true);
                    next.setBackgroundResource(R.drawable.border3);
                    next.setTextColor(Color.parseColor("#ffffff"));
                }

                else {
                    next.setEnabled(false);
                    next.setBackgroundResource(R.drawable.border8);
                    next.setTextColor(Color.parseColor("#aeaeae"));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length()>0){
                    desct.setText(s.toString().length() + " / 4096");
                    desct.setVisibility(View.VISIBLE);
                }

                else {
                    descriptiondes.setText("Include condition, features, reason for selling and any details a buyer might be interested in.");
                    descriptiondes.setTextColor(Color.parseColor("#5f6077"));
                    desct.setVisibility(View.GONE);
                }

                if(s.toString().length()>=10)
                    dect=1;
                else{
                    dect=0;
                    descriptiondes.setTextColor(Color.parseColor("#fa2600"));
                    descriptiondes.setText("It has minimum length of 10. Please edit it.");
                }

                if(s.toString().length()>4096){
                    dect=0;
                    descriptiondes.setTextColor(Color.parseColor("#fa2600"));
                    descriptiondes.setText("It has maximum length of 4096. Please edit it.");
                }

                if(dect==1)
                    descriptiondes.setText(" ");

                if(pricetext==1 && dect==1 && titct==1  && (newit==1 || usedit==1)){
                    next.setEnabled(true);
                    next.setBackgroundResource(R.drawable.border3);
                    next.setTextColor(Color.parseColor("#ffffff"));
                }

                else {
                    next.setEnabled(false);
                    next.setBackgroundResource(R.drawable.border8);
                    next.setTextColor(Color.parseColor("#aeaeae"));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        adtitle=findViewById(R.id.adtitle);
        description=findViewById(R.id.description2);
        price=findViewById(R.id.price);
        brand=findViewById(R.id.brand);

        String Ad=(pref.getString("Ad", null));

        if(Ad!=null && Ad.equals("1")){
            adtitle.setText(pref.getString("adtitle", null));
            description.setText(pref.getString("addescription", null));
            price.setText(pref.getString("price", null));
            brand.setText(pref.getString("brand", null));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
