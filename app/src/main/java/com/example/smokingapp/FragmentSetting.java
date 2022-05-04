package com.example.smokingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FragmentSetting extends Fragment {
    RecyclerView setting_container;
    ArrayList<Setting> data;
    Context context;

    // firebase 인증 객체 선언
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    // firebase 실시간 데이터 베이스
    private DatabaseReference mDatabaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_fragment_setting, container, false);

        setting_container = (RecyclerView) v.findViewById(R.id.setting_container);
        context = getActivity().getApplicationContext();

        data = new ArrayList<>(10);
        data.add(new Setting(getResources().getString(R.string.setting_account), getResources().getDrawable(R.drawable.ic_noti), true, false));
        data.add(new Setting(getResources().getString(R.string.setting_edit_name), getResources().getDrawable(R.drawable.ic_setting_name), false, false));
        data.add(new Setting(getResources().getString(R.string.setting_logout), getResources().getDrawable(R.drawable.ic_setting_logout), false, false));
        data.add(new Setting(getResources().getString(R.string.setting_system), getResources().getDrawable(R.drawable.ic_noti), true, false));
        data.add(new Setting(getResources().getString(R.string.setting_dark), getResources().getDrawable(R.drawable.ic_setting_dark), false, true));
        data.add(new Setting(getResources().getString(R.string.setting_noti), getResources().getDrawable(R.drawable.ic_noti), false, true));
        data.add(new Setting(getResources().getString(R.string.setting_language), getResources().getDrawable(R.drawable.ic_setting_language), false, false));
        data.add(new Setting(getResources().getString(R.string.setting_help), getResources().getDrawable(R.drawable.ic_setting_help), false, false));

        SettingAdapter adapter = new SettingAdapter(data);
        setting_container.setLayoutManager(new LinearLayoutManager(context));
        setting_container.setAdapter(adapter);

        // firebase
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SmokingApp/UserAccount/" + User.getUid() + "/name");

        //tv.setText(getArguments().getString("msg"));
        return v;
    }

    public static FragmentSetting newInstance(String text) {

        FragmentSetting f = new FragmentSetting();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);
        return f;
    }

}
