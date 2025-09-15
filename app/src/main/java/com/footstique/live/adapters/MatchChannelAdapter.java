package com.footstique.live.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.footstique.live.R;
import com.footstique.live.models.MatchChannel;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adapter to display only the broadcasting channels for a match (no stream links).
 */
public class MatchChannelAdapter extends RecyclerView.Adapter<MatchChannelAdapter.ChannelViewHolder> {

    private final Context context;
    private final List<MatchChannel> channels;

    public MatchChannelAdapter(Context context, List<MatchChannel> channels) {
        this.context = context;
        this.channels = channels;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        MatchChannel channel = channels.get(position);
        holder.tvChannelName.setText(channel.getName());
        com.bumptech.glide.Glide.with(context)
            .load(channel.getLogo())
            .placeholder(R.color.fs_dark_grey_secondary)
            .error(R.color.fs_dark_grey_secondary)
            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
            .into(holder.ivChannelLogo);
        // Open player with this channel's streams on item click
        holder.itemView.setOnClickListener(v -> {
            JSONArray arr = new JSONArray();
            for (com.footstique.live.models.Stream s : channel.getStreams()) {
                if (s == null || s.getUrl() == null || s.getUrl().trim().isEmpty()) continue;
                try {
                    JSONObject o = new JSONObject();
                    o.put("url", s.getUrl());
                    String label = firstNonEmpty(s.getLabel(), s.getQuality(), "Stream");
                    o.put("label", label);
                    putIf(o, "userAgent", s.getUserAgent());
                    putIf(o, "referer", s.getReferer());
                    putIf(o, "cookie", s.getCookie());
                    putIf(o, "origin", s.getOrigin());
                    arr.put(o);
                } catch (JSONException ignored) {}
            }
            String json = arr.toString();
            Intent intent = new Intent();
            // استخدام Intent مع action و package
          intent.setComponent(new ComponentName("com.footstique.fsplayer","com.footstique.player.PlayerActivity"));
            intent.putExtra("streams_json", json);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                  Toast.makeText(context, "الرجاء تثبيت مشغل الفيديو", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void putIf(JSONObject o, String k, String v) throws JSONException {
        if (v != null && !v.trim().isEmpty()) o.put(k, v.trim());
    }

    private static String firstNonEmpty(String... vals) {
        if (vals == null) return "";
        for (String v : vals) if (v != null && !v.trim().isEmpty()) return v.trim();
        return "";
    }

    @Override
    public int getItemCount() {
        return channels != null ? channels.size() : 0;
    }

    static class ChannelViewHolder extends RecyclerView.ViewHolder {
        ImageView ivChannelLogo;
        TextView tvChannelName;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChannelLogo = itemView.findViewById(R.id.channel_logo);
            tvChannelName = itemView.findViewById(R.id.channel_name);
        }
    }
}