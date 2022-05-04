package com.example.smokingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private ArrayList<Module> m_data;
    TextView title, time, person, smoking;
    ConstraintLayout layout;
    Context context;

    // firebase 인증 객체 선언
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    // firebase 실시간 데이터 베이스
    private DatabaseReference mDatabaseRef;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        ViewHolder(View itemView){
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            User = mAuth.getCurrentUser();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("SmokingApp/UserAccount");
            title = (TextView) itemView.findViewById(R.id.main_custom_title);
            time = (TextView) itemView.findViewById(R.id.main_custom_lasttime);
            person = (TextView) itemView.findViewById(R.id.main_custom_person);
            smoking = (TextView) itemView.findViewById(R.id.main_custom_smoking);
            layout = (ConstraintLayout) itemView.findViewById(R.id.main_custom_layout);

            // item click event(건들지 말것)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent t = new Intent(context, DataActivity.class);
                        t.putExtra("key", m_data.get(pos).getTitle());
                        Log.i("MainAdapter", "key : " + m_data.get(pos).getTitle());
                        context.startActivity(t);
                    }
                }
            });

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
            MenuItem edit = menu.add(Menu.NONE, 1001, 1, "Name Edit");
            MenuItem delete = menu.add(Menu.NONE, 1002, 2, "Delete");
            edit.setOnMenuItemClickListener(onEditMenu);
            delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                String module_name = m_data.get(getAdapterPosition()).getTitle();
                String module_time = m_data.get(getAdapterPosition()).getTime();
                int module_person = m_data.get(getAdapterPosition()).getDetected_person();
                int moudle_smokingperson = m_data.get(getAdapterPosition()).getDetected_smoking();

                switch (menuItem.getItemId()){
                    case 1001:
                        /*
                        Toast.makeText(context, m_data.get(getAdapterPosition()).getTitle(), Toast.LENGTH_SHORT).show();
                        final EditText et = new EditText(context);
                        final int pos = getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION) {
                            et.setHint("Enter module name");
                            et.setTextColor(Color.parseColor("#FFFFFF"));
                            //et.setPadding(0,context.getResources().getDimensionPixelSize(R.dimen.dialog_top_margin),0,0);

                            FrameLayout container = new FrameLayout(context);
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                            params.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                            et.setLayoutParams(params);

                            container.addView(et);

                            final AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, R.style.dialog_style);
                            alt_bld.setTitle("Modify").setMessage("Please enter a correction.").setIcon(R.drawable.ic_setting_name).setCancelable(false).setView(container).setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // 수정 완료
                                            String message = et.getText().toString();
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                            DatabaseReference edit_module_name = mDatabaseRef.child(User.getUid().toString()).child("module_list").child(module_name);
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            final AlertDialog alert = alt_bld.create();
                            alert.show();
                        }
                      
                         */ //edit 코드 key값을 모듈이름으로 한 관계로 아직 미구현
                        Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
                        break;
                    case 1002:
                        Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
                        mDatabaseRef.child(User.getUid().toString()).child("module_list").child(module_name).setValue(null);
                        Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        };
    }

    MainAdapter(ArrayList<Module> list){
        m_data = list;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom, parent, false);
        MainAdapter.ViewHolder vh = new MainAdapter.ViewHolder(view);

        return vh ;
    }

    @Override // 여기서 초기상태를 바꿔줌, ex)
    public void onBindViewHolder(MainAdapter.ViewHolder holder, int position) {
        Module temp = m_data.get(position) ;

        title.setText(temp.getTitle());
        time.setText(temp.getTime());
        person.setText(Integer.toString(temp.getDetected_person()));
        smoking.setText(Integer.toString(temp.getDetected_smoking()));

        switch(temp.getColor()){
            case 1:
                layout.setBackground(context.getResources().getDrawable(R.drawable.main_custom_background_blue));
                person.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.custom_blue_deep)));
                smoking.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.custom_blue_deep)));
                break;
            case 2:
                layout.setBackground(context.getResources().getDrawable(R.drawable.main_custom_background_darkblue));
                person.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.custom_darkblue_deep)));
                smoking.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.custom_darkblue_deep)));
                break;
            case 3:
                layout.setBackground(context.getResources().getDrawable(R.drawable.main_custom_background_purple));
                person.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.custom_purple_deep)));
                smoking.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.custom_purple_deep)));
                break;
            case 4:
                layout.setBackground(context.getResources().getDrawable(R.drawable.main_custom_background_pink));
                person.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.custom_pink_deep)));
                smoking.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.custom_pink_deep)));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return m_data.size() ;
    }
}

