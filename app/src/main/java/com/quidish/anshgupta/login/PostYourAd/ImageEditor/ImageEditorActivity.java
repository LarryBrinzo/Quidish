package com.quidish.anshgupta.login.PostYourAd.ImageEditor;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import com.github.chrisbanes.photoview.PhotoView;
import com.quidish.anshgupta.login.PostYourAd.PostAd.SelectCatagoryActivity;
import com.quidish.anshgupta.login.R;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;


public class ImageEditorActivity extends AppCompatActivity {

    static Bitmap bm=ImageCropActivity.bm;
    static PhotoView imageDisplay;
    static float iHeight = 0;
    float cont = 1f;
    float bright = 0f;
    float sat = 1f;
    Bitmap textBit = bm;
    LinearLayout brightness,contrast,saturation;
    ImageView clear,done,done2,clear2,done3,clear3;
    BottomNavigationView optionNavigation;
    int bprogress,cprogress,sprogress;
    SeekBar bseek,cseek,sseek;
    LinearLayout rotate_right,rotate_left,final_done,back;
    int position;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_image_editor);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            position=(bundle.getInt("position"));
        }

        imageDisplay = findViewById(R.id.imageDisplay);
        brightness=findViewById(R.id.brightness);
        done=findViewById(R.id.done);
        clear=findViewById(R.id.clear);
        done2=findViewById(R.id.done2);
        clear2=findViewById(R.id.clear2);
        done3=findViewById(R.id.done3);
        clear3=findViewById(R.id.clear3);
        optionNavigation = findViewById(R.id.optionNavigation);
        contrast=findViewById(R.id.contrast);
        saturation=findViewById(R.id.saturation);
        bseek=findViewById(R.id.brightnessBar);
        cseek=findViewById(R.id.contrastBar);
        sseek=findViewById(R.id.saturationBar);
        rotate_left=findViewById(R.id.rotate_left);
        rotate_right=findViewById(R.id.rotate_right);
        final_done=findViewById(R.id.final_done);
        back=findViewById(R.id.backbt);

        imageDisplay.setImageBitmap(bm);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        final_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                SelectCatagoryActivity.finalSelectedFilepath.set(position,bm);
                onBackPressed();
                ImageCropActivity.fa.finish();
            }
        });

        rotate_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap myImg = bm;
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
                        matrix, true);

                bm=textBit=rotated;
                imageDisplay.setImageBitmap(rotated);

            }
        });

        rotate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap myImg = bm;
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
                        matrix, true);

                bm=textBit=rotated;
                imageDisplay.setImageBitmap(rotated);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionNavigation.setVisibility(View.VISIBLE);
                brightness.setVisibility(View.GONE);

                bm = ((BitmapDrawable)imageDisplay.getDrawable()).getBitmap();
                (imageDisplay).setImageBitmap(bm);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionNavigation.setVisibility(View.VISIBLE);
                brightness.setVisibility(View.GONE);

                (imageDisplay).setImageBitmap(bm);
                bseek.setProgress(bprogress);
            }
        });

        done2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionNavigation.setVisibility(View.VISIBLE);
                contrast.setVisibility(View.GONE);

                bm = ((BitmapDrawable)imageDisplay.getDrawable()).getBitmap();
                (imageDisplay).setImageBitmap(bm);
            }
        });

        clear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionNavigation.setVisibility(View.VISIBLE);
                contrast.setVisibility(View.GONE);

                (imageDisplay).setImageBitmap(bm);
                cseek.setProgress(cprogress);
            }
        });

        done3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionNavigation.setVisibility(View.VISIBLE);
                saturation.setVisibility(View.GONE);

                bm = ((BitmapDrawable)imageDisplay.getDrawable()).getBitmap();
                (imageDisplay).setImageBitmap(bm);
            }
        });

        clear3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionNavigation.setVisibility(View.VISIBLE);
                saturation.setVisibility(View.GONE);

                (imageDisplay).setImageBitmap(bm);
                sseek.setProgress(sprogress);
            }
        });


        BottomNavigationMenuView menuView = (BottomNavigationMenuView) optionNavigation.getChildAt(0);
        try {

            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException ignored) {
        } catch (IllegalAccessException ignored){
        }

        optionNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.brightness_option:
                        bprogress = bseek.getProgress();
                        optionNavigation.setVisibility(View.GONE);
                        brightness.setVisibility(View.VISIBLE);
                        brightnessSet();
                        break;

                    case R.id.contrast_option:
                        cprogress = cseek.getProgress();
                        optionNavigation.setVisibility(View.GONE);
                        contrast.setVisibility(View.VISIBLE);
                        contrastSet();
                        break;

                    case R.id.saturation_option:
                        cprogress = cseek.getProgress();
                        optionNavigation.setVisibility(View.GONE);
                        saturation.setVisibility(View.VISIBLE);
                        saturationSet();
                        break;
                }
                return true;
            }
        });

    }

    private void brightnessSet(){

        bseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bright = ((255f/50f)*i)-255f;
                imageDisplay.setImageBitmap(changeBitmapContrastBrightness(cont,bright,sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void contrastSet(){

       cseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cont = i*(0.1f);
                imageDisplay.setImageBitmap(changeBitmapContrastBrightness(cont,bright,sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    private void saturationSet(){

        sseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sat = (float)i/256f;
                imageDisplay.setImageBitmap(changeBitmapContrastBrightness(cont,bright,sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }


    private Bitmap changeBitmapContrastBrightness(float contrast, float brightness, float saturation)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(textBit.getWidth(), textBit.getHeight(), textBit.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(textBit, 0, 0, paint);
        cm.setSaturation(saturation);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(ret,0,0,paint);
        return ret;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
