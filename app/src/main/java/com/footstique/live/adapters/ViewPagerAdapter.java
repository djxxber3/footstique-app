package com.footstique.live.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.footstique.live.ChannelsFragment;
import com.footstique.live.MatchesFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new ChannelsFragment();
        }
        return new MatchesFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // We have two fragments: Matches and Channels
    }
}