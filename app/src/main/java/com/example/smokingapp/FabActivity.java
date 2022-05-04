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

public class FabActivity extends AppCompatActivity {
    private int device_width = 0;
    private int device_height = 0;

    FloatingActionButton fab_main, fab_setting;
    TextView fab_setting_txt;
    TextView Module_name_txt, add_Module;
    Animation fab_open, fab_close, rotate_forward, rotate_backward, activity_finish;
    View.OnClickListener mClickLST;

    String tmp_Module_name;

    //dialog_add_module 연결해주는 View 선언
    View v_d;

    private FirebaseAuth User;
    private DatabaseReference mDatabaseRef;
    ModuleAccount module_Account = new ModuleAccount();  // DB위한 객체
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

                        AlertDialog.Builder d = new AlertDialog.Builder(FabActivity.this);

                        d.setTitle("");

                        v_d = (View) View.inflate(FabActivity.this, R.layout.dialog_add_module, null);
                        d.setView(v_d);

                        d.setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        add_Module = (EditText) v_d.findViewById(R.id.add_Module);

                                        if(add_Module.toString().length()!=0){
                                            tmp_Module_name = add_Module.getText().toString();
                                            Log.i("FabActivity",mDatabaseRef.toString());
                                            mDatabaseRef.child("module_list").child(tmp_Module_name);
                                            mDatabaseRef.child("module_list").child(tmp_Module_name).setValue(module_Account);
                                            finish();

                                        }

                                    }
                                });

                        d.show();
                        break;
                }
            }
        };



        fab_main = (FloatingActionButton) findViewById(R.id.fab_main);
        fab_setting = (FloatingActionButton) findViewById(R.id.fab_setting);

        fab_setting.setOnClickListener(mClickLST);

        fab_setting_txt = (TextView) findViewById(R.id.fab_setting_txt);
        Module_name_txt = (TextView) findViewById(R.id.Module_name_txt);
        add_Module = (EditText) findViewById(R.id.add_Module);


        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);



        rotate_forward.setAnimationListener(new Animation.AnimationListener() {
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
        fab_main.setOnClickListener(new View.OnClickListener() {
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
            fab_main.startAnimation(rotate_forward);
            fab_setting.startAnimation(fab_close);
            fab_setting_txt.startAnimation(fab_close);
            fab_setting.setClickable(false);
            isOpen = false;
        }
        else{
            fab_main.startAnimation(rotate_backward);
            fab_setting.startAnimation(fab_open);
            fab_setting_txt.startAnimation(fab_open);
            fab_setting.setClickable(true);
            isOpen = true;
        }
    }
}