package com.footstique.player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlayerActivity extends AppCompatActivity {

    public static final String EXTRA_STREAMS = "streams"; // Serializable: ArrayList<Map<String,Object>>
    private static final String TAG = "PlayerActivity";
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36";

    private ExoPlayer exoPlayer;
    private androidx.media3.ui.PlayerView playerView;
    private LinearLayout qualityBar;

    private final List<Map<String, Object>> streams = new ArrayList<>();
    private final Map<String, List<Map<String, Object>>> byQuality = new LinkedHashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.player_view);
        qualityBar = findViewById(R.id.quality_bar);

        readStreamsFromIntent();
        groupByQuality();
        setupQualityButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
        Map<String, Object> first = getFirstStream();
        if (first != null) playStream(first);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer() {
        if (exoPlayer == null) {
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this)
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);

            DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);

            exoPlayer = new ExoPlayer.Builder(this, renderersFactory)
                    .setTrackSelector(trackSelector)
                    .build();

            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(@NonNull PlaybackException error) {
                    Log.e(TAG, "Player Error: " + error.getErrorCodeName(), error);
                    Toast.makeText(PlayerActivity.this, "Playback Error: " + error.getErrorCodeName(), Toast.LENGTH_LONG).show();
                }
            });

            playerView.setPlayer(exoPlayer);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
    @OptIn(markerClass = UnstableApi.class)
    private void playStream(Map<String, Object> stream) {
        if (exoPlayer == null || stream == null) return;

        Object urlObj = stream.get("url");
        if (!(urlObj instanceof String) || ((String) urlObj).isEmpty()) {
            Log.e(TAG, "Stream URL is null or empty.");
            return;
        }
        String url = (String) urlObj;

        // اجمع الـ headers
        Map<String, String> headers = new HashMap<>();
        Object headersObj = stream.get("headers");
        if (headersObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> h = (Map<String, String>) headersObj;
            headers.putAll(h);
        }
        if (stream.containsKey("referer")) headers.put("Referer", (String) stream.get("referer"));
        if (stream.containsKey("origin")) headers.put("Origin", (String) stream.get("origin"));
        if (stream.containsKey("cookie")) headers.put("Cookie", (String) stream.get("cookie"));

        // Factory مخصص يطبق headers على كل Connection
        DataSource.Factory customDataSourceFactory = new DataSource.Factory() {
            @NonNull
            @Override
            public DataSource createDataSource() {
                DefaultHttpDataSource dataSource = new DefaultHttpDataSource
                        .Factory()
                        .setUserAgent((String) stream.getOrDefault("userAgent", DEFAULT_USER_AGENT))
                        .setAllowCrossProtocolRedirects(true)
                        .createDataSource();

                // تطبيق الـ headers
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    dataSource.setRequestProperty(entry.getKey(), entry.getValue());
                }

                return dataSource;
            }
        };

        // إنشاء MediaItem
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(Uri.parse(url))
                .build();

        // تحديد نوع المحتوى
        int type = Util.inferContentType(Uri.parse(url));
        if (type == C.CONTENT_TYPE_HLS) {
            HlsMediaSource hls = new HlsMediaSource.Factory(customDataSourceFactory)
                    .createMediaSource(mediaItem);
            exoPlayer.setMediaSource(hls);
        } else {
            ProgressiveMediaSource prog = new ProgressiveMediaSource.Factory(customDataSourceFactory)
                    .createMediaSource(mediaItem);
            exoPlayer.setMediaSource(prog);
        }

        exoPlayer.prepare();
        exoPlayer.play();
    }

    private void groupByQuality() {
        byQuality.clear();
        for (Map<String, Object> s : streams) {
            Object qObj = s.get("quality");
            String q = qObj instanceof String ? (String) qObj : "AUTO";
            List<Map<String, Object>> list = byQuality.computeIfAbsent(q, k -> new ArrayList<>());
            list.add(s);
        }
    }

    private void setupQualityButtons() {
        qualityBar.removeAllViews();
        for (Map.Entry<String, List<Map<String, Object>>> entry : byQuality.entrySet()) {
            String quality = entry.getKey();
            List<Map<String, Object>> links = entry.getValue();

            Button btn = new Button(this);
            btn.setAllCaps(false);
            btn.setText(quality);
            btn.setOnClickListener(v -> showLinksPopup(btn, quality, links));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8,8,8,8);
            btn.setLayoutParams(lp);
            qualityBar.addView(btn);
        }
    }

    private void showLinksPopup(View anchor, String quality, List<Map<String, Object>> links) {
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < links.size(); i++) {
            Map<String, Object> s = links.get(i);
            String label = Objects.requireNonNull(s.getOrDefault("label", "Link " + (i + 1))).toString();
            labels.add(label);
        }

        ListPopupWindow popup = new ListPopupWindow(this);
        popup.setAnchorView(anchor);
        popup.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels));
        popup.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> selected = links.get(position);
            playStream(selected);
            popup.dismiss();
        });
        popup.show();
    }

    private Map<String, Object> getFirstStream() {
        if (!streams.isEmpty()) return streams.get(0);
        return null;
    }

    @SuppressWarnings("unchecked")
    private void readStreamsFromIntent() {
        Intent intent = getIntent();
        Serializable extra = intent.getSerializableExtra(EXTRA_STREAMS);
        if (extra instanceof ArrayList<?>) {
            for (Object item : (ArrayList<?>) extra) {
                if (item instanceof Map) {
                    streams.add(new HashMap<>((Map<String, Object>) item));
                }
            }
        }
    }
}
