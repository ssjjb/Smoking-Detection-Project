package com.example.smokingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final int NUM_PAGES = 3;
    private String[] titles = new String[]{"My Module", "Setting", "Log"};

    private DatabaseReference mDatabaseRef;
    private Context context;

    FloatingActionButton floatingActionButton;
    ViewPager2 viewPager;
    FragmentStateAdapter pagerAdapter;
    TabLayout tablayout;

    //TextView logout, id_delete, pw_change;
    //EditView change_pw;

    // firebase 추가 코드
    private FirebaseAuth mAuth;
    private FirebaseUser User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        viewCasting();

        // firebase 추가 코드
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SmokingApp");

        pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tablayout, viewPager, (tab, position) -> tab.setText(titles[position])).attach();

        // floating action setting
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fab_intent = new Intent(MainActivity.this, FabActivity.class);
                startActivity(fab_intent);
            }
        });

        // firebase logout
        /*logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signOut();

                Intent t = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(t);
            }
        });*/

        // firebase delete user
        /*
        id_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id_delete();

                Intent t = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(t);
            }
        });
         */

        // firebase change password
        /*
        pw_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                String new_password = change_pw.getText().toString()
                
                pw_change(new_password);

                Intent t = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(t);
            }
        });
         */
    }
    private void viewCasting(){
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        viewPager = (ViewPager2) findViewById(R.id.main_viewpager);
        tablayout = (TabLayout) findViewById(R.id.main_tab);

        /*logout = (TextView) findViewById(R.id.logout);
        id_delete = (TextView) findViewById(R.id.id_delete);
        pw_change = (TextView) findViewById(R.id.pw_change);  // 비밀번호 변경 클릭해주는 텍스트뷰
        change_pw = (EditView) findViewById(R.id.change_pw);  // 새로운 비밀번호 입력 받는 에디터뷰*/
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

    // firebase logout
    private void signOut() {
        mAuth.signOut();
    }

    // firebase delete user
    private void id_delete() {
        User.delete();
    }

    // firebase change password
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