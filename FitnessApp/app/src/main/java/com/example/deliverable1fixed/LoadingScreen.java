package com.example.deliverable1fixed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

// import pl.droidsonroids.gif.GifImageView;


public class LoadingScreen extends AppCompatActivity {

 /**   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        /*EasySplashScreen config = new EasySplashScreen(LoadingScreen.this)
                .withFullScreen()
                .withTargetActivity(FrontScreen.class)
                .withSplashTimeOut(3000)
                .withBackgroundColor(Color.parseColor("FFFFF"))
                .withHeaderText("Header")
                .withFooterText("Footer")
                .withBeforeLogoText("Before Logog Text")
                .withAfterLogoText("After")
                .withLogo(R.mipmap.ic_launcher_round);
        config.getHeaderTextView().setTextColor(Color.BLACK);
        config.getFooterTextView().setTextColor(Color.BLACK);
        config.getBeforeLogoTextView().setTextColor(Color.BLACK);
        config.getAfterLogoTextView().setTextColor(Color.BLACK);

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);

        getSupportActionBar().hide();

        Intent intentView;
        Handler().postDeplayed({
                Intent intentView = new Intent(LoadingScreen.this, FrontScreen.class)
                startActivity(intentView)
                finish()

        })
    }*/
/** int[] myImageList = new int[]{R.drawable.workout_clb, R.drawable.overhead_shoulder_press,R.drawable.dbpress, R.drawable.bench_press,R.drawable.livewallpaper};
    GifImageView logoImageView1= (GifImageView) findViewById(R.id.logoImageView);
    logoImageView1.setImageDrawable(d);
    public GifImageView getLogoImageView1() {
        return logoImageView1;
    }

    Drawable d = getResources().getDrawable(myImageList[1]);
    ImageView.setImageDrawable(d);
    logoImageView1.setImageDrawable(d);*/

 //logoImageView.setImageResource(logoImageView1[i]);

    // Create a new event for the activity.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for the content view.
        //starter("InstructorMain");
        setContentView(R.layout.activity_loading_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LoadingScreen.this, FrontScreen.class);
                startActivity(i);
                finish();
            }
        }, 6000);
    }

    public void starter(String className){

        if (className.equals("FrontScreen")) {
            setContentView(R.layout.activity_loading_screen);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LoadingScreen.this, FrontScreen.class);
                    startActivity(i);
                    finish();
                }
            }, 6000);
        }else{
           String className1=className+".class";
            setContentView(R.layout.activity_loading_screen);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i2 = new Intent();
                    //Intent i = new Intent(String.valueOf(LoadingScreen.this));
                    i2.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    startActivity(i2);
                    finish();
                }
            }, 6000);
        }
    }
    public void goBack(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                Intent i =new Intent(LoadingScreen.this, InstructorMain.class);
                startActivity(i);
                finish();

                //here you can start your Activity B.

            }

        }, 10000);

    }
}