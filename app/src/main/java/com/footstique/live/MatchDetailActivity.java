package com.footstique.live;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.footstique.live.utils.TimeUtils;
import java.util.stream.Collectors;


public class MatchDetailActivity extends AppCompatActivity {

    private Match match;

    // Views for the top card (Teams and Score)
    private ImageView ivCompetitionLogo, ivHomeTeamLogo, ivAwayTeamLogo;
    private TextView tvCompetitionName, tvMatchStatus;
    private TextView tvHomeTeamName, tvAwayTeamName, tvHomeTeamGoals, tvAwayTeamGoals, tvKickoffTime;

    // Views for the details list (as per the new UI)
    private TextView tvCompetitionValue, tvStadiumValue, tvBroadcastingChannelValue, tvMatchTimeValue, tvMatchDateValue;

    // The RecyclerView for streaming channels is now replaced by a simple TextView
    // but we can keep it if we want to list channels at the bottom as well.
    // For this example, we will populate the TextView instead.
    // private RecyclerView recyclerViewChannels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        // Get match from intent
        match = (Match) getIntent().getSerializableExtra("match");
        if (match == null) {
            finish();
            return;
        }

        // Initialize all views
        initializeViews();

        // Display match data using the new structure
        displayMatchData();
    }

    private void initializeViews() {
        // Top card views
        ivCompetitionLogo = findViewById(R.id.ivCompetitionLogo);
        tvCompetitionName = findViewById(R.id.tvCompetitionName);
        tvMatchStatus = findViewById(R.id.tvMatchStatus);
        ivHomeTeamLogo = findViewById(R.id.ivHomeTeamLogo);
        ivAwayTeamLogo = findViewById(R.id.ivAwayTeamLogo);
        tvHomeTeamName = findViewById(R.id.tvHomeTeamName);
        tvAwayTeamName = findViewById(R.id.tvAwayTeamName);
        tvHomeTeamGoals = findViewById(R.id.tvHomeTeamGoals);
        tvAwayTeamGoals = findViewById(R.id.tvAwayTeamGoals);
        tvKickoffTime = findViewById(R.id.tvKickoffTime); // TextView for score or time between logos

        // New detail views from the image
        // Make sure you have these IDs in your activity_match_detail.xml
        tvCompetitionValue = findViewById(R.id.tvCompetitionValue);
        tvStadiumValue = findViewById(R.id.tvStadiumValue);
        tvBroadcastingChannelValue = findViewById(R.id.tvBroadcastingChannelValue);
        tvMatchTimeValue = findViewById(R.id.tvMatchTimeValue);
        tvMatchDateValue = findViewById(R.id.tvMatchDateValue);
    }

    private void displayMatchData() {
        // --- 1. Top Card: Competition, Teams, Score/Time ---
        tvCompetitionName.setText(match.getCompetition().getName());
        com.bumptech.glide.Glide.with(this)
                .load(match.getCompetition().getLogo())
                .into(ivCompetitionLogo);

        tvMatchStatus.setText(match.getStatusText());

        // Home team
        Team homeTeam = match.getHomeTeam();
        tvHomeTeamName.setText(homeTeam.getName());
        com.bumptech.glide.Glide.with(this).load(homeTeam.getLogo()).into(ivHomeTeamLogo);
        tvHomeTeamGoals.setText(homeTeam.getGoals() != null ? String.valueOf(homeTeam.getGoals()) : "-");

        // Away team
        Team awayTeam = match.getAwayTeam();
        tvAwayTeamName.setText(awayTeam.getName());
        com.bumptech.glide.Glide.with(this).load(awayTeam.getLogo()).into(ivAwayTeamLogo);
        tvAwayTeamGoals.setText(awayTeam.getGoals() != null ? String.valueOf(awayTeam.getGoals()) : "-");

        // --- 2. Details List (New Implementation) ---

        // Competition Name
        tvCompetitionValue.setText(match.getCompetition().getName());

        // Stadium
        String venueName = (match.getVenue() != null && match.getVenue().getName() != null)
                ? match.getVenue().getName() : "غير متوفر";
        tvStadiumValue.setText(venueName);

        // Broadcasting Channels
        List<String> channelNames = new ArrayList<>();
        for(MatchChannel channel : match.getStreamingChannels()){
            if(channel.getName() != null && !channel.getName().isEmpty()){
                channelNames.add(channel.getName());
            }
        }
        String channelsString = TextUtils.join("، ", channelNames); // Join with an Arabic comma
        tvBroadcastingChannelValue.setText(channelsString.isEmpty() ? "غير متوفر" : channelsString);

        // Match Time
        // Format: 08:00 م (GMT+1)
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a (z)", new Locale("ar"));
        timeFormat.setTimeZone(TimeUtils.getPreferredTimeZone(this));
        tvMatchTimeValue.setText(timeFormat.format(match.getKickoffTime()));

        // This also sets the time in the middle of the top card
        SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("hh:mm a", new Locale("ar"));
        timeOnlyFormat.setTimeZone(TimeUtils.getPreferredTimeZone(this));
        tvKickoffTime.setText(timeOnlyFormat.format(match.getKickoffTime()));


        // Match Date
        // Format: الاثنين (15-09-2025)
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE (dd-MM-yyyy)", new Locale("ar"));
        dateFormat.setTimeZone(TimeUtils.getPreferredTimeZone(this));
        tvMatchDateValue.setText(dateFormat.format(match.getKickoffTime()));
    }
}