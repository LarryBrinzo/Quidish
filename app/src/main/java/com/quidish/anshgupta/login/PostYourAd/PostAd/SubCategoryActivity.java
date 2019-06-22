package com.quidish.anshgupta.login.PostYourAd.PostAd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quidish.anshgupta.login.R;

import java.util.List;

public class SubCategoryActivity extends AppCompatActivity {

    TextView toolbartext;
    String category;
    CategoriesAdapter categoriesAdapter;
    RecyclerView categories;
    LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        toolbartext=findViewById(R.id.texttoolbar);
        categories=findViewById(R.id.categories);
        back=findViewById(R.id.backbt);

        categories.setNestedScrollingEnabled(false);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            toolbartext.setText(bundle.getString("category"));
            category = bundle.getString("category");
        }

        List<String> list2;
        list2=SelectCatagoryActivity.listofcategories.get(category);

        categoriesAdapter = new CategoriesAdapter(list2,SubCategoryActivity.this);
        RecyclerView.LayoutManager recyceSugg2 = new GridLayoutManager(SubCategoryActivity.this,1);
        categories.setLayoutManager(recyceSugg2);
        recyceSugg2.setAutoMeasureEnabled(false);
        categories.setItemAnimator( new DefaultItemAnimator());
        categories.setAdapter(categoriesAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
