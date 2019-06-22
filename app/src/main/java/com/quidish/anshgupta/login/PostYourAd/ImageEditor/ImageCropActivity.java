package com.quidish.anshgupta.login.PostYourAd.ImageEditor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quidish.anshgupta.login.PostYourAd.PostAd.SelectCatagoryActivity;
import com.quidish.anshgupta.login.PostYourAd.PostAd.SelectPictureActivity;
import com.quidish.anshgupta.login.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ImageCropActivity extends AppCompatActivity {

    public static Bitmap bm;
    CropImageView cropImageView;
    BottomNavigationView optionNavigation;
    int H,W,position;
    LinearLayout back;
    TextView done;

    @SuppressLint("StaticFieldLeak")
    public static Activity fa;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Objects.requireNonNull(getSupportActionBar()).hide();

        fa = this;
        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            position=(bundle.getInt("position"));
            H=(bundle.getInt("height"));
            W=(bundle.getInt("width"));

            bm=(SelectCatagoryActivity.finalSelectedFilepath.get(position));
        }

        optionNavigation = findViewById(R.id.optionNavigation);

        Menu nav_Menu = optionNavigation.getMenu();

        if(position==0)
            nav_Menu.removeItem(R.id.del_option);

        cropImageView = findViewById(R.id.cropImageView);
        cropImageView.setImageBitmap(bm);
        cropImageView.setFixedAspectRatio(false);
        cropImageView.setGuidelines(CropImageView.Guidelines.ON);

        done=findViewById(R.id.donee);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                SelectCatagoryActivity.finalSelectedFilepath.set(position,bm);
                onBackPressed();
            }
        });

        optionNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.edit_option:
                        bm = cropImageView.getCroppedImage();
                        Rect wh = cropImageView.getCropRect();
                        ImageEditorActivity.iHeight = wh.height();

                       ImageEditorActivity.bm=bm;

                        Intent i = new Intent(getApplicationContext(),ImageEditorActivity.class);
                        i.putExtra("position",position);
                        startActivity(i);
                        break;

                    case R.id.del_option:
                        AlertShow();
                        break;

                    case R.id.rep_option:
                        onBackPressed();
                        SelectCatagoryActivity.fa.finish();
                        break;
                }
                return true;
            }
        });


    }

    public void AlertShow(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageCropActivity.this);
        builder.setMessage("Remove photo?")
                .setCancelable(false)
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int delimgno;

//                        for(int ind=0;ind< SelectPictureActivity.SelectedFilepath.size();ind++){
//
//                            if(SelectPictureActivity.SelectedFilepath.get(ind).equals(SelectPictureActivity.SelectedFilepath.get(position))){

                              //  SelectPictureActivity.Allimages.set(SelectPictureActivity.Selectedpos.get(ind),new Pair(SelectPictureActivity.SelectedFilepath.get(ind),0));
                                //SelectPictureActivity.Selectedpos.remove(ind);

                                int index=SelectPictureActivity.SelectedFilepath.indexOf(SelectPictureActivity.SelectedFilepath.get(position));

                                delimgno=SelectPictureActivity.Selectedimagesno.get(index);
                        SelectPictureActivity.SelectedFilepath.remove(index);
                        SelectPictureActivity.Selectedimagesno.set(index,0);
                        SelectPictureActivity.Selectedimagesno.remove(index);

                        //    }
                       // }

                        for(int ind=0;ind<SelectPictureActivity.SelectedFilepath.size();ind++){

                            if(SelectPictureActivity.Selectedimagesno.get(ind) > delimgno){
                              //  int fir=SelectPictureActivity.Selectedpos.get(ind);
                                int sec=SelectPictureActivity.Selectedimagesno.get(ind);

                               // SelectPictureActivity.Allimages.set(fir,new Pair(SelectPictureActivity.SelectedFilepath.get(ind),sec-1));
                                SelectPictureActivity.Selectedimagesno.set(ind,sec-1);
                            }
                        }


                        SelectPictureActivity.imageno--;

                        SelectCatagoryActivity.finalSelectedFilepath.remove(position);

                        onBackPressed();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
