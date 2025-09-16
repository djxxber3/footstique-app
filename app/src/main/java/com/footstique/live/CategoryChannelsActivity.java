package com.footstique.live;

import android.os.Bundle;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.footstique.live.adapters.ChannelAdapter;
import com.footstique.live.models.Channel;
import com.footstique.live.models.ChannelCategory;
import com.footstique.live.utils.VideoPlayer; // استيراد الفئة الجديدة
import java.util.ArrayList;
import java.util.List;

public class CategoryChannelsActivity extends AppCompatActivity {

    private RecyclerView rvChannels;
    private ProgressBar progressBar;
    private ChannelAdapter channelAdapter;
    private List<Channel> channels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_channels);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvChannels = findViewById(R.id.rvChannels);
        progressBar = findViewById(R.id.progressBar);

        // Get category from intent
        ChannelCategory category = (ChannelCategory) getIntent().getSerializableExtra("category");
        if (category != null) {
            getSupportActionBar().setTitle(category.getName());
            channels = category.getChannels();
        } else {
            getSupportActionBar().setTitle("Channels");
            channels = new ArrayList<>();
        }

        // Setup RecyclerView
        rvChannels.setLayoutManager(new LinearLayoutManager(this));
        // **هنا تم تعديل منطق تشغيل الفيديو**
        channelAdapter = new ChannelAdapter(this, channels, channel -> {
            VideoPlayer.playChannel(this, channel); // استدعاء الوظيفة الجديدة
        });
        rvChannels.setAdapter(channelAdapter);

        if (channels.isEmpty()) {
            // Optionally, show a message if there are no channels
        }
    }
}