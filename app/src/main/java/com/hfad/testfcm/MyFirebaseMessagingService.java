package com.hfad.testfcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "FCM-MessagingService";

    // 새로운 토큰이 생성되는 경우
   @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed Token is :" + token);
        super.onNewToken(token);
    }


    // FCM에서 메세지 보내기 했을때 메세지 받는 서비스
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null){
            String messageBody = remoteMessage.getNotification().getBody();
            Log.d(TAG, "알림메시지 : " + messageBody);
            String messageTitle = remoteMessage.getNotification().getTitle();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);
            String channelId = "췌널아이디";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this,channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                String channelName = "췌널눼임";
                NotificationChannel channel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0, notificationBuilder.build());


        }


    }
}
