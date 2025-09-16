package com.footstique.live;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.footstique.live.models.Channel; // استيراد Channel
import com.footstique.live.models.Match;
import com.footstique.live.models.MatchChannel;
import com.footstique.live.models.Team;
import com.footstique.live.utils.TimeUtils;
import com.footstique.live.utils.VideoPlayer; // استيراد VideoPlayer

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MatchDetailActivity extends AppCompatActivity {

    private Match match;
    private Button btnWatchMatch;

    // Views for the top card (Teams and Score)
    private ImageView ivCompetitionLogo, ivHomeTeamLogo, ivAwayTeamLogo;
    private TextView tvCompetitionName;
    private TextView tvMatchStatus;
    private Handler countdownHandler = new Handler();
    private Runnable countdownRunnable;
    private TextView tvHomeTeamName, tvAwayTeamName, tvKickoffTime;

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

        // --- **المنطق الجديد لزر مشاهدة المباراة** ---
        btnWatchMatch.setOnClickListener(v -> handleWatchButtonClick());
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
        btnWatchMatch = findViewById(R.id.btnWatchMatch); // أضف هذا السطر
        tvCompetitionName = findViewById(R.id.tvCompetitionName);
        ivHomeTeamLogo = findViewById(R.id.ivHomeTeamLogo);
        ivAwayTeamLogo = findViewById(R.id.ivAwayTeamLogo);
        tvMatchStatus = findViewById(R.id.tvMatchStatus);
        tvHomeTeamName = findViewById(R.id.tvHomeTeamName);
        tvAwayTeamName = findViewById(R.id.tvAwayTeamName);
        tvKickoffTime = findViewById(R.id.tvKickoffTime);

        // New detail views
        tvCompetitionValue = findViewById(R.id.tvCompetitionValue);
        tvStadiumValue = findViewById(R.id.tvStadiumValue);
        tvBroadcastingChannelValue = findViewById(R.id.tvBroadcastingChannelValue);
        tvMatchTimeValue = findViewById(R.id.tvMatchTimeValue);
        tvMatchDateValue = findViewById(R.id.tvMatchDateValue);
    }

    private void displayMatchData() {
        if (tvCompetitionName != null) {
            tvCompetitionName.setText(match.getCompetition().getName());
        } else {
            // Log an error to help with debugging
            android.util.Log.e("MatchDetailActivity", "tvCompetitionName is null!");
        }
        com.bumptech.glide.Glide.with(this)
                .load(match.getCompetition().getLogo())
                .into(ivCompetitionLogo);

        // Home team
        Team homeTeam = match.getHomeTeam();
        tvHomeTeamName.setText(homeTeam.getName());
        com.bumptech.glide.Glide.with(this).load(homeTeam.getLogo()).into(ivHomeTeamLogo);

        // Away team
        Team awayTeam = match.getAwayTeam();
        tvAwayTeamName.setText(awayTeam.getName());
        com.bumptech.glide.Glide.with(this).load(awayTeam.getLogo()).into(ivAwayTeamLogo);

        // --- 2. Details List ---
        tvCompetitionValue.setText(match.getCompetition().getName());

        // Stadium
        String venueName = (match.getVenue() != null && match.getVenue().getName() != null)
                ? match.getVenue().getName() : "غير متوفر";
        tvStadiumValue.setText(venueName);

        // Broadcasting Channels
        List<String> channelNames = new ArrayList<>();
        if (match.getStreamingChannels() != null) {
            for (MatchChannel channel : match.getStreamingChannels()) {
                if (channel.getName() != null && !channel.getName().isEmpty()) {
                    channelNames.add(channel.getName());
                }
            }
        }

        if (channelNames.isEmpty()) {
            btnWatchMatch.setVisibility(View.GONE); // إخفاء الزر
        } else {
            btnWatchMatch.setVisibility(View.VISIBLE); // إظهار الزر
        }
        String channelsString = TextUtils.join("، ", channelNames);
        tvBroadcastingChannelValue.setText(channelsString.isEmpty() ? "غير متوفر" : channelsString);

        // --- Conditional Date/Time Formatting ---
        String currentLanguage = com.yariksoffice.lingver.Lingver.getInstance().getLanguage();
        TimeZone preferredTimeZone = TimeUtils.getPreferredTimeZone(this);

        SimpleDateFormat timeFormat, timeOnlyFormat;

        if (currentLanguage.equals("ar")) {
            // --- ARABIC FORMATTING ---
            DateFormatSymbols arSymbols = new DateFormatSymbols();
            arSymbols.setAmPmStrings(new String[]{"ص", "م"});

            // Time formats (with Arabic AM/PM)
            timeFormat = new SimpleDateFormat("hh:mm a (z)", Locale.US);
            timeFormat.setDateFormatSymbols(arSymbols);
            timeOnlyFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            timeOnlyFormat.setDateFormatSymbols(arSymbols);

            // --- WORKAROUND FOR DATE ---
            // 1. Format Day Name in Arabic
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("ar"));
            dayFormat.setTimeZone(preferredTimeZone);
            String dayName = dayFormat.format(match.getKickoffTime());

            // 2. Format Date (numbers) in English
            SimpleDateFormat dateNumberFormat = new SimpleDateFormat(" (dd-MM-yyyy)", Locale.US);
            dateNumberFormat.setTimeZone(preferredTimeZone);
            String dateWithLatinNumbers = dateNumberFormat.format(match.getKickoffTime());

            // 3. Combine and set the date string
            tvMatchDateValue.setText(dayName + dateWithLatinNumbers);

        } else {
            // --- DEFAULT (ENGLISH) FORMATTING ---
            timeFormat = new SimpleDateFormat("hh:mm a (z)", Locale.US);
            timeOnlyFormat = new SimpleDateFormat("hh:mm a", Locale.US);

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE (dd-MM-yyyy)", Locale.US);
            dateFormat.setTimeZone(preferredTimeZone);
            tvMatchDateValue.setText(dateFormat.format(match.getKickoffTime()));
        }

        // Apply TimeZone to time formats
        timeFormat.setTimeZone(preferredTimeZone);
        timeOnlyFormat.setTimeZone(preferredTimeZone);

        // Set Text for time fields
        tvMatchTimeValue.setText(timeFormat.format(match.getKickoffTime()));
        tvKickoffTime.setText(timeOnlyFormat.format(match.getKickoffTime()));
        // --- منطق حالة المباراة ---
        long now = System.currentTimeMillis();
        long matchTimeMillis = match.getKickoffTime().getTime();

        if (matchTimeMillis > now) {
            // المباراة في المستقبل
            long diff = matchTimeMillis - now;
            long days = diff / (24 * 60 * 60 * 1000);

            if (days == 0) {
                // اليوم
                tvKickoffTime.setVisibility(View.VISIBLE);
                startCountdown(matchTimeMillis);
            } else if (days == 1) {
                // غداً
                tvKickoffTime.setVisibility(View.VISIBLE);
                tvMatchStatus.setText("غدا");
            }
        } else {
            // المباراة في الماضي
            tvKickoffTime.setVisibility(View.GONE);
            tvMatchStatus.setText("انتهت");
        }

    }
    private void startCountdown(long matchTimeMillis) {
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long diff = matchTimeMillis - now;

                if (diff > 0) {
                    long hours = diff / (60 * 60 * 1000) % 24;
                    long minutes = diff / (60 * 1000) % 60;
                    long seconds = diff / 1000 % 60;

                    tvMatchStatus.setText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
                    countdownHandler.postDelayed(this, 1000);
                } else {
                    tvMatchStatus.setText("مباشر");
                }
            }
        };
        countdownHandler.post(countdownRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownHandler != null && countdownRunnable != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
        }
    }
    // --- **دالة جديدة للتعامل مع النقر على زر المشاهدة** ---
    private void handleWatchButtonClick() {
        List<MatchChannel> channels = match.getStreamingChannels();
        if (channels == null || channels.isEmpty()) {
            return; // لا تفعل شيئًا إذا لم تكن هناك قنوات
        }

        if (channels.size() == 1) {
            // قناة واحدة فقط، قم بالتشغيل مباشرة
            playVideo(channels.get(0));
        } else {
            // أكثر من قناة، أظهر نافذة للاختيار
            showChannelChooser(channels);
        }
    }

    // --- **دالة جديدة لتحويل MatchChannel إلى Channel وتشغيل الفيديو** ---
    private void playVideo(MatchChannel matchChannel) {
        // قم بإنشاء كائن Channel مباشرة باستخدام المُنشئ
        // مرر قيمًا فارغة أو افتراضية للمعلمات غير المتوفرة
        Channel channel = new Channel(
                matchChannel.getName(),
                "", // logo (فارغ)
                "", // category (فارغ)
                0,  // epgId (قيمة افتراضية)
                null, // stream (قيمة افتراضية)
                matchChannel.getStreams()
        );

        VideoPlayer.playChannel(this, channel);
    }
    // --- **دالة جديدة لعرض نافذة اختيار القناة** ---
    private void showChannelChooser(List<MatchChannel> channels) {
        // استخراج أسماء القنوات
        String[] channelNames = new String[channels.size()];
        for (int i = 0; i < channels.size(); i++) {
            channelNames[i] = channels.get(i).getName();
        }

        // إنشاء وعرض AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_channel_title)); // "اختر قناة"
        builder.setItems(channelNames, (dialog, which) -> {
            // عند اختيار قناة، قم بتشغيل الفيديو
            MatchChannel selectedChannel = channels.get(which);
            playVideo(selectedChannel);
        });
        builder.show();
    }
}