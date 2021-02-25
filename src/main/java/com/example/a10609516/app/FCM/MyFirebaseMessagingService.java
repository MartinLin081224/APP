package com.example.a10609516.app.FCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;


import com.example.a10609516.app.R;
import com.example.a10609516.app.Workers.CalendarActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Set;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived:" + remoteMessage.getFrom());
        Intent intent = new Intent();   //點擊啟動到CalendarActivity頁面
        intent.setClass(this, CalendarActivity.class);
        //showNotification(this, remoteMessage, intent);
        Set<String> keys = remoteMessage.getData().keySet();
        for(String s:keys) {
            Log.e("fcm data:", remoteMessage.getData().get(s));
            sendNotification(remoteMessage.getData().get(s));
            MyNotice.getInstance().notifyOnMessageReceived(remoteMessage.getData().get(s));
        }
    }

    /*// 顯示通知
    // remoteMessage.getData()         -  無論app在什麼狀態下皆會執行 MyFirebaseMessagingService(需對應Service設定的字串文字)
    // remoteMessage.getNotification() -  只會在app顯示時,執行app的接收訊息 MyFirebaseMessagingService
    private void showNotification(Context context, RemoteMessage remoteMessage, Intent intent) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        PendingIntent iPending = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.dispatch)
                        .setContentTitle(remoteMessage.getData().get("title")) //需對應Service設定的字串文字
                        .setContentText(remoteMessage.getData().get("body")) //需對應Service設定的字串文字
                        .setVibrate(new long[] {2000 , 1000})
                        .setColor(Color.rgb(6,102,219))
                        .setAutoCancel(true)
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentIntent(iPending);
        Notification notification = mBuilder.build();
        manager.notify(1, notification);

    }*/

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.bwt_icon)
                .setContentTitle("拓霖企業")
                .setContentText(messageBody)
                .setVibrate(new long[] {2000 , 1000})
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setColor(Color.rgb(6,102,219))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}