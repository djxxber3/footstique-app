package com.footstique.live;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // طباعة رسالة للتأكد من استقبال الإشعار
        Log.d(TAG, "FROM: " + remoteMessage.getFrom());

        // التحقق من وجود بيانات الإشعار
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + body);

            // استدعاء دالة عرض الإشعار
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String message) {
        // عند الضغط على الإشعار، سيتم فتح التطبيق
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // تحديد ID واسم قناة الإشعارات
        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = "News and Updates"; // يمكنك تغيير هذا الاسم

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // إنشاء قناة الإشعارات (ضروري لأندرويد 8.0 فما فوق)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_HIGH); // 👈 استخدم أولوية عالية
            notificationManager.createNotificationChannel(channel);
        }

        // بناء الإشعار
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // تأكد من وجود أيقونة للتطبيق
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 👈 تحديد الأولوية
                .setContentIntent(pendingIntent);

        // عرض الإشعار
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        // يمكنك هنا إرسال الـ token الجديد إلى الخادم الخاص بك إذا احتجت ذلك
    }
}