package com.footstique.live;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PolicyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
    }
    // --- Language Configuration ---
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateBaseContextLocale(newBase));
    }

    private Context updateBaseContextLocale(Context context) {
        // Fetch the stored language code, default to device language if not found
        SharedPreferences languagePrefs = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE);
        String language = languagePrefs.getString("language", java.util.Locale.getDefault().getLanguage());

        java.util.Locale locale = new java.util.Locale(language);
        java.util.Locale.setDefault(locale);

        android.content.res.Configuration config = new android.content.res.Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
