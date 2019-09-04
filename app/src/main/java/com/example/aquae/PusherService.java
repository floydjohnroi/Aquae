package com.example.aquae;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class PusherService extends Service {
    PusherOptions options;
    Pusher pusher;
    Channel channel;

    @Override
    public void onCreate() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "application/json");
        HttpAuthorizer authorizer = new HttpAuthorizer("http://192.168.1.9/pusher/auth.php");
        authorizer.setHeaders(headers);
        options = new PusherOptions();
        options.setCluster("ap1");
        options.setAuthorizer(authorizer);
        pusher = new Pusher("4de9228ab3e254f06ee8", options);


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
               if (change.getCurrentState() == ConnectionState.CONNECTED) {
                   channel = pusher.subscribePrivate("private-update", new PrivateChannelEventListener() {
                       @Override
                       public void onAuthenticationFailure(String message, Exception e) {
                           Log.d("errorText", message);
                       }

                       @Override
                       public void onSubscriptionSucceeded(String channelName) {
                            channel.bind("client-update", new PrivateChannelEventListener() {
                                @Override
                                public void onAuthenticationFailure(String message, Exception e) {

                                }

                                @Override
                                public void onSubscriptionSucceeded(String channelName) {

                                }

                                @Override
                                public void onEvent(PusherEvent event) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(event.getData());
                                        if (jsonObject.getString("id").equals(new Session(getApplicationContext()).getId())) {
                                            if (jsonObject.getString("name").equals("orders")) {
                                                sendOrderNotification(jsonObject.getString("order_id"), jsonObject.getString("payment"),  jsonObject.getString("message"));
                                            }
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                       }

                       @Override
                       public void onEvent(PusherEvent event) {
                           Log.d("getDataTa", event.getData());
                       }
                   });
               }
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.d("errorCode", message);

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendOrderNotification(String orderId, String payment, String messageBody) {

        if (payment.equals("Aquae Wallet")) {

            FirebaseDatabase.getInstance().getReference().child("orders")
                    .orderByChild("order_id").equalTo(orderId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (Objects.equals(snapshot.child("status").getValue(), "accepted")) {

                                    FirebaseDatabase.getInstance().getReference().child("customers")
                                            .orderByChild("customer_id").equalTo(String.valueOf(snapshot.child("customer_id").getValue()))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                        int w = Integer.parseInt(String.valueOf(snapshot1.child("wallet").getValue()))
                                                                - Integer.parseInt(String.valueOf(snapshot.child("total_amount").getValue()));
                                                        snapshot1.getRef().child("wallet").setValue(String.valueOf(w));
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                    FirebaseDatabase.getInstance().getReference().child("clients")
                                            .orderByChild("client_id").equalTo(String.valueOf(snapshot.child("client_id").getValue()))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                        int w = Integer.parseInt(String.valueOf(snapshot1.child("wallet").getValue()))
                                                                + Integer.parseInt(String.valueOf(snapshot.child("total_amount").getValue()));
                                                        snapshot1.getRef().child("wallet").setValue(String.valueOf(w));
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.icon_aquae_dark)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
