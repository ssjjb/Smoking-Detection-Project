package com.example.smokingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private ArrayList<Module> m_data;
    TextView title, time, person, smoking;
    ConstraintLayout layout;
    Context context;
    String email = ((MainActivity) MainActivity.context).email;
    private String flaskIP = "";


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
                        t.putExtra("key", m_data.get(pos).getModuleUID());
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
                String ip_address = m_data.get(getAdapterPosition()).getIp_address();

                switch (menuItem.getItemId()){
                    case 1001:
                        final EditText et = new EditText(context);
                        final EditText et2 = new EditText(context);
                        et.setTextColor(Color.parseColor("#000000"));
                        et2.setTextColor(Color.parseColor("#000000"));
                        et.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.black));
                        et2.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.black));
                        et.setHintTextColor(context.getResources().getColor(R.color.gray));
                        et2.setHintTextColor(context.getResources().getColor(R.color.gray));
                        et.setHint("Module Name");
                        et2.setHint("IP address");
                        et.setMaxLines(1);
                        et2.setMaxLines(1);
                        et.setSingleLine(true);
                        et.setText(module_name);
                        et2.setText(ip_address);
                        LinearLayout container = new LinearLayout(context);
                        container.setOrientation(LinearLayout.VERTICAL);
                        //container.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                        params.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                        et.setLayoutParams(params);
                        et2.setLayoutParams(params);
                        container.addView(et);
                        container.addView(et2);
                        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                        alt_bld.setTitle("Edit Module").setMessage("Enter Information").setCancelable(false).setView(container).setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 수정 완료
                                        String name = et.getText().toString();
                                        String ip = et2.getText().toString();

                                        DatabaseReference edit_module_name = mDatabaseRef.child(User.getUid().toString()).child("module_list").child(m_data.get(getAdapterPosition()).getModuleUID());
                                        edit_module_name.child("name").setValue(name);
                                        edit_module_name.child("ip_address").setValue(ip);
                                        Log.e("email", email);

                                        SharedPreferences appData = context.getSharedPreferences("appData", Context.MODE_PRIVATE);
                                        flaskIP = appData.getString("ip", "");

                                        try{
                                            HttpConnectModule postData = new HttpConnectModule(email, name, ip, 2, module_name, flaskIP);
                                            String receive = postData.execute().get();
                                            Log.e("receive", receive);
                                        } catch(InterruptedException e){
                                            e.printStackTrace();
                                        } catch (ExecutionException e){
                                            e.printStackTrace();
                                        }

                                        m_data.get(getAdapterPosition()).setTitle(name);
                                        m_data.get(getAdapterPosition()).setIp_address(ip);
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
                        break;
                    case 1002:
                        mDatabaseRef.child(User.getUid().toString()).child("module_list").child(m_data.get(getAdapterPosition()).getModuleUID()).setValue(null);
                        SharedPreferences appData = context.getSharedPreferences("appData", Context.MODE_PRIVATE);
                        flaskIP = appData.getString("ip", "");
                        try{
                            HttpConnectModule postData = new HttpConnectModule(email, module_name, ip_address, 3, module_name, flaskIP);
                            String receive = postData.execute().get();
                            Log.e("receive", receive);
                        } catch(InterruptedException e){
                            e.printStackTrace();
                        } catch (ExecutionException e){
                            e.printStackTrace();
                        }
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

