package net.vanduijn.worktimetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Button btnPrevious = findViewById(R.id.btn_previous_month);
        btnPrevious.setWidth(getResources().getDisplayMetrics().widthPixels/3);

        TextView txtMonth = findViewById(R.id.txt_month);
        txtMonth.setWidth(getResources().getDisplayMetrics().widthPixels/3);

        Button btnNext = findViewById(R.id.btn_next_month);
        btnNext.setWidth(getResources().getDisplayMetrics().widthPixels/3);


    }

    public void previousMonth(View v) {

    }

    public void nextMonth(View v) {

    }
}
