package com.footstique.live;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.yariksoffice.lingver.Lingver;

public class MoreFragment extends Fragment {

    // Views for Theme
    private LinearLayout themeLayout;
    private TextView selectedThemeTextView;

    // Views for Language
    private LinearLayout languageLayout;
    private TextView selectedLanguageTextView;

    // Other Views
    private LinearLayout policyLayout, aboutLayout, websiteLayout, facebookLayout, telegramLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_more, container, false);

        // --- Initialize Views ---
        initializeViews(view);

        // --- Setup Theme ---
        setupTheme();

        // --- Setup Language ---
        setupLanguage();

        // --- Setup Click Listeners ---
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        // Theme
        themeLayout = view.findViewById(R.id
                .theme_layout);
        selectedThemeTextView = view.findViewById(R.id.selected_theme_textview);

        // Language
        languageLayout = view.findViewById(R.id.language_layout);
        selectedLanguageTextView = view.findViewById(R.id.selected_language_textview);

        // Other
        policyLayout = view.findViewById(R.id.policy_layout);
        aboutLayout = view.findViewById(R.id.about_layout);
        websiteLayout = view.findViewById(R.id.website_layout);
        facebookLayout = view.findViewById(R.id.facebook_layout);
        telegramLayout = view.findViewById(R.id.telegram_layout);
    }

    private void setupTheme() {
        SharedPreferences themePrefs = requireActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        int currentNightMode = themePrefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        updateThemeTextView(currentNightMode);
    }

    private void setupLanguage() {

        SharedPreferences languagePrefs = requireActivity().getSharedPreferences("language_prefs", Context.MODE_PRIVATE);
        // Set default language to Arabic ("ar") if not found
        String currentLang = languagePrefs.getString("language", "ar");
        updateLanguageTextView(currentLang);
    }

    private void setupClickListeners() {
        themeLayout.setOnClickListener(v -> showThemeDialog());
        languageLayout.setOnClickListener(v -> showLanguageDialog());

        policyLayout.setOnClickListener(v -> startActivity(new Intent(requireContext(), PolicyActivity.class)));
        aboutLayout.setOnClickListener(v -> startActivity(new Intent(requireContext(), AboutActivity.class)));

        websiteLayout.setOnClickListener(v -> openUrl(AppData.WEBSITE_URL));
        facebookLayout.setOnClickListener(v -> openUrl(AppData.FACEBOOK_URL));
        telegramLayout.setOnClickListener(v -> openUrl(AppData.TELEGRAM_URL));
    }

    // --- Language Methods ---
    private void updateLanguageTextView(String langCode) {
        if ("en".equals(langCode)) {
            selectedLanguageTextView.setText(getString(R.string.english));
        } else if ("fr".equals(langCode)) {
            selectedLanguageTextView.setText(getString(R.string.french));
        } else {
            selectedLanguageTextView.setText(getString(R.string.arabic));
        }
    }

    private void showLanguageDialog() {
        final String[] languages = {getString(R.string.arabic), getString(R.string.french), getString(R.string.english)};
        final String[] langCodes = {"ar", "fr", "en"};

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.language))
                .setItems(languages, (dialog, which) -> {
                    setLocale(langCodes[which]);
                })
                .show();
    }

    private void setLocale(String langCode) {
        // 1. احفظ اللغة الجديدة
        SharedPreferences languagePrefs = requireActivity().getSharedPreferences("language_prefs", Context.MODE_PRIVATE);
        languagePrefs.edit().putString("language", langCode).apply();

        // 2. استخدم Lingver لتطبيق اللغة (إذا كنت تستخدم المكتبة)
        Lingver.getInstance().setLocale(requireContext(), langCode);

        // 3. أعد تشغيل التطبيق بالكامل بطريقة آمنة
        // هذا الكود سيغلق كل الشاشات ويفتح الشاشة الرئيسية من جديد
        Intent intent = new Intent(requireActivity(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

        // --- Theme Methods ---
    private void updateThemeTextView(int nightMode) {
        if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            selectedThemeTextView.setText(getString(R.string.light));
        } else if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            selectedThemeTextView.setText(getString(R.string.dark));
        } else {
            selectedThemeTextView.setText(getString(R.string.system_default));
        }
    }

    private void showThemeDialog() {
        final String[] themes = {getString(R.string.light), getString(R.string.dark), getString(R.string.system_default)};
        final int[] themeValues = {AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM};

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.appearance))
                .setItems(themes, (dialog, which) -> {
                    int selectedNightMode = themeValues[which];
                    AppCompatDelegate.setDefaultNightMode(selectedNightMode);

                    SharedPreferences themePrefs = requireActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = themePrefs.edit();
                    editor.putInt("night_mode", selectedNightMode);
                    editor.apply();

                    updateThemeTextView(selectedNightMode);
                })
                .show();
    }

    // --- Utility Method ---
    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}