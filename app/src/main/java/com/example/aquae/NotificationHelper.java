package com.example.aquae;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    public static void showNotification(Context context, String title, String body) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HomeActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_aquae_dark)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(1, builder.build());

    }
}
