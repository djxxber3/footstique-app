package com.footstique.live;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.footstique.live.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        viewPager.setUserInputEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_matches) {
                viewPager.setCurrentItem(0, false);
            } else if (itemId == R.id.nav_channels) {
                viewPager.setCurrentItem(1, false);
            } else if (itemId == R.id.nav_more) {
                viewPager.setCurrentItem(2, false);
            }
            return true;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_matches);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_channels);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_more);
                        break;
                }
            }
        });
    }
}