package com.example.aquae;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    ScheduleAlarm scheduleAlarm = new ScheduleAlarm();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        FirebaseDatabase.getInstance().getReference().child("schedules")
                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        scheduleAlarm.cancelAlarm(getApplicationContext());
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if ("scheduled".equals(snapshot.child("status").getValue())
                                    && "on".equals(snapshot.child("switch").getValue())) {

                                if (new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date())
                                        .equals(snapshot.child("schedule").getValue())) {

                                    scheduleAlarm.setAlarm(getApplicationContext(), 17, 30);

                                }
                                else {
                                    scheduleAlarm.cancelAlarm(getApplicationContext());
                                }

                            }
                            else {
                                scheduleAlarm.cancelAlarm(getApplicationContext());
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
