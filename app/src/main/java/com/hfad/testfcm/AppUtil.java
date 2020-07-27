package com.hfad.testfcm;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.util.List;

public class AppUtil {
    public static final String TAG = "[test-AppUtil]";

    //최상위에서 실행중인 앱패키지이름과 해당 프로젝트의 패키지네임과 비교해 booelan type return
    public static boolean isRunnigApp(Context context){

        // 현재 앱의 패키지명
        String packageName = context.getApplicationInfo().packageName;
        // 실행중인 앱의 패키지명
        String runPackageName = "";

        ActivityManager activityManager = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);

        //API 21에서 getRunningTasks deprecated 됨
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            runPackageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
            Log.d(TAG, "Run Pacakage(21이하) : " + runPackageName);
        } else{
            List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo processInfo : processInfos){
                if(processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    if(processInfo.processName.equals(packageName)){
                        runPackageName = processInfo.processName;
                        Log.d(TAG, "RunningAppProcessInfo : " + processInfo.processName);
                        break;
                    }
                }
            }
            Log.d(TAG,"Run Package(21이하) : " + runPackageName);
        }
        return (packageName.equals(runPackageName)) ? true: false;
    }

    //화면이 켜져있는지 확인
    public static boolean isScreenOn(Context context){

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        boolean isScreenOn = true;

        //API 20에서 isScreenOn deprecated
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH){
            isScreenOn = pm.isScreenOn();
            Log.d(TAG,"Screen on in API20 이하" + isScreenOn);
        } else{
            isScreenOn = pm.isInteractive();
            Log.d(TAG,"Screen on in API20 이상" + isScreenOn);
        }
        return isScreenOn;
    }


}
