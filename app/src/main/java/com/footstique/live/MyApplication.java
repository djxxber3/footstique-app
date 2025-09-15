package com.footstique.live;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java.util.Locale;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        SharedPreferences languagePrefs = base.getSharedPreferences("language_prefs", Context.MODE_PRIVATE);
        // استخدم "ar" كلغة افتراضية إذا لم يتم العثور على لغة محفوظة
        String languageCode = languagePrefs.getString("language", "ar");

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        Context newContext = base.createConfigurationContext(config);
        super.attachBaseContext(newContext);
    }
}