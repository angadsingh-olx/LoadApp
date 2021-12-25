package com.udacity

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat

fun NotificationManager.sendNotification(extras: Bundle?, notificationId: Int, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("data", extras)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationId,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            0, applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    notify(notificationId, builder.build())
}


fun NotificationManager.createChannel(activity: Activity, channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
            .apply {
                setShowBadge(false)
            }
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)

        val notificationManager = activity.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)

    }
}