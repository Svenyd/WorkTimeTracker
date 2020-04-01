package net.vanduijn.worktimetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import net.vanduijn.worktimetracker.adapters.LogEntryAdapter;
import net.vanduijn.worktimetracker.models.WorkLog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class OverviewActivity extends AppCompatActivity implements LogEntryAdapter.ItemClickListener {

    private LocalDateTime dateTime = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

    private TextView txtMonth;
    private LogEntryAdapter logEntryAdapter;

    private static final String TAG = "OVERVIEW_ACTIVITY";
    private static final String WORK_LOG = "work_log";

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

        fillLogList();
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + logEntryAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    public void fillLogList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        db.collection(user.getUid()).document(WORK_LOG).collection(dateTime.toString().substring(0, 7))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<WorkLog> workLogs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            workLogs.add(document.toObject(WorkLog.class));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }

                        Collections.sort(workLogs);

                        //Setup recyclerView
                        RecyclerView recyclerView = findViewById(R.id.rvWorkLogs);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        logEntryAdapter = new LogEntryAdapter(this, workLogs);
                        logEntryAdapter.setClickListener(this);
                        recyclerView.setAdapter(logEntryAdapter);

                        //Calculate totalTime
                        TextView txtTotalTime = findViewById(R.id.txt_total_time_month);
                        int totalTimeInSeconds = workLogs.stream().mapToInt(WorkLog::getTimeWorked).sum();
                        String hours = (int) Math.floor(totalTimeInSeconds / 3600) < 10 ? "0" + (int) Math.floor(totalTimeInSeconds / 3600) : String.valueOf((int) Math.floor(totalTimeInSeconds / 3600));
                        String minutes = (totalTimeInSeconds % 3600) / 60 < 10 ? "0" + (totalTimeInSeconds % 3600) / 60 : (totalTimeInSeconds % 3600) / 60 + "";
                        txtTotalTime.setText(hours + ":" + minutes);

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    public void previousMonth(View v) {
        dateTime = dateTime.minusMonths(1);
        txtMonth.setText(getMonthYearString(dateTime));

        fillLogList();
    }

    public void nextMonth(View v) {
        dateTime = dateTime.plusMonths(1);
        txtMonth.setText(getMonthYearString(dateTime));

        fillLogList();
    }

    private String getMonthYearString(LocalDateTime dateTime) {
        return dateTime.getMonth().toString() + " " + dateTime.getYear();
    }
}
