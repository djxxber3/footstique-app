package com.footstique.live;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.footstique.live.adapters.MatchAdapter;
import com.footstique.live.models.Match;

import org.json.JSONArray;
import org.json.JSONObject;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.footstique.live.utils.TimeUtils;
import java.util.TimeZone;

public class MatchesFragment extends Fragment implements MatchAdapter.OnMatchClickListener {

    private RecyclerView recyclerView;
   private ImageButton btnPreviousDay, btnNextDay;

    private TextView tvCurrentDate;
    private Calendar currentDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private SimpleDateFormat displayDateFormat;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerViewMatches);
        btnPreviousDay = view.findViewById(R.id.btnPreviousDay);
        btnNextDay = view.findViewById(R.id.btnNextDay);
        tvCurrentDate = view.findViewById(R.id.tvCurrentDate);
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
    // Set the current date
        updateDateDisplay();
        
        // Set click listeners for navigation buttons
        btnPreviousDay.setOnClickListener(v -> changeDayBy(-1));
        
        btnNextDay.setOnClickListener(v -> changeDayBy(1));

        // Swipe gestures disabled as requested; navigation via buttons and date picker only.
        
        // Set click listener for the date to show date picker
        tvCurrentDate.setOnClickListener(v -> showDatePicker());
        
        // Display the current matches
        displayMatches(AppData.getInstance().getMatches());
        
        return view;
    }

    private void updateDateDisplay() {
        // --- Determine Locale ---
        String currentLanguage = com.yariksoffice.lingver.Lingver.getInstance().getLanguage();
        Locale displayLocale;
        String displayPattern;

        if (currentLanguage.equals("ar")) {
            displayLocale = new Locale("ar");
            displayPattern = "EEEE, dd MMMM"; // الجمعة، 05 سبتمبر
        } else {
            displayLocale = Locale.US;
            displayPattern = "EEEE, MMMM dd"; // Friday, September 05
        }

        displayDateFormat = new SimpleDateFormat(displayPattern, displayLocale);

        // --- Apply Timezone and Set Text ---
        TimeZone tz = TimeUtils.getPreferredTimeZone(requireContext());
        displayDateFormat.setTimeZone(tz);

        // --- Logic for Today, Yesterday, Tomorrow ---
        Calendar today = Calendar.getInstance(tz);
        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        String dateText;
        if (isSameDay(currentDate, today)) {
            dateText = getString(R.string.today);
        } else if (isSameDay(currentDate, yesterday)) {
            dateText = getString(R.string.yesterday);
        } else if (isSameDay(currentDate, tomorrow)) {
            dateText = getString(R.string.tomorrow);
        } else {
            dateText = displayDateFormat.format(currentDate.getTime());
        }

        tvCurrentDate.setText(dateText);
        updateNavButtons();
    }

    private Calendar getTomorrow() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        return tomorrow;
    }

    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.ERA) == b.get(Calendar.ERA)
                && a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    private void updateNavButtons() {
        if (btnNextDay == null) return;
        Calendar tomorrow = getTomorrow();
        boolean atOrAfterTomorrow = isSameDay(currentDate, tomorrow) || currentDate.after(tomorrow);
        btnNextDay.setEnabled(!atOrAfterTomorrow);
        btnNextDay.setAlpha(atOrAfterTomorrow ? 0.4f : 1.0f);
    }

    private void changeDayBy(int delta) {
        if (delta > 0) {
            Calendar tomorrow = getTomorrow();
            if (isSameDay(currentDate, tomorrow)) {
                updateNavButtons();
                return;
            }
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
            if (currentDate.after(tomorrow)) {
                currentDate = (Calendar) tomorrow.clone();
            }
        } else if (delta < 0) {
            currentDate.add(Calendar.DAY_OF_MONTH, -1);
        }
        updateDateDisplay();
        loadMatchesForDate(dateFormat.format(currentDate.getTime()));
    }
    
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    currentDate.set(Calendar.YEAR, year);
                    currentDate.set(Calendar.MONTH, month);
                    currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    Calendar tomorrow = getTomorrow();
                    if (currentDate.after(tomorrow)) {
                        currentDate = (Calendar) tomorrow.clone();
                    }
                    updateDateDisplay();
                    loadMatchesForDate(dateFormat.format(currentDate.getTime()));
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );
        Calendar max = getTomorrow();
        max.set(Calendar.HOUR_OF_DAY, 23);
        max.set(Calendar.MINUTE, 59);
        max.set(Calendar.SECOND, 59);
        max.set(Calendar.MILLISECOND, 999);
        datePickerDialog.getDatePicker().setMaxDate(max.getTimeInMillis());
        datePickerDialog.show();
    }
    
    private void loadMatchesForDate(String date) {
        String serverUrl = AppData.getInstance().getServerUrl();
        if (serverUrl == null) {
            Toast.makeText(getContext(), "Server URL not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String matchesUrl = serverUrl + "/api/client/matches/" + date;
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL(matchesUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                JSONObject matchesJson = new JSONObject(response.toString());
                List<Match> matches = new ArrayList<>();
                
                if (matchesJson.getBoolean("success")) {
                    JSONArray matchesArray = matchesJson.getJSONObject("data").getJSONArray("matches");
                    
                    for (int i = 0; i < matchesArray.length(); i++) {
                        matches.add(Match.fromJson(matchesArray.getJSONObject(i)));
                    }
                    
                    requireActivity().runOnUiThread(() -> displayMatches(matches));
                } else {
                    requireActivity().runOnUiThread(() -> 
                            Toast.makeText(getContext(), "Error loading matches", Toast.LENGTH_LONG).show());
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                executor.shutdown();
            }
        });
    }

    private void displayMatches(List<Match> matches) {
        if (getContext() == null) return;

        // ----- الكود الجديد لتجميع المباريات -----
        List<Object> groupedItems = new ArrayList<>();
        if (matches != null && !matches.isEmpty()) {
            // استخدام LinkedHashMap للحفاظ على ترتيب الدوريات كما وردت من السيرفر
            LinkedHashMap<String, List<Match>> map = new LinkedHashMap<>();
            for (Match match : matches) {
                String competitionId = match.getCompetition().getName(); // استخدام اسم الدوري كمعرّف
                if (!map.containsKey(competitionId)) {
                    map.put(competitionId, new ArrayList<>());
                }
                map.get(competitionId).add(match);
            }

            for (String competitionId : map.keySet()) {
                List<Match> leagueMatches = map.get(competitionId);
                if (leagueMatches != null && !leagueMatches.isEmpty()) {
                    // إضافة رأس الدوري أولاً
                    groupedItems.add(leagueMatches.get(0).getCompetition());
                    // ثم إضافة كل مباريات هذا الدوري
                    groupedItems.addAll(leagueMatches);
                }
            }
        }
        // ----- نهاية الكود الجديد -----

        // استخدام القائمة المجمعة في الـ Adapter
        MatchAdapter adapter = new MatchAdapter(getContext(), groupedItems, this);
        recyclerView.setAdapter(adapter);
    }
    
    @Override
    public void onMatchClick(Match match) {
        Intent intent = new Intent(getActivity(), MatchDetailActivity.class);
        intent.putExtra("match", match);
        startActivity(intent);
    }
}
