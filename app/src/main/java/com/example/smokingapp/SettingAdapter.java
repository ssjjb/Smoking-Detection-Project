package com.example.smokingapp;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {
    private ArrayList<Setting> m_data;
    TextView title, child_title;
    ImageView icon, right;
    Switch toggle;
    ConstraintLayout layout, layout_child;
    Context context;
    String themeColor;

    // firebase 인증 객체 선언
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    // firebase 실시간 데이터 베이스
    private DatabaseReference mDatabaseRef;

    public class ViewHolder extends RecyclerView.ViewHolder{

        ViewHolder(View itemView){
            super(itemView);


            title = (TextView) itemView.findViewById(R.id.setting_title);
            child_title = (TextView) itemView.findViewById(R.id.setting_child_title);
            icon = (ImageView) itemView.findViewById(R.id.setting_child_img);
            layout = (ConstraintLayout) itemView.findViewById(R.id.setting_title_section);
            layout_child = (ConstraintLayout) itemView.findViewById(R.id.setting_child_section);
            right = (ImageView) itemView.findViewById(R.id.setting_right);
            toggle = (Switch) itemView.findViewById(R.id.setting_toggle);

            // firebase
            mAuth = FirebaseAuth.getInstance();
            User = mAuth.getCurrentUser();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("SmokingApp");


            // item click event(건들지 말것)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                }
            });

            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos==1) {
                        //Toast.makeText(context, m_data.get(getAdapterPosition()).getTitle(), Toast.LENGTH_SHORT).show();
                        final EditText et = new EditText(context);
                        et.setTextColor(Color.parseColor("#000000"));
                        et.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.black));
                        et.setHintTextColor(context.getResources().getColor(R.color.gray));
                        et.setHint("Account Name");
                        et.setMaxLines(1);
                        et.setSingleLine(true);
                        LinearLayout container = new LinearLayout(context);
                        container.setOrientation(LinearLayout.VERTICAL);
                        //container.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                        params.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                        et.setLayoutParams(params);
                        container.addView(et);
                        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                        alt_bld.setTitle("Edit Name").setMessage("Enter Name").setCancelable(false).setView(container).setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if(!et.getText().toString().equals("")) {
                                            String edit_info = et.getText().toString();
                                            DatabaseReference editname = FirebaseDatabase.getInstance().getReference("SmokingApp/UserAccount/" + User.getUid() + "/name");
                                            editname.setValue(edit_info);
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
                                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.black));
                                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.black));
                            }
                        });
                        alert.show();
                    }


                    else if(pos==2){
                        mAuth.signOut();
                        Intent t = new Intent(context, LoginActivity.class);
                        context.startActivity(t);
                        // 추가해야하는거 : 로그인 화면 으로 이동
                    }

                    // Language 변경 함수 호출
                    else if(pos==6){
                        changeLanguage();
                    }
                }
            });


            toggle.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            int pos = getAdapterPosition();

                            // Dark Mode
                            if (pos == 4) {
                                saveLanguage();
                                SharedPreferences.Editor editor = context.getSharedPreferences("Setting", Context.MODE_PRIVATE).edit();
                                if (isChecked == true) {
                                    Log.i("SettingAdapter", "isChecked==true");

                                    themeColor = ThemUtil.DARK_MODE;
                                    ThemUtil.applyTheme(themeColor);
                                    ThemUtil.modSave(context.getApplicationContext(), themeColor);
                                    Toast.makeText(context, "dark mode on", Toast.LENGTH_SHORT).show();
                                    editor.putString("My_DM", "True");
                                } else {
                                    saveLanguage();
                                    Log.i("SettingAdapter", "isChecked==false");
                                    themeColor = ThemUtil.LIGHT_MODE;
                                    ThemUtil.applyTheme(themeColor);
                                    ThemUtil.modSave(context.getApplicationContext(), themeColor);
                                    Toast.makeText(context, "dark mode off", Toast.LENGTH_SHORT).show();
                                    editor.putString("My_DM", "false");
                                }
                                editor.apply();

                                // Notification 함수 호출
                            } else if (pos == 5) {
                                if (isChecked) {
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "My Notification");
                                    builder.setContentTitle("Smoking App");
                                    builder.setContentText("앱을 실행 중...");
                                    builder.setSmallIcon(R.drawable.ic_launcher_background);
                                    builder.setAutoCancel(true);

                                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                                    managerCompat.notify(1, builder.build());

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
                                        NotificationManager manager = context.getSystemService(NotificationManager.class);
                                        manager.createNotificationChannel(channel);
                                    }
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        NotificationManager manager = context.getSystemService(NotificationManager.class);
                                        manager.cancel(1);

                                    }

                                }

                            }
                        }
                    });
        }
    }

    // 언어 변경 함수 정의
    public void changeLanguage(){
        final String[] list = {"한국어", "中文", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Choose Language.");
        mBuilder.setCancelable(true);

        mBuilder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 한국어
                if (i == 0){
                    setLanguage(context, "ko");
//                    Intent intent = ((Activity)context).getIntent();
//                    ((Activity)context).finish();
//                    context.startActivity(intent);
                    ((Activity)context).recreate();
                }
                // 중국어
                else if(i == 1){
                    setLanguage(context,"zh");
//                    Intent intent = ((Activity)context).getIntent();
//                    ((Activity)context).finish();
//                    context.startActivity(intent);
                    ((Activity)context).recreate();
                }
                // 영어
                else if(i == 2){
                    setLanguage(context,"");
//                  Intent intent = ((Activity)context).getIntent();
//                  ((Activity)context).finish();
//                  context.startActivity(intent);
                    ((Activity)context).recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    // Locale 함수 정의
    public void setLanguage(Context context, String language){
        Locale locale = new Locale(language);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());

        // 언어 저장
        SharedPreferences.Editor editor = context.getSharedPreferences("Setting",Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang","");
        editor.apply();
    }

    // 언어 저장 함수 정의
    public void saveLanguage(){
        SharedPreferences prefs = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLanguage(context, language);
    }

    // Context 함수 정의
    public static Context mContext;


    SettingAdapter(ArrayList<Setting> list){
        m_data = list;
    }

    @Override
    public SettingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_settings, parent, false);
        SettingAdapter.ViewHolder vh = new SettingAdapter.ViewHolder(view);


        return vh ;
    }

    @Override // 여기서 초기상태를 바꿔줌, ex)
    public void onBindViewHolder(SettingAdapter.ViewHolder holder, int position) {
        Setting temp = m_data.get(position) ;

        if(position == 4) {
            Log.i("SettingAdapter", "onBindViewHolder() " + position + " : isFlag : " + temp.isFlag() + " : isToggle :  " + temp.isToggle());

            SharedPreferences prefs = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
            String dmMode = prefs.getString("My_DM","");
            Log.i("SettingAdapter", "onBindViewHolder()  dmMode : " + dmMode);

            if(dmMode=="True") toggle.setChecked(true);
            else toggle.setChecked(false);
        }

        if(temp.isFlag()){  // title
            layout_child.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            title.setText(temp.getTitle());

        }
        else{
            layout_child.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
            child_title.setText(temp.getTitle());
            icon.setImageDrawable(temp.getIcon());
            if(temp.isToggle()){
                right.setVisibility(View.GONE);
            }
            else{
                toggle.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return m_data.size() ;
    }
}
