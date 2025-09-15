package com.footstique.live;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.footstique.live.models.ChannelCategory;
import com.footstique.live.models.Config;
import com.footstique.live.models.Match;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SplashActivity extends AppCompatActivity {

    private String serverUrl;
    private List<Match> matches = new ArrayList<>();
    private List<ChannelCategory> categories = new ArrayList<>();

    private static final String CONFIG_URL = "https://footstique.app/data.json";
    private static final String WEBSITE_URL = "https://footstique.app";

    // متغير لتتبع عدد العمليات المكتملة
    private final AtomicInteger dataLoadCounter = new AtomicInteger(0);
    private boolean navigationStarted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Apply the saved theme
        SharedPreferences sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_splash);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_smoothly);
        progressBar.startAnimation(rotation);

        loadConfig();
    }

    private void loadConfig() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String configResponse = makeHttpRequest(CONFIG_URL);
                JSONObject configJson = new JSONObject(configResponse);
                Config config = Config.fromJson(configJson);
                runOnUiThread(() -> {
                    if (config.isActive()) {
                        serverUrl = config.getServerUrl();
                        loadData();
                    } else {
                        showUpdateDialog();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading config: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(this::loadConfig, 3000);
                });
            }
        });
        executor.shutdown();
    }

    private void loadData() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Load matches
        executor.execute(() -> {
            try {
                String currentDate = java.time.LocalDate.now().toString();
                String matchesUrl = serverUrl + "/api/client/matches/" + currentDate;
                String matchesResponse = makeHttpRequest(matchesUrl);
                JSONObject matchesJson = new JSONObject(matchesResponse);
                if (matchesJson.getBoolean("success")) {
                    JSONArray matchesArray = matchesJson.getJSONObject("data").getJSONArray("matches");
                    for (int i = 0; i < matchesArray.length(); i++) {
                        matches.add(Match.fromJson(matchesArray.getJSONObject(i)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // عند اكتمال تحميل المباريات، نزود العداد ونتحقق
                checkAndNavigate();
            }
        });

        // Load channels
        executor.execute(() -> {
            try {
                String channelsUrl = serverUrl + "/api/client/channels";
                String channelsResponse = makeHttpRequest(channelsUrl);
                JSONObject channelsJson = new JSONObject(channelsResponse);
                if (channelsJson.getBoolean("success")) {
                    JSONArray categoriesArray = channelsJson.getJSONObject("data").getJSONArray("categories");
                    for (int i = 0; i < categoriesArray.length(); i++) {
                        categories.add(ChannelCategory.fromJson(categoriesArray.getJSONObject(i)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // عند اكتمال تحميل القنوات، نزود العداد ونتحقق
                checkAndNavigate();
            }
        });

        executor.shutdown();
    }

    // دالة جديدة للتحقق من اكتمال العمليتين والانتقال
    private synchronized void checkAndNavigate() {
        // نزيد العداد بواحد. إذا وصل إلى 2 (اكتمال العمليتين) ولم يتم الانتقال من قبل، ننتقل
        if (dataLoadCounter.incrementAndGet() == 2 && !navigationStarted) {
            navigationStarted = true; // نمنع تكرار الانتقال
            runOnUiThread(this::navigateToMainActivity);
        }
    }


    private void navigateToMainActivity() {
        AppData.getInstance().setMatches(matches);
        AppData.getInstance().setCategories(categories);
        AppData.getInstance().setServerUrl(serverUrl);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 500);
    }

    private void showUpdateDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.update_required)
                .setMessage(R.string.update_message)
                .setPositiveButton(R.string.visit_website, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE_URL));
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private String makeHttpRequest(String urlString) throws IOException, JSONException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
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