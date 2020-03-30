package net.vanduijn.worktimetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.time.LocalDateTime;

public class OverviewActivity extends AppCompatActivity {

    private LocalDateTime dateTime = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

    private TextView txtMonth;

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
    }

    public void previousMonth(View v) {
        dateTime = dateTime.minusMonths(1);
        txtMonth.setText(getMonthYearString(dateTime));
    }

    public void nextMonth(View v) {
        dateTime = dateTime.plusMonths(1);
        txtMonth.setText(getMonthYearString(dateTime));
    }

    private String getMonthYearString(LocalDateTime dateTime) {
        return dateTime.getMonth().toString() + " " + dateTime.getYear();
    }
}
