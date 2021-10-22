package com.digitalskies.virtualclothierdemo.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.digitalskies.virtualclothierdemo.ui.mainactivity.fragments.checkoutfragment.CheckOutFragment;
import com.google.codelabs.mdc.java.virtualclothierdemo.R;

public class NotificationCreator {
    private static NotificationChannel channel;
    private static final String NOTIFICATION_TAG = "CartReminder";

    public void buildNotification(String userName, Context context){


        String CHANNEL_ID="CART_REMINDER";

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            channel = new NotificationChannel(CHANNEL_ID,"cart reminder", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Remind user of pending items in cart");
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.enableVibration(false);
            channel.enableLights(true);


        }
        String notificationTitle="Cart Reminder";
        String notificationContent = "Dear "+userName+" you have items pending in your cart";
        final NotificationCompat.Builder builder=new NotificationCompat.Builder(context,CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.icon_shopping_cart)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(notificationContent)
                                    .setBigContentTitle(notificationTitle)
                                    //Summary line is line that appears just above the BigContentTitle when
                                    //notification is expanded
                                    .setSummaryText("cart"))
                .setNumber(1)
                .setContentIntent(
                        PendingIntent.getActivity(context,
                                0,
                                new Intent(context, CheckOutFragment.class),
                                PendingIntent.FLAG_UPDATE_CURRENT)
                )
                .setAutoCancel(true);
        notify(context,builder.build());
    }
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
       final NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
           notificationManager.createNotificationChannel(channel);
           notificationManager.notify(1,notification);
       }
       else{
           notificationManager.notify(NOTIFICATION_TAG.hashCode(),notification);
       }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ECLAIR){
            notificationManager.cancel(NOTIFICATION_TAG,0);
        }
        else{
            notificationManager.cancel(NOTIFICATION_TAG.hashCode());
        }
    }


}
