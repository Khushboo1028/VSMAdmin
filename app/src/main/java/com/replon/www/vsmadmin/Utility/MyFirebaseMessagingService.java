package com.replon.www.vsmadmin.Utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.messaging.FirebaseMessagingService;

import com.google.firebase.messaging.RemoteMessage;
import com.replon.www.vsmadmin.MainActivity;
import com.replon.www.vsmadmin.R;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    NotificationManager notificationManager;
    Intent notificationIntent;
    PendingIntent intent;
    String title,message,action;
    String replyLabel;
    RemoteInput remoteInput;
    NotificationCompat.Action replyAction;
    private static String KEY_REPLY = "key_reply_message";
    NotificationCompat.Builder notificationBuilder;

    public MyFirebaseMessagingService() {
        super();
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG,"IN ON MESSAGE RECEIVED");


        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Setting up Notification channels for android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
            action = remoteMessage.getData().get("click_action");


            notificationIntent = new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent.putExtra("title",title);
            notificationIntent.putExtra("message",message);
            notificationIntent.putExtra("click_action", action);
            Log.i(TAG,"ACTION NOTIF FROM DATA: "+action);

            intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (action.equalsIgnoreCase("messages")){
                Log.i(TAG,"BUILDING REPLY NOTIF");
                replyLabel = "REPLY";
                remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_notification, replyLabel, intent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();
            }




        }

         if(remoteMessage.getNotification()!=null) {

            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();




        }

        notificationBuilder = new NotificationCompat.Builder(this, "NOTIF")
                .setSmallIcon(R.drawable.ic_notification)  //a resource for your custom small icon
                .setContentTitle(title) //the "title" value you sent in your notification
                .setContentText(message) //ditto
                .setContentIntent(intent)
                .setAutoCancel(true);


        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
//        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
//        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("NOTIF", "Notifications", NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription("This channel is for all notifications");
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }



    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }


}
