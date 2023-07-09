package com.temp.chatapps_hayoo.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.activities.ChatActivity;
import com.temp.chatapps_hayoo.models.User;
import com.temp.chatapps_hayoo.utilities.Constants;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        System.out.println("MESSAGE RECEIVED !!");
        System.out.println("USER ID =" + message.getData().get(Constants.KEY_USER_ID));
        System.out.println("USER NAME =" + message.getData().get(Constants.KEY_NAME));
        System.out.println("USER MESSAGE =" + message.getData().get(Constants.KEY_MESSAGE));
        System.out.println("USER FCM TOKEN =" + message.getData().get(Constants.KEY_FCM_TOKEN));

        User user = new User();
        user.setId(message.getData().get(Constants.KEY_USER_ID));
        user.setName(message.getData().get(Constants.KEY_NAME));
        user.setFcmToken(message.getData().get(Constants.KEY_FCM_TOKEN));

        int notificationID = new Random().nextInt();
        String channelID = "chat_message";

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER, user);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        builder.setContentTitle(user.getName());
        builder.setContentText(message.getData().get(Constants.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                message.getData().get(Constants.KEY_MESSAGE)
        ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Chat Message";
            String channelDescription = "This notification channel is used for chat message notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationID, builder.build());
    }

}
