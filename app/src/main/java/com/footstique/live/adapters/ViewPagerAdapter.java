package com.footstique.live.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.footstique.live.ChannelsFragment;
import com.footstique.live.MoreFragment;
import com.footstique.live.MatchesFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new MatchesFragment();
        if (position == 1) return new ChannelsFragment();
        return new MoreFragment();
    }

    @Override
    public int getItemCount() {
    return 3; // Matches, Channels, More
    }
}