package com.quidish.anshgupta.login.PostYourAd.PostAd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.quidish.anshgupta.login.PostYourAd.CameraUtils;
import com.quidish.anshgupta.login.R;

public class SelectPictureActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public static ArrayList<Pair<String,Integer>> Allimages = new ArrayList<>();
    Set<String> folders = new HashSet<>();
    String foldername="All Photos";
    Spinner spinner;
    RecyclerView gallery;
    ImageAdapter imageAdapter;
    int myColumnWidth;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int PICK_FROM_GALLERY = 1;
    String imageurl;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static String imageStoragePath="0";
    public static int imageno=1;
    TextView next;
    LinearLayout back;
    public static ArrayList<Integer> Selectedimagesno =new ArrayList<>();
    public static ArrayList<Integer> Selectedpos =new ArrayList<>();
    public static ArrayList<String> SelectedFilepath =new ArrayList<>();
    public static String Ad_category=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);

        gallery = findViewById(R.id.galleryGridView);
        spinner =  findViewById(R.id.spinner);
        next=findViewById(R.id.next);

        back=findViewById(R.id.backbt);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        int myGridColumns = width / 3;

        if ( myGridColumns == 0 || myGridColumns == 1 )
            myGridColumns = 3;
        myColumnWidth = ( width / myGridColumns )-28;

        Ad_category=null;


        try {
            if (ActivityCompat.checkSelfPermission(SelectPictureActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SelectPictureActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
            } else {
                galleryImageSetup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void galleryImageSetup(){

        Allimages = getAllShownImagesPath(SelectPictureActivity.this,"All Photos");
        imageurl=getURLForResource(R.drawable.add_pic2);
        Allimages.add(0,new Pair(getURLForResource(R.drawable.add_pic2),0));

        imageAdapter = new ImageAdapter(Allimages,SelectPictureActivity.this);
        RecyclerView.LayoutManager recyceSugg2 = new GridLayoutManager(SelectPictureActivity.this,3);
        gallery.setLayoutManager(recyceSugg2);
        recyceSugg2.setAutoMeasureEnabled(false);
        gallery.setItemAnimator( new DefaultItemAnimator());
        gallery.setAdapter(imageAdapter);

        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<>();

        categories.add("All Photos");

        for (Object folder : folders) {
            categories.add(folder.toString());
        }

        Collections.sort(categories);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        spinner.setAdapter(dataAdapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SelectCatagoryActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   galleryImageSetup();
                } else {
                    onBackPressed();
                }
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        foldername = parent.getItemAtPosition(position).toString();

        Allimages.clear();
        gallery.removeAllViewsInLayout();

        Allimages = getAllShownImagesPath(this,foldername);
        Allimages.add(0,new Pair(getURLForResource(R.drawable.add_pic2),0));

        imageAdapter = new ImageAdapter(Allimages,SelectPictureActivity.this);
        RecyclerView.LayoutManager recyceSugg2 = new GridLayoutManager(SelectPictureActivity.this,3);
        gallery.setLayoutManager(recyceSugg2);
        recyceSugg2.setAutoMeasureEnabled(false);
        gallery.setItemAnimator( new DefaultItemAnimator());
        gallery.setAdapter(imageAdapter);

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }


    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyHoder>{

        private List<Pair<String,Integer>> list;
        private Context context;

        ImageAdapter(List<Pair<String,Integer>> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(context).inflate(R.layout.grid_item,parent,false);
            return new MyHoder(view);
        }

        @SuppressLint({"CheckResult", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull final MyHoder holder, @SuppressLint("RecyclerView") final int position) {

            RequestOptions options = new RequestOptions();
            options.centerCrop();

            Glide.with(context).load(list.get(position).first)
                    .into( holder.image);

            if(SelectedFilepath.contains(list.get(position).first)){

                int index=SelectedFilepath.indexOf(list.get(position).first);

                holder.selectimage.setVisibility(View.VISIBLE);
                holder.imgno.setVisibility(View.VISIBLE);

                //Allimages.set(position,new Pair(SelectedFilepath.get(index),Selectedimagesno.get(index)));

                if(Selectedimagesno.get(index)==0)
                    holder.imgno.setVisibility(View.GONE);

                holder.imgno.setText(Integer.toString(Selectedimagesno.get(index)));

                if(imageno>1)
                    next.setVisibility(View.VISIBLE);
                else
                    next.setVisibility(View.GONE);

            }


                holder.card.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View view) {

                        if(list.get(position).first.equals(imageurl)){
                            if (CameraUtils.checkPermissions(getApplicationContext())) {
                                captureImage();
                            } else {
                                requestCameraPermission(MEDIA_TYPE_IMAGE);
                            }
                        }

                        else {

                            if (holder.selectimage.getVisibility() == View.GONE) {

                                holder.selectimage.setVisibility(View.VISIBLE);
                                holder.imgno.setVisibility(View.VISIBLE);
                                holder.imgno.setText(Integer.toString(imageno));

                                Selectedimagesno.add(imageno);
                                //Selectedpos.add(position);
                                SelectedFilepath.add(list.get(position).first);

                                Allimages.set(position,new Pair(list.get(position).first,imageno));

                                imageAdapter.notifyDataSetChanged();

                                imageno++;

                                if(imageno>1)
                                    next.setVisibility(View.VISIBLE);
                                else
                                    next.setVisibility(View.GONE);


//                                try {
//                                    Bitmap bmp;
//                                    bmp= CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, Allimages.get(position).first);
//                                    SelectedFilepath.add(getImageUri(getApplicationContext(),bmp));
//
//                                } catch (NullPointerException ignored) {
//                                }

                            } else {

                                holder.selectimage.setVisibility(View.GONE);
                                holder.imgno.setVisibility(View.GONE);

                                int delimgno = 11;

                                for(int ind=0;ind<SelectedFilepath.size();ind++){

                                    if(SelectedFilepath.get(ind).equals(list.get(position).first)){

                                     //   Allimages.set(position,new Pair(SelectedFilepath.get(ind),0));
                                     //   Selectedpos.remove(ind);

                                        delimgno=Selectedimagesno.get(ind);
                                        SelectedFilepath.remove(ind);
                                        Selectedimagesno.remove(ind);
                                    }
                                }

                                for(int ind=0;ind<SelectedFilepath.size();ind++){

                                    if(Selectedimagesno.get(ind) > delimgno){
                                       // int fir=Selectedpos.get(ind);
                                        int sec=Selectedimagesno.get(ind);

                                       // Allimages.set(fir,new Pair(SelectedFilepath.get(ind),sec-1));
                                        Selectedimagesno.set(ind,sec-1);
                                    }
                                }


                                imageAdapter.notifyDataSetChanged();

                                imageno--;

                                if(imageno>1)
                                    next.setVisibility(View.VISIBLE);
                                else
                                    next.setVisibility(View.GONE);
                            }
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
            ImageView image;
            ConstraintLayout card;
            PercentRelativeLayout selectimage;
            TextView imgno;

            MyHoder(View itemView) {
                super(itemView);

                image =itemView.findViewById(R.id.image);
                card=itemView.findViewById(R.id.card_view);
                selectimage=itemView.findViewById(R.id.selectedimg);
                imgno=itemView.findViewById(R.id.imgno);
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


    @SuppressLint("Recycle")
    private ArrayList<Pair<String,Integer>> getAllShownImagesPath(Activity activity,String foldername) {
        Uri uri;
        Cursor cursor;
        int column_index_data,column_index_folder_name;

        ArrayList<Pair<String,Integer>> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        assert cursor != null;

        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        column_index_data = cursor.getColumnIndexOrThrow(MediaColumns.DATA);

        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

                folders.add(cursor.getString(column_index_folder_name));

            if(cursor.getString(column_index_folder_name).equals(foldername) || foldername.equals("All Photos"))
                listOfAllImages.add(0,new Pair(absolutePathOfImage,0));
        }

        return listOfAllImages;
    }

    public String getURLForResource (int resourceId) {
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
    }


    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);

        if (file != null) {
                imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                CameraUtils.refreshGallery(getApplicationContext(),imageStoragePath);

                Allimages.add(0,new Pair(imageStoragePath,0));
                imageAdapter.notifyDataSetChanged();
            }

            else if (resultCode == RESULT_CANCELED) {
                }

                else {
            }
        }

    }


    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                captureImage();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(SelectPictureActivity.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("SeletedFilepath",SelectedFilepath);
        outState.putIntegerArrayList("Imagesno", Selectedimagesno);
        outState.putInt("TImagesno", imageno);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        SelectedFilepath = savedInstanceState.getStringArrayList("SeletedFilepath");
        Selectedimagesno= savedInstanceState.getIntegerArrayList("Imagesno");
        imageno= savedInstanceState.getInt("TImagesno");

    }

    @Override
    protected void onResume() {
        super.onResume();
        imageAdapter = new ImageAdapter(Allimages,SelectPictureActivity.this);
        RecyclerView.LayoutManager recyceSugg2 = new GridLayoutManager(SelectPictureActivity.this,3);
        gallery.setLayoutManager(recyceSugg2);
        recyceSugg2.setAutoMeasureEnabled(false);
        gallery.setItemAnimator( new DefaultItemAnimator());
        gallery.setAdapter(imageAdapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
