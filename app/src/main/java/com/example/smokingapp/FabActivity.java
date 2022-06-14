package com.example.smokingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutionException;

public class FabActivity extends AppCompatActivity {
    private int device_width = 0;
    private int device_height = 0;

    FloatingActionButton fabMain, fabSetting;
    FloatingActionButton fabAdmin;
    TextView fabSettingText;
    Animation fabOpen, fabClose, rotateForward, rotateBackward, activity_finish;
    View.OnClickListener mClickLST;

    String tmp_Module_name;

    //dialog_add_module 연결해주는 View 선언
    View v_d;

    private FirebaseAuth User;
    private DatabaseReference mDatabaseRef;

    SmokingPerson SmokingPerson = new SmokingPerson(); // 흡연 탐지를 위한 객체
    boolean isOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fab);

        DisplayMetrics display = getApplicationContext().getResources().getDisplayMetrics();
        device_width = display.widthPixels;
        device_height = display.heightPixels;

        User = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SmokingApp/UserAccount/" + User.getCurrentUser().getUid());

        mClickLST = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId())
                {
                    case R.id.fab_setting:
                        finish();
                        ((MainActivity)MainActivity.context).addModule();
                        break;
                    case R.id.fab_admin:
                        finish();
                        ((MainActivity)MainActivity.context).admin();
                        break;
                }
            }
        };



        fabMain = (FloatingActionButton) findViewById(R.id.fab_main);
        fabSetting = (FloatingActionButton) findViewById(R.id.fab_setting);

        fabSetting.setOnClickListener(mClickLST);
        fabSettingText = (TextView) findViewById(R.id.fab_setting_txt);

        fabAdmin = (FloatingActionButton) findViewById(R.id.fab_admin);
        fabAdmin.setOnClickListener(mClickLST);


        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);



        rotateForward.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        action();
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action();
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();
            Bitmap bitmapScreen = Bitmap.createBitmap(device_width, device_height, Bitmap.Config.ARGB_8888);
            if(x < 0 || y < 0)
                return false;
            int ARGB = bitmapScreen.getPixel(x, y);
            if(Color.alpha(ARGB) == 0) {
                action();
            }
            return true;
        }
        return false;
    }

    private void action(){
        if(isOpen){
            fabMain.startAnimation(rotateForward);
            fabSetting.startAnimation(fabClose);
            fabSettingText.startAnimation(fabClose);
            fabSetting.setClickable(false);
            fabAdmin.startAnimation(fabClose);
            fabAdmin.setClickable(false);
            isOpen = false;
        }
        else{
            fabMain.startAnimation(rotateBackward);
            fabSetting.startAnimation(fabOpen);
            fabSettingText.startAnimation(fabOpen);
            fabSetting.setClickable(true);
            fabAdmin.startAnimation(fabOpen);
            fabAdmin.setClickable(true);
            isOpen = true;
        }
    }
}