package net.vanduijn.worktimetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.vanduijn.worktimetracker.Adapters.LogEntryAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OverviewActivity extends AppCompatActivity implements LogEntryAdapter.ItemClickListener {

    private LocalDateTime dateTime = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

    private TextView txtMonth;
    private LogEntryAdapter logEntryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Button btnPrevious = findViewById(R.id.btn_previous_month);
        btnPrevious.setWidth(getResources().getDisplayMetrics().widthPixels/3);

        txtMonth = findViewById(R.id.txt_month);
        txtMonth.setWidth(getResources().getDisplayMetrics().widthPixels/3);
        txtMonth.setText(getMonthYearString(dateTime));

        Button btnNext = findViewById(R.id.btn_next_month);
        btnNext.setWidth(getResources().getDisplayMetrics().widthPixels/3);

        //todo: get data from this month

        //temp
        ArrayList<String> dates = new ArrayList<>();
        dates.add("2020-03-20");
        dates.add("2020-03-21");
        dates.add("2020-03-22");
        dates.add("2020-03-23");
        dates.add("2020-03-24");
        dates.add("2020-03-25");

        //Setup recyclerView
        RecyclerView recyclerView = findViewById(R.id.rvWorkLogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        logEntryAdapter = new LogEntryAdapter(this, dates);
        logEntryAdapter.setClickListener(this);
        recyclerView.setAdapter(logEntryAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + logEntryAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    public void previousMonth(View v) {
        dateTime = dateTime.minusMonths(1);
        txtMonth.setText(getMonthYearString(dateTime));

        //todo: get data previous month
    }

    public void nextMonth(View v) {
        dateTime = dateTime.plusMonths(1);
        txtMonth.setText(getMonthYearString(dateTime));

        //todo: get data next month
    }

    private String getMonthYearString(LocalDateTime dateTime) {
        return dateTime.getMonth().toString() + " " + dateTime.getYear();
    }
}
