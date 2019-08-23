package com.quidish.anshgupta.login.CloudMessaging;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.quidish.anshgupta.login.Home.BottomNavigation.BottomNavigationDrawerActivity;
import com.quidish.anshgupta.login.R;

import static android.support.v4.app.NotificationCompat.*;


@SuppressLint("Registered")
    public class MyFirebaseMessagingService extends FirebaseMessagingService {

        @Override
        public void onMessageReceived(RemoteMessage message) {
            sendMyNotification(message.getNotification().getBody(),message.getNotification().getTitle());
        }


        private void sendMyNotification(String message,String title) {

            //On click of notification it redirect to this Activity
            Intent intent = new Intent(this, BottomNavigationDrawerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Builder notificationBuilder = new Builder(this)
                    .setSmallIcon(R.drawable.quid2)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setLargeIcon(BitmapFactory.decodeResource
                            (getResources(), R.drawable.quid2))
                    .setBadgeIconType(R.drawable.quid2)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            assert notificationManager != null;
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
