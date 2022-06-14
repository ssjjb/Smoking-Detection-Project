package com.example.smokingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

public class DataActivity extends AppCompatActivity {
    private String IP_ADDRESS = "";

    ArrayList<DetectionData> data;
    ArrayList<DetectionData> total_data;
    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<String> xLabel = new ArrayList<>();
    ArrayList<Integer> chartData = new ArrayList<>();
    private Thread thread;
    int today_detection_person = 0;
    String current_day = "";

    TextView data_person_day_num, data_person_total_num;
    TextView data_chart_day;
    RecyclerView detail_content;
    DataAdapter adapter;
    BarChart chart;
    Toolbar tb;
    Context context;

    // firebase 인증 객체 선언
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    // firebase 실시간 데이터 베이스
    private DatabaseReference mDatabaseRef;

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.i("DataActivity", "Snapshot = " + snapshot.toString());
            //Log.i("DataActivity", snapshot.child("day_detection_person").getValue().toString());
            //data_person_day_num.setText(snapshot.child("day_detection_person").getValue().toString());
            data_person_total_num.setText(snapshot.child("total_detection_person").getValue().toString());
            IP_ADDRESS = snapshot.child("ip_address").getValue().toString();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        context = getApplicationContext();

        Intent t = getIntent();
        String key = t.getStringExtra("key");

        Log.i("DataActivity", "key = " + key);

        // firebase
        // firebase 추가 코드
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SmokingApp/UserAccount/" + User.getUid()+"/module_list" + "/"+key);

        //id find
        detail_content = (RecyclerView) findViewById(R.id.data_detail_content);
        tb = (Toolbar) findViewById(R.id.data_toolbar);
        chart = (BarChart) findViewById(R.id.data_chart);
        data_chart_day = (TextView) findViewById(R.id.data_chart_day);

        //data 선언
        data = new ArrayList<>();
        total_data = new ArrayList<>();
        chartInit();
        init();

        showLoading();

        // module info
        data_person_day_num = (TextView) findViewById(R.id.data_person_day_num);
        data_person_total_num = (TextView) findViewById(R.id.data_person_total_num);

        if(key != null)
            mDatabaseRef.addValueEventListener(listener);

        // recent adapter
        detail_content.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DataAdapter(data);
        detail_content.setAdapter(adapter);
        detail_content.startLayoutAnimation();


        // toolbar
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        current_day = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        change_date(current_day);
        data_chart_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = 0;
                ArrayList<String> date = new ArrayList<>();
                try {
                    for (int i = 0; i < total_data.size(); i++) {
                        Date t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(total_data.get(i).getTime());
                        String cur_date = new SimpleDateFormat("yyyy-MM-dd").format(t);
                        date.add(cur_date);
                        count++;
                        for (int j = 0; j < date.size() -1; j++) {
                            if (cur_date.equals(date.get(j))){
                                date.remove(date.size()-1);
                                count--;
                            }
                        }
                    }
                }catch(ParseException e){

                }
                DateComparator c = new DateComparator();
                Collections.sort(date, c);
                final String[] list = new String[count];
                for(int i = 0; i < date.size(); i++){
                    list[i] = date.get(i);
                }

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DataActivity.this);
                mBuilder.setTitle("Select Date.");
                mBuilder.setCancelable(true);

                mBuilder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        current_day = list[i];
                        change_date(current_day);
                        refresh_view(list[i]);
                        //updateChart();
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        mDatabaseRef.removeEventListener(listener);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.tb, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.data_realtime:
                Intent t = new Intent(this, CameraActivity.class);
                t.putExtra("ip_address", IP_ADDRESS);
                startActivity(t);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void init(){
        mDatabaseRef.child("smoking_person_list").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                SmokingPerson temp = snapshot.getValue(SmokingPerson.class);
                DetectionData adp_data = new DetectionData(ContextCompat.getDrawable(context, R.drawable.ic_person), temp.getDetection_name(), temp.getDetection_time());
                total_data.add(adp_data);
                add_view(adp_data);
                //updateChart();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void add_view(DetectionData temp){
        try {
            String today = current_day;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(temp.getTime());
            String compare = new SimpleDateFormat("yyyy-MM-dd").format(date);

            int result = today.compareTo(compare);

            if(result ==0){
                data.add(temp);
                today_detection_person++;
                data_person_day_num.setText(Integer.toString(today_detection_person));
            }
        }catch(ParseException e){

        }
        adapter.notifyDataSetChanged();
    }
    static class DateComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date day1 = null;
            Date day2 = null;
            try {
                day1 = format.parse(o1);
                day2 = format.parse(o2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int result = day1.compareTo(day2);
            return result;
        }
    }
    public void change_date(String date){
        String array[] = date.split("-");
        String year = array[0];
        String month = array[1];
        String day = array[2];

        if(month.equals("01")){
            month = "January";
        }
        else if(month.equals("02")){
            month = "February";
        }
        else if(month.equals("03")){
            month = "March";
        }
        else if(month.equals("04")){
            month = "April";
        }
        else if(month.equals("05")){
            month = "May";
        }
        else if(month.equals("06")){
            month = "June";
        }
        else if(month.equals("07")){
            month = "July";
        }
        else if(month.equals("08")){
            month = "August";
        }
        else if(month.equals("09")){
            month = "September";
        }
        else if(month.equals("10")){
            month = "October";
        }
        else if(month.equals("11")){
            month = "November";
        }
        else if(month.equals("12")){
            month = "December";
        }
        String result = day + " " + month + ", " + year;
        data_chart_day.setText(result);
    }
    public void refresh_view(String date){
        data.clear();
        today_detection_person = 0;
        try {
            for (int i = 0; i < total_data.size(); i++) {
                Date t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(total_data.get(i).getTime());
                String cur_date = new SimpleDateFormat("yyyy-MM-dd").format(t);

                int result = cur_date.compareTo(date);
                if(result == 0){
                    data.add(total_data.get(i));
                    today_detection_person++;
                }
            }
        }catch (ParseException e){
            Log.e("compare", "error");
        }
        data_person_day_num.setText(Integer.toString(today_detection_person));
        adapter = new DataAdapter(data);
        detail_content.setAdapter(adapter);
        detail_content.startLayoutAnimation();
        updateChart();
    }
    private void chartInit(){
        for(int i =0; i < 25; i++){
            xLabel.add(Integer.toString(i+1) + "h");
            entries.add(new BarEntry((Integer) i, 0));
        }
        //set axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(8);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);
        chart.getAxisRight().setEnabled(false);

        //set dataset
        BarDataSet set = new BarDataSet(entries, "Detection Value");
        set.setColor(getResources().getColor(R.color.main_theme));
        set.setDrawValues(false);

        BarData barData = new BarData(set);
        chart.setData(barData);
        chart.setDescription(null);
        chart.invalidate();

    }
    private void updateChart(){
        BarData barData = chart.getBarData();
        for(int i =0; i < 25; i++){
            entries.get(i).setY(0);
        }
        Log.e("total", Integer.toString(total_data.size()));
        for (int i = 0; i < total_data.size(); i++) {
            try {
                String today = current_day;
                Date curDateFullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(total_data.get(i).getTime());
                String curDate = new SimpleDateFormat("yyyy-MM-dd").format(curDateFullFormat);

                int result = today.compareTo(curDate);
                if(result == 0) {
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(curDateFullFormat);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);

                    entries.get(hour).setY(entries.get(hour).getY() + 1);
                }
            }catch (ParseException e){
            }
        }
        float max = 10;
        for(int i = 0; i < entries.size(); i++){
            if(entries.get(i).getY() > max)
                max = entries.get(i).getY();
            Log.i("entry", Float.toString(entries.get(i).getY()));
        }
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0l);
        yAxis.setAxisMaximum(max);

        chart.animateY(500, Easing.Linear);
        chart.setData(barData);
        chart.invalidate();

        barData.notifyDataChanged();
        chart.notifyDataSetChanged();
    }
    void showLoading(){
        Dialog progress = new Dialog(DataActivity.this, R.style.LoadingStyle);
        progress.setCancelable(false);
        progress.addContentView(new ProgressBar(DataActivity.this), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        progress.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
                updateChart();
            }
        },1000);
    }

}