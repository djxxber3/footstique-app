package com.footstique.live.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.footstique.live.models.Channel;
import com.footstique.live.models.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoPlayer {

    public static void playChannel(Context context, Channel channel) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.footstique.fsplayer", "com.footstique.player.PlayerActivity"));
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
            } catch (JSONException ignored) {
            }
        }
        String json = arr.toString();
        intent.putExtra("streams_json", json);
        context.startActivity(intent);
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