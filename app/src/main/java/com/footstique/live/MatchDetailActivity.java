package com.footstique.live;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.footstique.live.models.Match;
import com.footstique.live.models.MatchChannel;
import com.footstique.live.models.Team;
import com.footstique.live.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MatchDetailActivity extends AppCompatActivity {

    private Match match;

    // Views for the top card (Teams and Score)
    private ImageView ivCompetitionLogo, ivHomeTeamLogo, ivAwayTeamLogo;
    private TextView tvCompetitionName;
    private TextView tvHomeTeamName, tvAwayTeamName, tvHomeTeamGoals, tvAwayTeamGoals, tvKickoffTime;

    // Views for the details list
    private TextView tvCompetitionValue, tvStadiumValue, tvBroadcastingChannelValue, tvMatchTimeValue, tvMatchDateValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        // ---  الكود الجديد لإضافة شريط الأدوات وسهم الرجوع ---
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(""); // لإخفاء العنوان النصي
        }
        // --- نهاية الكود الجديد ---

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

    // --- دالة جديدة للتعامل مع النقر على سهم الرجوع ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // تنفيذ الرجوع للخلف
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // --- نهاية الدالة الجديدة ---

    private void initializeViews() {
        // Top card views
        ivCompetitionLogo = findViewById(R.id.ivCompetitionLogo);
        tvCompetitionName = findViewById(R.id.tvCompetitionName);
        ivHomeTeamLogo = findViewById(R.id.ivHomeTeamLogo);
        ivAwayTeamLogo = findViewById(R.id.ivAwayTeamLogo);
        tvHomeTeamName = findViewById(R.id.tvHomeTeamName);
        tvAwayTeamName = findViewById(R.id.tvAwayTeamName);
        tvHomeTeamGoals = findViewById(R.id.tvHomeTeamGoals);
        tvAwayTeamGoals = findViewById(R.id.tvAwayTeamGoals);
        tvKickoffTime = findViewById(R.id.tvKickoffTime);

        // New detail views
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

        // --- 2. Details List ---

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
        String channelsString = TextUtils.join("، ", channelNames);
        tvBroadcastingChannelValue.setText(channelsString.isEmpty() ? "غير متوفر" : channelsString);

        // Match Time
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a (z)", new Locale("ar"));
        timeFormat.setTimeZone(TimeUtils.getPreferredTimeZone(this));
        tvMatchTimeValue.setText(timeFormat.format(match.getKickoffTime()));

        SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("hh:mm a", new Locale("ar"));
        timeOnlyFormat.setTimeZone(TimeUtils.getPreferredTimeZone(this));
        tvKickoffTime.setText(timeOnlyFormat.format(match.getKickoffTime()));

        // Match Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE (dd-MM-yyyy)", new Locale("ar"));
        dateFormat.setTimeZone(TimeUtils.getPreferredTimeZone(this));
        tvMatchDateValue.setText(dateFormat.format(match.getKickoffTime()));
    }
}