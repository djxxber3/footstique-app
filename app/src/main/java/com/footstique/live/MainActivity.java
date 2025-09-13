package com.footstique.live;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.footstique.live.adapters.ViewPagerAdapter;

import io.ak1.BubbleTabBar;
import io.ak1.OnBubbleClickListener;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BubbleTabBar bubbleTabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bubbleTabBar = findViewById(R.id.bubbleTabBar);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false); // لمنع التمرير بالسحب

        // ربط الفوتر بالـ ViewPager
        bubbleTabBar.addBubbleListener(new OnBubbleClickListener() {
            @Override
            public void onBubbleClick(int id) {
                if (id == R.id.nav_matches) {
                    viewPager.setCurrentItem(0, false);
                } else if (id == R.id.nav_channels) {
                    viewPager.setCurrentItem(1, false);
                } else if (id == R.id.nav_more) {
                    viewPager.setCurrentItem(2, false);
                }
            }
        });

        // ربط الـ ViewPager بالفوتر (للتحديث عند التغيير)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bubbleTabBar.setSelected(position, false);
            }
        });
    }
}