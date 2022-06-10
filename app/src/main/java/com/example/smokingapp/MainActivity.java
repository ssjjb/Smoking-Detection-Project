package com.example.smokingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final int NUM_PAGES = 3;
    private String[] titles = new String[]{"My Module", "Setting", "Log"};

    private DatabaseReference mDatabaseRef;
    public static Context context;

    ViewPager2 viewPager;
    FragmentStateAdapter pagerAdapter;
    TabLayout tablayout;

    //TextView logout, id_delete, pw_change;
    //EditView change_pw;

    // firebase 추가 코드
    private FirebaseAuth mAuth;
    private FirebaseUser User;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        viewCasting();

        // firebase 추가 코드
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SmokingApp/UserAccount/" + User.getUid());

        pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tablayout, viewPager, (tab, position) -> tab.setText(titles[position])).attach();



        mDatabaseRef.child("emailId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.getValue(String.class);
                Log.e("email", email);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    public void addModule(){
        final EditText et = new EditText(context);
        final EditText et2 = new EditText(context);
        et.setTextColor(Color.parseColor("#000000"));
        et2.setTextColor(Color.parseColor("#000000"));
        et.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.black));
        et2.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.black));
        et.setHintTextColor(getResources().getColor(R.color.gray));
        et2.setHintTextColor(getResources().getColor(R.color.gray));
        et.setHint("Module Name");
        et2.setHint("IP address");
        et.setMaxLines(1);
        et2.setMaxLines(1);
        et.setSingleLine(true);
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        //container.setBackgroundColor(Color.parseColor("#FFFFFF"));
        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        et.setLayoutParams(params);
        et2.setLayoutParams(params);
        container.addView(et);
        container.addView(et2);
        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        alt_bld.setTitle("Add Module").setMessage("Enter Information").setCancelable(false).setView(container).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String moduleName = et.getText().toString();
                        String ipAddress = et2.getText().toString();
                        ModuleAccount module_Account = new ModuleAccount(moduleName, ipAddress);

                        if(!moduleName.equals("")){
                            mDatabaseRef.child("module_list").push().setValue(module_Account);
                            try{
                                HttpConnectModule postData = new HttpConnectModule(email, moduleName, ipAddress, 1, moduleName);
                                String receive = postData.execute().get();
                                Log.e("receive", receive);
                            } catch(InterruptedException e){
                                e.printStackTrace();
                            } catch (ExecutionException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        final AlertDialog alert = alt_bld.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });
        alert.show();
    }

}