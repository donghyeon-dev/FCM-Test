package com.hfad.testfcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "FCM-MessagingService";
    String id = "my_channel_02";
    CharSequence name = "fcm_nt";
    String description = "push";
    int importance = NotificationManager.IMPORTANCE_DEFAULT;
    MediaPlayer mediaPlayer;

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
        if (remoteMessage.getNotification() != null) { // Foreground
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        } else if(remoteMessage.getData().size() > 0 ) {// Background
            sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
        }
    }

    // Foreground / Background 푸시 알림 처리
    private void sendNotification(String messageBody, String messageTitle){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(id,name,importance);

        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mNotificationManager.createNotificationChannel(mChannel);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notifyID = 2;

        String CHANNEL_ID = "my_channel_02";

        try{
            Notification notification = new Notification.Builder(MyFirebaseMessagingService.this)
                    .setContentTitle(URLDecoder.decode(messageTitle,"UTF-8"))
                    .setContentText(URLDecoder.decode(messageBody,"UTF-8"))
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setChannelId(CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .build();
            mNotificationManager.notify(notifyID, notification);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

}
