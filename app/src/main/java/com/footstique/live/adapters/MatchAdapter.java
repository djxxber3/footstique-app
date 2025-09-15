package com.footstique.live.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.footstique.live.R;
import com.footstique.live.models.Competition;
import com.footstique.live.models.Match;
import com.footstique.live.utils.TimeUtils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MatchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_MATCH = 1;

    private final Context context;
    private final List<Object> items;
    private final OnMatchClickListener listener;
    private final SimpleDateFormat timeFormat;
    private final TimeZone preferredTimeZone; // تم تعريف المتغير هنا

    public interface OnMatchClickListener {
        void onMatchClick(Match match);
    }

    // تم تعديل الـ Constructor بالكامل
    public MatchAdapter(Context context, List<Object> items, OnMatchClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.preferredTimeZone = TimeUtils.getPreferredTimeZone(context);

        // Conditional Time Formatting based on current language
        String currentLanguage = com.yariksoffice.lingver.Lingver.getInstance().getLanguage();
        if (currentLanguage.equals("ar")) {
            // Use Locale.US to force Latin digits, then set custom symbols for Arabic
            this.timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            DateFormatSymbols arSymbols = new DateFormatSymbols();
            arSymbols.setAmPmStrings(new String[]{"ص", "م"});
            this.timeFormat.setDateFormatSymbols(arSymbols);
        } else {
            // Default to US locale for other languages (e.g., English)
            this.timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        }
        this.timeFormat.setTimeZone(this.preferredTimeZone);
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Competition) {
            return TYPE_HEADER;
        } else {
            return TYPE_MATCH;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_league_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false);
            return new MatchViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            Competition competition = (Competition) items.get(position);
            headerHolder.leagueName.setText(competition.getName());
            Glide.with(context)
                    .load(competition.getLogo())
                    .placeholder(R.color.fs_dark_grey_secondary)
                    .error(R.color.fs_dark_grey_secondary)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(headerHolder.leagueLogo);
        } else {
            MatchViewHolder matchHolder = (MatchViewHolder) holder;
            Match match = (Match) items.get(position);

            matchHolder.homeTeamName.setText(match.getHomeTeam().getName());
            matchHolder.awayTeamName.setText(match.getAwayTeam().getName());
            Glide.with(context)
                    .load(match.getHomeTeam().getLogo())
                    .placeholder(R.color.fs_dark_grey_secondary)
                    .error(R.color.fs_dark_grey_secondary)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(matchHolder.homeTeamLogo);
            Glide.with(context)
                    .load(match.getAwayTeam().getLogo())
                    .placeholder(R.color.fs_dark_grey_secondary)
                    .error(R.color.fs_dark_grey_secondary)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(matchHolder.awayTeamLogo);

            if (match.getStatus().equals("LIVE") || match.getStatus().equals("FT")) {
                String score = match.getHomeTeam().getGoals() + " - " + match.getAwayTeam().getGoals();
                matchHolder.matchTime.setText(score);
            } else {
                String formatted = timeFormat.format(match.getKickoffTime());
                int space = formatted.lastIndexOf(' ');
                if (space > 0 && space < formatted.length() - 1) {
                    SpannableString span = new SpannableString(formatted);
                    span.setSpan(new RelativeSizeSpan(0.8f), space + 1, formatted.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    matchHolder.matchTime.setText(span);
                } else {
                    matchHolder.matchTime.setText(formatted);
                }
            }

            matchHolder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMatchClick(match);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView leagueLogo;
        TextView leagueName;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            leagueLogo = itemView.findViewById(R.id.league_logo);
            leagueName = itemView.findViewById(R.id.league_name);
        }
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView homeTeamName, awayTeamName, matchTime;
        ImageView homeTeamLogo, awayTeamLogo;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            homeTeamName = itemView.findViewById(R.id.team1_name);
            awayTeamName = itemView.findViewById(R.id.team2_name);
            matchTime = itemView.findViewById(R.id.match_time);
            homeTeamLogo = itemView.findViewById(R.id.team1_logo);
            awayTeamLogo = itemView.findViewById(R.id.team2_logo);
        }
    }
}