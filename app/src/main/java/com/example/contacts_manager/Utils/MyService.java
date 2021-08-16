package com.example.contacts_manager.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.contacts_manager.Activities.ChatActivity;
import com.example.contacts_manager.Models.MessageNotificationModel;
import com.example.contacts_manager.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;


public class MyService extends FirebaseMessagingService {
    Context context;
    static CallbackMethod callbackMethod;

    public MyService() {
    }

    public MyService(Context context) {
        this.context = context;
        callbackMethod = ((CallbackMethod) context);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "from: " + remoteMessage.getFrom());

        context = getApplicationContext();

        if (callbackMethod!=null) {
            callbackMethod.messageReceived();
        }
        if (remoteMessage.getData().size() > 0) {
            sendMessageNotification(remoteMessage.getData().get("name"), remoteMessage.getData().get("body"), remoteMessage.getData().get("time"),remoteMessage.getData().get("senderId"), context);
        }

    }

    public  void sendMessageNotification(String name, String body, String time, String senderId, Context context) {
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_card);
        Intent remoteIntent = new Intent(context, ChatActivity.class);
        remoteIntent.putExtra("userId", senderId);
        remoteIntent.putExtra("name", name);

        remoteIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, remoteIntent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_defaultChannel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setContentTitle(name)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.ic_baseline_contacts_24)
                        .setAutoCancel(true)
                        .setTicker("custom")
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        notificationManager.notify(0, notificationBuilder.build());

    }
}


