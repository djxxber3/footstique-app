package com.footstique.live;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.footstique.live.adapters.ChannelAdapter;
import com.footstique.live.models.Channel;
import com.footstique.live.models.ChannelCategory;
import com.footstique.live.models.Stream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList; // still used for category fallback
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
        // **هنا تم إضافة منطق تشغيل الفيديو**
        channelAdapter = new ChannelAdapter(this, channels, channel -> {
            Intent intent = new Intent();
            intent.setClassName(this, "com.footstique.player.PlayerActivity");
                JSONArray arr = new JSONArray();
            for (Stream stream : channel.getStreams()) {
                    if (stream == null || stream.getUrl() == null || stream.getUrl().trim().isEmpty()) continue;
                    try {
                        JSONObject o = new JSONObject();
                        o.put("url", stream.getUrl());
                        String label = firstNonEmpty(stream.getLabel(), stream.getQuality(), "Stream");
                        o.put("label", label);
                        putIfNotEmpty(o, "userAgent", stream.getUserAgent());
                        putIfNotEmpty(o, "referer", stream.getReferer());
                        putIfNotEmpty(o, "cookie", stream.getCookie());
                        putIfNotEmpty(o, "origin", stream.getOrigin());
                        arr.put(o);
                    } catch (JSONException ignored) {}
            }
                String json = arr.toString();
                intent.putExtra("EXTRA_STREAMS_JSON", json);
            startActivity(intent);
        });
        rvChannels.setAdapter(channelAdapter);

        if (channels.isEmpty()) {
            // Optionally, show a message if there are no channels
        }
    }

        private static void putIfNotEmpty(JSONObject o, String k, String v) throws JSONException {
            if (v != null && !v.trim().isEmpty()) o.put(k, v.trim());
        }

        private static String firstNonEmpty(String... vals) {
            if (vals == null) return "";
            for (String v : vals) if (v != null && !v.trim().isEmpty()) return v.trim();
            return "";
        }
}