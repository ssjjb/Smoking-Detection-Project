package com.example.smokingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;

    private static final int NUM_PAGES = 3;
    public static Context context;

    FloatingActionButton fab;
    ViewPager2 viewPager;
    FragmentStateAdapter pagerAdapter;
    TabLayout tablayout;
    private String[] titles = new String[]{"My Module", "Setting", "Log"};

    // 로그아웃, 회원탈퇴, 비밀번호 변경
    //TextView logout, id_delete, pw_change;
    //EditView change_pw;

    // firebase 추가 코드
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    //private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        // find id
        fab = (FloatingActionButton) findViewById(R.id.fab);
        viewPager = (ViewPager2) findViewById(R.id.main_viewpager);
        tablayout = (TabLayout) findViewById(R.id.main_tab);


        pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 로그아웃, 회원탈퇴, 비밀번호 변경
        //logout = (TextView) findViewById(R.id.logout);
        //id_delete = (TextView) findViewById(R.id.id_delete);
        //pw_change = (TextView) findViewById(R.id.pw_change);  // 비밀번호 변경 클릭해주는 텍스트뷰
        //change_pw = (EditView) findViewById(R.id.change_pw);  // 새로운 비밀번호 입력 받는 에디터뷰
        
        // firebase 추가 코드
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SmokingApp");

        new TabLayoutMediator(tablayout, viewPager, (tab, position) -> tab.setText(titles[position])).attach();

        // fab setting
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fab_intent = new Intent(MainActivity.this, FabActivity.class);
                startActivity(fab_intent);
            }
        });

        /*
        // 나중에 로그아웃 logout으로 추가해주세요 ~
        // firebase 로그아웃 코드 ~
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signOut();

                Intent t = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(t);
            }
        });

        // 나중에 회원탈퇴 id_delete로 추가해주세요 ~
        // firebase 회원탙퇴 코드 ~
        id_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id_delete();

                Intent t = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(t);
            }
        });

        // 나중에 비밀번호 변경 pw_change로 추가해주세요 ~
        // firebase 비밀번호 변경 코드 ~
        pw_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                String new_password = change_pw.getText().toString()
                
                pw_change(new_password);

                Intent t = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(t);
        */
    }
    private class MyPagerAdapter extends FragmentStateAdapter {
        public MyPagerAdapter(FragmentActivity fa) {
            super(fa);
        }
        @Override
        public Fragment createFragment(int pos) {
            switch (pos) {
                case 0: {
                    return FragmentModule.newInstance("fragment 1");
                }
                case 1: {
                    return FragmentSetting.newInstance("fragment 2");
                }
                case 2: {
                    return FragmentLog.newInstance("fragment 3");
                }
                default:
                    return FragmentModule.newInstance("fragment 1, Default");
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    // firebase 로그아웃 코드
    private void signOut() {
        mAuth.signOut();
    }

    // firebase 회원탈퇴 코드
    private void id_delete() {
        User.delete();
    }

    // firebase 비밀번호 변경 코드
    private void pw_change(String new_password) {
        User.updatePassword(new_password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("MainActivity", "User password updated.");
                        }
                    }
                });
    }
}