package com.howaboutthis.satyaraj.qnews;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

public class aboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.about);
        final TextView developed = findViewById(R.id.developed);
        final TextView satyaraj = findViewById(R.id.satyaraj);
        final TextView by = findViewById(R.id.textView2);

        developed.startAnimation(AnimationUtils.loadAnimation(aboutActivity.this,R.anim.textview_left));
        satyaraj.startAnimation(AnimationUtils.loadAnimation(aboutActivity.this,R.anim.textview_right));

        Animation animation = new ScaleAnimation(1000,0,1000,0);
        animation.setDuration(500);
        by.startAnimation(animation);


        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation anin = new RotateAnimation(-10,10,50,0);
                anin.setRepeatMode(10);
                anin.setDuration(100);

                developed.startAnimation(anin);
                satyaraj.startAnimation(anin);


            }
        });



    }
}