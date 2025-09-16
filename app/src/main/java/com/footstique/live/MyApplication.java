package com.footstique.live;

import android.app.Application;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yariksoffice.lingver.Lingver;
import java.util.Locale;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // قم بتهيئة المكتبة هنا
        Lingver.init(this, "ar"); // "ar" هي اللغة العربية الافتراضية


        FirebaseMessaging.getInstance().subscribeToTopic("all_users");

    }
}