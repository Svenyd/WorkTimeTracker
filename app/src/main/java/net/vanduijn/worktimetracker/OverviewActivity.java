package net.vanduijn.worktimetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.vanduijn.worktimetracker.Adapters.LogEntryAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class OverviewActivity extends AppCompatActivity implements LogEntryAdapter.ItemClickListener {

    private LocalDateTime dateTime = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

    private TextView txtMonth;
    private LogEntryAdapter logEntryAdapter;

    private static final String TAG = "OVERVIEW_ACTIVITY";
    private static final String WORK_LOG = "work_log";
    private static final String LATEST_LOG = "latestLog";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";

    private FirebaseFirestore db;
    private FirebaseUser user;

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

        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        assert user != null;
        db.collection(user.getUid()).document(WORK_LOG).collection(dateTime.toString().substring(0, 7))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });

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
