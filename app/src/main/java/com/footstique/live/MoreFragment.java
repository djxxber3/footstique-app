package com.footstique.live;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class MoreFragment extends Fragment {

    private Switch switchTheme;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_more, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);

        switchTheme = view.findViewById(R.id.switch_theme);
        boolean isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false);
        switchTheme.setChecked(isDarkMode);

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_dark_mode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });


        // تيليجرام
        LinearLayout telegramLayout = view.findViewById(R.id.telegram_layout);
        telegramLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/footstique"));
            startActivity(intent);
        });

        // الموقع الإلكتروني
        LinearLayout websiteLayout = view.findViewById(R.id.website_layout);
        websiteLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://footstique.app"));
            startActivity(intent);
        });

        // فيسبوك
        LinearLayout facebookLayout = view.findViewById(R.id.facebook_layout);
        facebookLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/footstique"));
            startActivity(intent);
        });

        // حول التطبيق
        LinearLayout aboutLayout = view.findViewById(R.id.about_layout);
        aboutLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });

        // سياسة الخصوصية
        LinearLayout policyLayout = view.findViewById(R.id.policy_layout);
        policyLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PolicyActivity.class);
            startActivity(intent);
        });

        return view;
    }
}