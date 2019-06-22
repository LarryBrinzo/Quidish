package com.quidish.anshgupta.login.PostYourAd.PostAd;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quidish.anshgupta.login.Home.Location.AddInstituteActivity;
import com.quidish.anshgupta.login.R;
import com.quidish.anshgupta.login.SavedLocationModel;

import java.util.ArrayList;
import java.util.List;

public class LocationSetActivity extends AppCompatActivity {

    LinearLayout add,saved,back;
    SavedAddressAdapter savedAddressAdapter;
    RecyclerView savedadd;
    List<SavedLocationModel> savedAdd=new ArrayList<>();

    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_set);

        fa = this;

        add=findViewById(R.id.add);
        savedadd=findViewById(R.id.savedadd);
        saved=findViewById(R.id.saved);
        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        savedadd.setNestedScrollingEnabled(false);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationSetActivity.this, AddInstituteActivity.class);
                intent.putExtra("post","1");
                startActivity(intent);
            }
        });


        savedAddressAdapter = new SavedAddressAdapter(savedAdd,LocationSetActivity.this);
        RecyclerView.LayoutManager recyceSugg3 = new GridLayoutManager(LocationSetActivity.this,1);
        savedadd.setLayoutManager(recyceSugg3);
        recyceSugg3.setAutoMeasureEnabled(false);
        savedadd.setItemAnimator( new DefaultItemAnimator());
        savedadd.setAdapter(savedAddressAdapter);

        displaysavedaddress();

    }

    public void displaysavedaddress(){

        savedAdd.clear();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int serachnumber=pref.getInt("saved_address", 0);

        for(int i=serachnumber;i>=1;i--){

            SavedLocationModel model=new SavedLocationModel();

            model.setCompadd(pref.getString("comp_address"+Integer.toString(i), null));
            model.setNickname(pref.getString("nickname"+Integer.toString(i), null));
            model.setLandmark(pref.getString("landmark"+Integer.toString(i), null));
            model.setLat(pref.getString("latitude"+Integer.toString(i), null));
            model.setLng(pref.getString("longitude"+Integer.toString(i), null));
            model.setInstname(pref.getString("inst_name"+Integer.toString(i), null));
            model.setInstid(pref.getString("inst_id"+Integer.toString(i), null));
            model.setId(i);

            savedAdd.add(model);
        }

        if(savedAdd.size()==0)
            saved.setVisibility(View.GONE);

        else {
            savedAddressAdapter.notifyDataSetChanged();
            saved.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
