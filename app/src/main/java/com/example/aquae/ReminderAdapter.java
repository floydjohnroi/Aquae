package com.example.aquae;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private Context context;
    private List<ReminderModel> reminderModelList;

    public ReminderAdapter(Context context, List<ReminderModel> reminderModelList) {
        this.context = context;
        this.reminderModelList = reminderModelList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_reminder, parent, false);

        return new ReminderAdapter.ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        ReminderModel reminderModel = reminderModelList.get(position);

        Picasso.get()
                .load(reminderModel.getImage())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.refillssss)
                .into(holder.image);

        holder.station.setText(reminderModel.getClientName());
        holder.pickupTime.setText(reminderModel.getPickupTime());

        String[] remind = reminderModel.getRemindTime().split(" ");
        holder.remindTime.setText(remind[4]+" "+remind[5]);
        holder.remindDate.setText(remind[0]+" "+remind[1].replace(",", ""));

        holder.reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScheduleActivity.class);
                intent.putExtra("schedule_id", reminderModel.getSchedId());
                intent.putExtra("station", reminderModel.getClientName());
                intent.putExtra("client_id", reminderModel.getClientId());
                intent.putExtra("activity", "reminder");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reminderModelList.size();
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {

        TextView remindTime, remindDate, station, pickupTime;
        ImageView image;
        LinearLayout reminder;

        public ReminderViewHolder(View itemView) {
            super(itemView);

            remindTime = itemView.findViewById(R.id.remind_time);
            remindDate = itemView.findViewById(R.id.remind_date);
            station = itemView.findViewById(R.id.station);
            pickupTime = itemView.findViewById(R.id.pickup_time);
            image = itemView.findViewById(R.id.image);
            reminder = itemView.findViewById(R.id.reminder);

        }
    }
}
