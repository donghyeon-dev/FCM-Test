package com.hfad.testfcm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "Main-Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FirebaseInstanceIdService가 deprecated 되면서, onTokenRefresh 메서드를 사용할 수 없다.
        // FirebaseMessagingService의 onNewToken을 사용해서 토큰을 만들거나 refresh 할때 사용한다.
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
                MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.d("Device Token is => ", newToken);

            }
        });
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if(extras != null){
            for(String key: extras.keySet()){
                Object value = extras.get(key);
                Log.d(TAG,"extras received at onCreate : key :" + key + "Value: " + value);
            }
            String title = extras.getString("title");
            String message = extras.getString("body");
            if(message != null && message.length()>0){
                getIntent().removeExtra("body");
                showNotificationInADialog(title, message);
            }
        }

    }

    @Override
    // 새 인텐트가 이 클래스에 의해 생성될때 불러짐
    // 주로 앱이 백그라운드에 있을때, 알림이 알림창으로 갈때, 유저가 알림을 클릭했을때
    protected void onNewIntent(Intent intent) {
       super.onNewIntent(intent);
       Log.d(TAG,"onNewIntent - 시작");
       Bundle extras = intent.getExtras();
        if(extras != null){
            for(String key: extras.keySet()){
                Object value = extras.get(key);
                Log.d(TAG,"extras received at onCreate : key :" + key + "Value: " + value);
            }
            String title = extras.getString("title");
            String message = extras.getString("body");
            if(message != null && message.length()>0){
                getIntent().removeExtra("body");
                showNotificationInADialog(title, message);
            }
       }
    }
    // 전해진 title과 message로 dialog를 만듬
    private void showNotificationInADialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
