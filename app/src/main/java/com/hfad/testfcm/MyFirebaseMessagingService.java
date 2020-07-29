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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "FCM-MessagingService";

    // 새로운 토큰이 생성되거나 refresh 될때 사용한다
    // FirebaseIdInstanceService 의 deprecated로 MessagingService 내에 구현
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed Token is :" + token);
        super.onNewToken(token);
    }


    // 받은 메세지에서 title과 body를 추출한다
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG,"remoteMessage 한글써보자 씨빨 " + remoteMessage);
        try {
            Log.d(TAG,"remoteMessage Encode is " + URLDecoder.decode(remoteMessage.getNotification().getTitle(),"EUC-KR"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (true) {
            } else {
                handleNow();
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Title: " + (remoteMessage.getNotification().getTitle()));
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            try {
                sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    //받은 title과 body로 디바이스에 알림을 전송한다.
    private void sendNotification(String messageTitle, String messageBody) throws UnsupportedEncodingException {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(URLDecoder.decode(messageTitle,"UTF-8"))
                        .setContentText(URLDecoder.decode(messageBody,"UTF-8"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

}
