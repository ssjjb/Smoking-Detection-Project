package com.example.smokingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{

    private ArrayList<DetectionData> detectionData;
    ImageView img;
    TextView title, time;
    TextView data_person_day_num, data_person_total_num;

    public class ViewHolder extends RecyclerView.ViewHolder{

        ViewHolder(View itemView){
            super(itemView);

            data_person_day_num = (TextView) itemView.findViewById(R.id.data_person_day_num);
            data_person_total_num = (TextView) itemView.findViewById(R.id.data_person_total_num);

            title = (TextView) itemView.findViewById(R.id.detail_title);
            time = (TextView) itemView.findViewById(R.id.detail_time);
            img = (ImageView) itemView.findViewById(R.id.detail_pic);
        }
    }

    DataAdapter(ArrayList<DetectionData> list){
        detectionData = list;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_detail, parent, false);
        DataAdapter.ViewHolder vh = new DataAdapter.ViewHolder(view);

        return vh ;
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        DetectionData temp = detectionData.get(position) ;

        img.setImageDrawable(temp.getIcon());
        title.setText(temp.getTitle());
        time.setText(temp.getTime());

    }


    @Override
    public int getItemCount() {
        return detectionData.size() ;
    }

    public void remove(){
        detectionData.clear();
        notifyDataSetChanged();
    }
}
