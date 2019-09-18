package com.example.aquae;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>  {

    private Context context;
    private List<ScheduleModel> scheduleModelList;

    public ScheduleAdapter(Context context, List<ScheduleModel> scheduleModelList) {
        this.context = context;
        this.scheduleModelList = scheduleModelList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_schedule, parent, false);

        return new ScheduleAdapter.ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleModel scheduleModel = scheduleModelList.get(position);

        if ("Sun – Mon – Tue – Wed – Thu – Fri – Sat".equals(scheduleModel.getSchedule())) {
            holder.schedule.setText("Everyday");
        }
        else if ("Mon – Tue – Wed – Thu – Fri".equals(scheduleModel.getSchedule())) {
            holder.schedule.setText("Weekdays");
        }
        else if ("Sun – Sat".equals(scheduleModel.getSchedule())) {
            holder.schedule.setText("Weekends");
        }
        else {
            if (scheduleModel.getSchedule().length() > 3) {
                String finalStr = scheduleModel.getSchedule().substring(0, scheduleModel.getSchedule().lastIndexOf("–")) + "&" + scheduleModel.getSchedule().substring(scheduleModel.getSchedule().lastIndexOf("–") + 1);
                holder.schedule.setText(finalStr.replace(" – ", ", "));
            }
            else {
                if ("Sun".equals(scheduleModel.getSchedule())) {
                    holder.schedule.setText("Sunday");
                }
                if ("Mon".equals(scheduleModel.getSchedule())) {
                    holder.schedule.setText("Monday");
                }
                if ("Tue".equals(scheduleModel.getSchedule())) {
                    holder.schedule.setText("Tuesday");
                }
                if ("Wed".equals(scheduleModel.getSchedule())) {
                    holder.schedule.setText("Wednesday");
                }
                if ("Thu".equals(scheduleModel.getSchedule())) {
                    holder.schedule.setText("Thursday");
                }
                if ("Fri".equals(scheduleModel.getSchedule())) {
                    holder.schedule.setText("Friday");
                }
                if ("Sat".equals(scheduleModel.getSchedule())) {
                    holder.schedule.setText("Saturday");
                }
            }
        }

        holder.station.setText(scheduleModel.getClientName());

//        FirebaseDatabase.getInstance().getReference().child("clients")
//                .orderByChild("client_id").equalTo(scheduleModel.getClientId())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            holder.station.setText(String.valueOf(snapshot.child("company").getValue()));
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

        holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScheduleActivity.class);
                if (!holder.onOff.isChecked()) {
                    intent.putExtra("switch", "off");
                }
                else {
                    intent.putExtra("switch", "on");
                }
                intent.putExtra("schedule_id", scheduleModel.getSchedId());
                intent.putExtra("station", holder.station.getText());
                intent.putExtra("client_id", scheduleModel.getClientId());
                context.startActivity(intent);
            }
        });

        if ("off".equals(scheduleModel.getOnOff())) {
            holder.onOff.setChecked(false);
        }
        else {
            holder.onOff.setChecked(true);
        }

        holder.onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase.getInstance().getReference().child("schedules")
                            .orderByChild("schedule_id").equalTo(scheduleModel.getSchedId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().child("switch").setValue("on");

                                        if (new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()).equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                            Toast.makeText(context, "Schedule set today", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            if ("Sun".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Mon".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Tue".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Wed".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Thu".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Fri".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Sat".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(context, "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("schedules")
                            .orderByChild("schedule_id").equalTo(scheduleModel.getSchedId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().child("switch").setValue("off");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }
            }
        });






    }

    @Override
    public int getItemCount() {
        return scheduleModelList.size();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {

        TextView station, schedule;
        Switch onOff;
        MaterialCardView materialCardView;
        ImageView remove;

        public ScheduleViewHolder(View itemView) {
            super(itemView);

            station = itemView.findViewById(R.id.station);
            schedule = itemView.findViewById(R.id.schedule);
            onOff = itemView.findViewById(R.id.on_off);
            materialCardView = itemView.findViewById(R.id.materialCardView);
            remove = itemView.findViewById(R.id.remove);
        }
    }
}
