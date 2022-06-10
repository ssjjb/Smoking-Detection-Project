package com.example.smokingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentModule extends Fragment {
    public static Context context;

    FloatingActionButton floatingActionButton;
    RecyclerView moduleContainer;
    ArrayList<Module> data;
    public String ip = "";

    // firebase 인증 객체 선언
    private FirebaseAuth mAuth;
    // firebase 실시간 데이터 베이스
    private DatabaseReference mDatabaseRef;

    int mDatabaseCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_fragment_module, container, false);

        context = getActivity().getApplicationContext();

        // firebase
        mAuth = FirebaseAuth.getInstance();

        // view 선언
        moduleContainer = (RecyclerView) v.findViewById(R.id.main_module_container);
        floatingActionButton = (FloatingActionButton) v.findViewById(R.id.fab);

        moduleContainer.setLayoutManager(new GridLayoutManager(context, 2));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fab_intent = new Intent(context, FabActivity.class);
                startActivity(fab_intent);
            }
        });

        InitDB();

        //TextView tv = (TextView) v.findViewById(R.id.textView1);
        //tv.setText(getArguments().getString("msg"));

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.i("FragmentModule", "onStart()  => " + data.size());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("FragmentModule", "onResume()  => " );
    }

    public static FragmentModule newInstance(String text) {

        FragmentModule f = new FragmentModule();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);
        return f;
    }



    private void InitDB() {
        // Firebase DB
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SmokingApp/UserAccount/" + mAuth.getCurrentUser().getUid());

        // Read DB - DB 전체 변경에 대한 사항 읽고 수신 대기 Listener
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Log.i("FragmentModule", "snapshot.getChildren() : " + snapshot.getChildren().toString());

                int module_count = (int) snapshot.child("module_list").getChildrenCount();
                int color;

                data = new ArrayList<>(module_count);

                for (DataSnapshot userSnapshot : snapshot.child("module_list").getChildren()){

                    String moduleUID = userSnapshot.getKey();
                    String title = userSnapshot.child("name").getValue().toString();
                    String time = userSnapshot.child("last_detection_time").getValue().toString();
                    int detected_person=Integer.parseInt(userSnapshot.child("day_detection_person").getValue().toString());
                    int detected_smoking =Integer.parseInt(userSnapshot.child("total_detection_person").getValue().toString());
                    String ip = userSnapshot.child("ip_address").getValue().toString();
                    color = (int)((Math.random()*10000)%4) + 1;

                    data.add(new Module(title,time,detected_person,detected_smoking,3, ip, moduleUID));
                }


                MainAdapter adapter = new MainAdapter(data);
                moduleContainer.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("FragmentModule", "readDB() => onCancelled()");
            }
        });
    }

}

