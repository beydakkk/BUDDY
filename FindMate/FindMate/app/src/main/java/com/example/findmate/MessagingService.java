package com.example.findmate;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @SuppressLint("NewApi")
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = "You have a match request!";
        String text = "Come check this.";
        String CHANNEL_ID = "MESSAGE";
        CharSequence name;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Message Notification",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Context context;
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.notification)
                .setAutoCancel(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {return;}
        NotificationManagerCompat.from(this).notify(1, notification.build());
        super.onMessageReceived(remoteMessage);
    }
}
