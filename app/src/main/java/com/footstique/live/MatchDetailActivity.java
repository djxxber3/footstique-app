package com.footstique.live;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.footstique.live.adapters.MatchChannelAdapter;
import com.footstique.live.models.Match;
import com.footstique.live.models.MatchChannel;
import com.footstique.live.models.Team;

import java.text.SimpleDateFormat;
import java.util.Locale;
import com.footstique.live.utils.TimeUtils;

public class MatchDetailActivity extends AppCompatActivity {

    private Match match;
    private ImageView ivCompetitionLogo, ivHomeTeamLogo, ivAwayTeamLogo;
    private TextView tvCompetitionName, tvMatchDate, tvMatchStatus;
    private TextView tvHomeTeamName, tvAwayTeamName, tvHomeTeamGoals, tvAwayTeamGoals;
    private TextView tvVenue;
    private RecyclerView recyclerViewChannels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);


        // Initialize views
        ivCompetitionLogo = findViewById(R.id.ivCompetitionLogo);
        tvCompetitionName = findViewById(R.id.tvCompetitionName);
        tvMatchDate = findViewById(R.id.tvMatchDate);
        tvMatchStatus = findViewById(R.id.tvMatchStatus);
        ivHomeTeamLogo = findViewById(R.id.ivHomeTeamLogo);
        ivAwayTeamLogo = findViewById(R.id.ivAwayTeamLogo);
        tvHomeTeamName = findViewById(R.id.tvHomeTeamName);
        tvAwayTeamName = findViewById(R.id.tvAwayTeamName);
        tvHomeTeamGoals = findViewById(R.id.tvHomeTeamGoals);
        tvAwayTeamGoals = findViewById(R.id.tvAwayTeamGoals);
        tvVenue = findViewById(R.id.tvVenue);
    recyclerViewChannels = findViewById(R.id.recyclerViewStreams);

        // Get match from intent
        match = (Match) getIntent().getSerializableExtra("match");
        if (match == null) {
            finish();
            return;
        }

        // Display match data
        displayMatchData();
    }

    private void displayMatchData() {
        // Competition info
        tvCompetitionName.setText(match.getCompetition().getName());
        com.bumptech.glide.Glide.with(this)
            .load(match.getCompetition().getLogo())
            .placeholder(R.color.fs_dark_grey_secondary)
            .error(R.color.fs_dark_grey_secondary)
            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
            .into(ivCompetitionLogo);

        // Match date and status
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy h:mm a", Locale.getDefault());
    dateFormat.setTimeZone(TimeUtils.getPreferredTimeZone(this));
        tvMatchDate.setText(dateFormat.format(match.getKickoffTime()));
        tvMatchStatus.setText(match.getStatusText());

        // Home team
        Team homeTeam = match.getHomeTeam();
        tvHomeTeamName.setText(homeTeam.getName());
        com.bumptech.glide.Glide.with(this)
            .load(homeTeam.getLogo())
            .placeholder(R.color.fs_dark_grey_secondary)
            .error(R.color.fs_dark_grey_secondary)
            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
            .into(ivHomeTeamLogo);
        if (homeTeam.getGoals() != null) {
            tvHomeTeamGoals.setText(String.valueOf(homeTeam.getGoals()));
        } else {
            tvHomeTeamGoals.setText("-");
        }

        // Away team
        Team awayTeam = match.getAwayTeam();
        tvAwayTeamName.setText(awayTeam.getName());
        com.bumptech.glide.Glide.with(this)
            .load(awayTeam.getLogo())
            .placeholder(R.color.fs_dark_grey_secondary)
            .error(R.color.fs_dark_grey_secondary)
            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
            .into(ivAwayTeamLogo);
        if (awayTeam.getGoals() != null) {
            tvAwayTeamGoals.setText(String.valueOf(awayTeam.getGoals()));
        } else {
            tvAwayTeamGoals.setText("-");
        }

        // Venue
        String venueName = match.getVenue() != null && match.getVenue().getName() != null 
                ? match.getVenue().getName() : "Not available";
        tvVenue.setText(venueName);

    // Channels list
    recyclerViewChannels.setLayoutManager(new LinearLayoutManager(this));
    java.util.List<MatchChannel> channels = match.getStreamingChannels();
    MatchChannelAdapter adapter = new MatchChannelAdapter(this, channels);
    recyclerViewChannels.setAdapter(adapter);
    }

}
