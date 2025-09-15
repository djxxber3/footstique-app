package com.footstique.live;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MoreActivity extends AppCompatActivity {
    private Switch switchTheme;
    private boolean isDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        // مفتاح الوضع الداكن/الفاتح
        switchTheme = findViewById(R.id.switch_theme);
        switchTheme.setChecked(isDarkMode);
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isDarkMode = isChecked;
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // تيليجرام
        LinearLayout telegramLayout = findViewById(R.id.telegram_layout);
        telegramLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/footstique"));
            startActivity(intent);
        });

        // الموقع الإلكتروني
        LinearLayout websiteLayout = findViewById(R.id.website_layout);
        websiteLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://footstique.com"));
            startActivity(intent);
        });

        // فيسبوك
        LinearLayout facebookLayout = findViewById(R.id.facebook_layout);
        facebookLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/footstique"));
            startActivity(intent);
        });

        // حول التطبيق
        LinearLayout aboutLayout = findViewById(R.id.about_layout);
        aboutLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });

        // سياسة الخصوصية
        LinearLayout policyLayout = findViewById(R.id.policy_layout);
        policyLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, PolicyActivity.class);
            startActivity(intent);
        });
    }
}
