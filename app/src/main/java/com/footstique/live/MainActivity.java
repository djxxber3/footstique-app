package com.footstique.live;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.footstique.live.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false); // لمنع التمرير بالسحب

        // ربط الضغط على الأيقونات بالـ ViewPager
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_matches) {
                    viewPager.setCurrentItem(0, false);
                    return true;
                } else if (itemId == R.id.nav_channels) {
                    viewPager.setCurrentItem(1, false);
                    return true;
                } else if (itemId == R.id.nav_more) {
                    viewPager.setCurrentItem(2, false);
                    return true;
                }
                return false;
            }
        });

        // ربط تمرير الـ ViewPager بتحديد الأيقونة
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
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