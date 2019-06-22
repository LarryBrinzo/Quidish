package com.quidish.anshgupta.login.PostYourAd.PostAd;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.quidish.anshgupta.login.R;

public class ProgressBarActivity extends AppCompatActivity {

    Button myButton;
    View myView;
    boolean isUp;
    LinearLayout dim;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_progress_bar);

        myView = findViewById(R.id.my_view);
        myButton = findViewById(R.id.my_button);
        dim=findViewById(R.id.dim);
        progressBar=findViewById(R.id.progressBar);

        // initialize as invisible (could also do in xml)
        myView.setVisibility(View.INVISIBLE);
        myButton.setText("Slide up");
        isUp = false;
    }

    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressBar.getProgress()+20);
        progressAnimator.setDuration(1000);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    @SuppressLint("Range")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onSlideViewButtonClick(View view) {
        if (isUp) {
            slideDown(myView);
            myButton.setText("Slide up");
            dim.setEnabled(true);
            dim.setBackgroundColor(Color.parseColor("#00ffffff"));
        } else {
            slideUp(myView);
            myButton.setText("Slide down");
            dim.setBackgroundColor(Color.parseColor("#80000000"));
        }
        isUp = !isUp;
    }
}