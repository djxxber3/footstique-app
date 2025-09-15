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

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class MoreFragment extends Fragment {

    // Views from original code
    private LinearLayout themeLayout;
    private TextView selectedThemeTextView;
    private LinearLayout policyLayout, aboutLayout, websiteLayout, facebookLayout, telegramLayout;

    // New views for language selection
    private LinearLayout languageLayout;
    private TextView selectedLanguageTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_more, container, false);

        // --- Initialize Original Views ---
        themeLayout = view.findViewById(R.id.theme_layout);
        selectedThemeTextView = view.findViewById(R.id.selected_theme_textview);
        policyLayout = view.findViewById(R.id.policy_layout);
        aboutLayout = view.findViewById(R.id.about_layout);
        websiteLayout = view.findViewById(R.id.website_layout);
        facebookLayout = view.findViewById(R.id.facebook_layout);
        telegramLayout = view.findViewById(R.id.telegram_layout);

        SharedPreferences themePrefs = requireActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        int currentNightMode = themePrefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        updateThemeTextView(currentNightMode);

        themeLayout.setOnClickListener(v -> showThemeDialog());
        policyLayout.setOnClickListener(v -> startActivity(new Intent(requireContext(), PolicyActivity.class)));
        aboutLayout.setOnClickListener(v -> startActivity(new Intent(requireContext(), AboutActivity.class)));
        websiteLayout.setOnClickListener(v -> openUrl(AppData.WEBSITE_URL));
        facebookLayout.setOnClickListener(v -> openUrl(AppData.FACEBOOK_URL));
        telegramLayout.setOnClickListener(v -> openUrl(AppData.TELEGRAM_URL));


        // --- New Code for Language Selection ---
        languageLayout = view.findViewById(R.id.language_layout);
        selectedLanguageTextView = view.findViewById(R.id.selected_language_textview);

        SharedPreferences languagePrefs = requireActivity().getSharedPreferences("language_prefs", Context.MODE_PRIVATE);
        String currentLang = languagePrefs.getString("language", "ar"); // Default language is Arabic
        setLanguageText(currentLang);

        languageLayout.setOnClickListener(v -> showLanguageDialog());
        // --- End of New Code ---

        return view;
    }

    // --- New Methods for Language Selection ---
    private void setLanguageText(String langCode) {
        switch (langCode) {
            case "en":
                selectedLanguageTextView.setText(getString(R.string.english));
                break;
            case "fr":
                selectedLanguageTextView.setText(getString(R.string.french));
                break;
            default: // "ar"
                selectedLanguageTextView.setText(getString(R.string.arabic));
                break;
        }
    }

    private void showLanguageDialog() {
        final String[] languages = {"العربية", "Français", "English"};
        final String[] langCodes = {"ar", "fr", "en"};

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.language))
                .setItems(languages, (dialog, which) -> {
                    setLocale(langCodes[which]);
                })
                .show();
    }

    private void setLocale(String langCode) {
        SharedPreferences languagePrefs = requireActivity().getSharedPreferences("language_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = languagePrefs.edit();
        editor.putString("language", langCode);
        editor.apply();

        // Recreate the activity to apply the new language
        requireActivity().recreate();
    }
    // --- End of New Language Methods ---


    // --- Original Methods (with a bug fix) ---
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

    /**
     * I have fixed a bug in this method. It was using setText(R.string.resource)
     * which causes a crash. The corrected version uses setText(getString(R.string.resource)).
     */
    private void updateThemeTextView(int nightMode) {
        if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            selectedThemeTextView.setText(getString(R.string.light));
        } else if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            selectedThemeTextView.setText(getString(R.string.dark));
        } else {
            selectedThemeTextView.setText(getString(R.string.system_default));
        }
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}