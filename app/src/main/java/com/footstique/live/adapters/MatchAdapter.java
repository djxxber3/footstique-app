package com.footstique.live.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.footstique.live.R;
import com.footstique.live.models.Competition;
import com.footstique.live.models.Match;
import com.footstique.live.utils.ImageLoader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MatchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_MATCH = 1;

    private final Context context;
    private final List<Object> items; // قائمة تحتوي على دوريات ومباريات
    private final OnMatchClickListener listener;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

    public interface OnMatchClickListener {
        void onMatchClick(Match match);
    }

    public MatchAdapter(Context context, List<Object> items, OnMatchClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
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
            ImageLoader.loadImage(competition.getLogo(), headerHolder.leagueLogo, R.color.fs_dark_grey_secondary);
        } else {
            MatchViewHolder matchHolder = (MatchViewHolder) holder;
            Match match = (Match) items.get(position);

            matchHolder.homeTeamName.setText(match.getHomeTeam().getName());
            matchHolder.awayTeamName.setText(match.getAwayTeam().getName());
            ImageLoader.loadImage(match.getHomeTeam().getLogo(), matchHolder.homeTeamLogo, R.color.fs_dark_grey_secondary);
            ImageLoader.loadImage(match.getAwayTeam().getLogo(), matchHolder.awayTeamLogo, R.color.fs_dark_grey_secondary);

            if (match.getStatus().equals("LIVE") || match.getStatus().equals("FT")) {
                String score = match.getHomeTeam().getGoals() + " - " + match.getAwayTeam().getGoals();
                matchHolder.matchTime.setText(score);
            } else {
                matchHolder.matchTime.setText(timeFormat.format(match.getKickoffTime()));
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

    // ViewHolder لرأس الدوري
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView leagueLogo;
        TextView leagueName;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            leagueLogo = itemView.findViewById(R.id.league_logo);
            leagueName = itemView.findViewById(R.id.league_name);
        }
    }

    // ViewHolder للمباراة (لاحظ تغيير IDs)
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