package com.footstique.live;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yariksoffice.lingver.Lingver;
import java.util.Locale;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // قم بتهيئة المكتبة هنا
        Lingver.init(this, "ar"); // "ar" هي اللغة العربية الافتراضية

        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed to all_users topic";
                    if (!task.isSuccessful()) {
                        msg = "Subscription to all_users topic failed";
                    }
                    Log.d(TAG, msg);
                });
    }
}