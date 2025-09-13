package com.footstique.live;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.footstique.live.adapters.ViewPagerAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MaterialButtonToggleGroup bottomNavGroup;
    private MaterialButton btnMatches;
    private MaterialButton btnChannels;
    private MaterialButton btnMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavGroup = findViewById(R.id.bottom_nav_group);
        btnMatches = findViewById(R.id.btn_matches);
        btnChannels = findViewById(R.id.btn_channels);
        btnMore = findViewById(R.id.btn_more);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);

        // Link ToggleGroup with ViewPager2
        bottomNavGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btn_matches) {
                viewPager.setCurrentItem(0, true);
                btnMatches.setTextColor(getColor(R.color.bottom_nav_selected_text));
                btnChannels.setTextColor(getColor(R.color.fs_nav_unselected));
                btnMore.setTextColor(getColor(R.color.fs_nav_unselected));
                btnMatches.setIconTintResource(R.color.bottom_nav_selected_icon);
                btnChannels.setIconTintResource(R.color.bottom_nav_channels_icon);
                btnMore.setIconTintResource(R.color.bottom_nav_more_icon);
                btnMatches.setText(getString(R.string.matches));
                btnChannels.setText("");
                btnMore.setText("");
            } else if (checkedId == R.id.btn_channels) {
                viewPager.setCurrentItem(1, true);
                btnMatches.setTextColor(getColor(R.color.fs_nav_unselected));
                btnChannels.setTextColor(getColor(R.color.bottom_nav_selected_text_channels));
                btnMore.setTextColor(getColor(R.color.fs_nav_unselected));
                btnMatches.setIconTintResource(R.color.bottom_nav_selected_icon);
                btnChannels.setIconTintResource(R.color.bottom_nav_channels_icon);
                btnMore.setIconTintResource(R.color.bottom_nav_more_icon);
                btnMatches.setText("");
                btnChannels.setText(getString(R.string.channels));
                btnMore.setText("");
            } else if (checkedId == R.id.btn_more) {
                viewPager.setCurrentItem(2, true);
                btnMatches.setTextColor(getColor(R.color.fs_nav_unselected));
                btnChannels.setTextColor(getColor(R.color.fs_nav_unselected));
                btnMore.setTextColor(getColor(R.color.bottom_nav_selected_text_more));
                btnMatches.setIconTintResource(R.color.bottom_nav_selected_icon);
                btnChannels.setIconTintResource(R.color.bottom_nav_channels_icon);
                btnMore.setIconTintResource(R.color.bottom_nav_more_icon);
                btnMatches.setText("");
                btnChannels.setText("");
                btnMore.setText(getString(R.string.more));
            }
        });

        // Link ViewPager2 with BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int buttonId = position == 0 ? R.id.btn_matches : position == 1 ? R.id.btn_channels : R.id.btn_more;
                if (bottomNavGroup.getCheckedButtonId() != buttonId) {
                    bottomNavGroup.check(buttonId);
                }
                // Sync text color when swiping
                if (position == 0) {
                    btnMatches.setTextColor(getColor(R.color.bottom_nav_selected_text));
                    btnChannels.setTextColor(getColor(R.color.fs_nav_unselected));
                    btnMore.setTextColor(getColor(R.color.fs_nav_unselected));
                    btnMatches.setIconTintResource(R.color.bottom_nav_selected_icon);
                    btnChannels.setIconTintResource(R.color.bottom_nav_channels_icon);
                    btnMore.setIconTintResource(R.color.bottom_nav_more_icon);
                    btnMatches.setText(getString(R.string.matches));
                    btnChannels.setText("");
                    btnMore.setText("");
                } else if (position == 1) {
                    btnMatches.setTextColor(getColor(R.color.fs_nav_unselected));
                    btnChannels.setTextColor(getColor(R.color.bottom_nav_selected_text_channels));
                    btnMore.setTextColor(getColor(R.color.fs_nav_unselected));
                    btnMatches.setIconTintResource(R.color.bottom_nav_selected_icon);
                    btnChannels.setIconTintResource(R.color.bottom_nav_channels_icon);
                    btnMore.setIconTintResource(R.color.bottom_nav_more_icon);
                    btnMatches.setText("");
                    btnChannels.setText(getString(R.string.channels));
                    btnMore.setText("");
                } else if (position == 2) {
                    btnMatches.setTextColor(getColor(R.color.fs_nav_unselected));
                    btnChannels.setTextColor(getColor(R.color.fs_nav_unselected));
                    btnMore.setTextColor(getColor(R.color.bottom_nav_selected_text_more));
                    btnMatches.setIconTintResource(R.color.bottom_nav_selected_icon);
                    btnChannels.setIconTintResource(R.color.bottom_nav_channels_icon);
                    btnMore.setIconTintResource(R.color.bottom_nav_more_icon);
                    btnMatches.setText("");
                    btnChannels.setText("");
                    btnMore.setText(getString(R.string.more));
                }
            }
        });

        // Default selection
        bottomNavGroup.check(R.id.btn_matches);
        btnMatches.setTextColor(getColor(R.color.bottom_nav_selected_text));
        btnChannels.setTextColor(getColor(R.color.fs_nav_unselected));
        btnMore.setTextColor(getColor(R.color.fs_nav_unselected));
        btnMatches.setIconTintResource(R.color.bottom_nav_selected_icon);
        btnChannels.setIconTintResource(R.color.bottom_nav_channels_icon);
        btnMore.setIconTintResource(R.color.bottom_nav_more_icon);
        btnMatches.setText(getString(R.string.matches));
        btnChannels.setText("");
        btnMore.setText("");
    }}
