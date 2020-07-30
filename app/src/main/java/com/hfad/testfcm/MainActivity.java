package com.hfad.testfcm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "Main-Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FirebaseInstanceIdService가 deprecated 되면서, onTokenRefresh 메서드를 사용할 수 없다.
        // FirebaseMessagingService의 onNewToken을 사용해서 토큰을 만들거나 refresh 할때 사용한다.
        // 앱이 실행될때 토큰이 생성되고, 변경되지않고 계속 되는듯
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
        // "ALL" 을 추가하면 해당 디바이스는 ALL을 주제로 등록한다는것이다.
        // FCM에서 디바이스 토큰으로도 날릴 수 있고, 해당 주제에 등록된 사람들을 대상으로 날리는 방법도 있다.
        // 주제 등록은 즉시 안되고 최대 몇일까지 소요될 수 있으므로 급하게 하진 말자.
//        FirebaseMessaging.getInstance().subscribeToTopic("All");

        checkNotificationPermission();
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

    // 커스텀 알림 권한에 대해 권한 체크
    private boolean checkNotificationPermission(){
        boolean checkPermission = false;

        int hasNotificationPermission = ContextCompat.checkSelfPermission(this, "com.hfad.testfcm.NOTIFICATION");
        if(hasNotificationPermission == PackageManager.PERMISSION_GRANTED){
            checkPermission = true;
            Log.d(TAG,"알림 권한 있음 : " + PackageManager.PERMISSION_GRANTED);
        } else {
            Log.d(TAG,"알림 권한 없음 : " + PackageManager.PERMISSION_GRANTED);
            requestPermission();
        }
        return checkPermission;
    }

    private void requestPermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("알림 동의")
                .setMessage("주기적으로 출퇴근에 관한 알림을 수신하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "알림 수신을 동의하셨습니다.",Toast.LENGTH_SHORT).show();
                        // "YES" 라는 주제로 구독시킴
                        FirebaseMessaging.getInstance().subscribeToTopic("YES");
                        Log.d(TAG,"DialogInterface, 알림 수신 동의");
                        // 동의 버튼을 눌렀을때 서버로 deviceToken을 보내는 메서드를 구현해서 넣으면 될듯.
                    }
                });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "알림 수신을 거부하셨습니다.",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"DialogInterface, 알림 수신 거부");
            }
        });
        builder.create().show();
    }
}
