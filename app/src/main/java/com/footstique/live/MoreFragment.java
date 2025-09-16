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


        // --- Setup Language ---
        setupLanguage();

        // --- Setup Click Listeners ---
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {


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


    private void setupLanguage() {

        SharedPreferences languagePrefs = requireActivity().getSharedPreferences("language_prefs", Context.MODE_PRIVATE);
        // Set default language to Arabic ("ar") if not found
        String currentLang = languagePrefs.getString("language", "ar");
        updateLanguageTextView(currentLang);
    }

    private void setupClickListeners() {
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
        requireActivity().recreate();

    }


    // --- Utility Method ---
    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}