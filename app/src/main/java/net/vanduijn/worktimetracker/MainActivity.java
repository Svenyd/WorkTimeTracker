package net.vanduijn.worktimetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final String WORK_LOG = "work_log";
    private static final String LATEST_LOG = "latestLog";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";

    private boolean isWorking = false;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private Handler handler;
    private TextView timer;
    long millisecondTime, startTime = 0L;
    int hours, seconds, minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        handler = new Handler();
        timer = findViewById(R.id.txt_timer);

        updateStartStopButton();
    }

    public void startStopWorking(View v) {
        LocalDateTime now = LocalDateTime.now();

        if (!isWorking) {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            saveStartTime(now);
            Log.d("StartTime", now.toString());
        } else {
            saveEndTime(now);
            handler.removeCallbacks(runnable);
            Log.d("EndTime", now.toString());
        }
    }

    private void updateStartStopButton() {
        Button startStopButton = findViewById(R.id.btn_start_stop);

        db.collection(user.getUid()).document(WORK_LOG).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.contains("isWorking")) {
                        isWorking = (boolean) documentSnapshot.get("isWorking");

                        if (!isWorking) {
                            startStopButton.setText(R.string.start_work);
                            handler.removeCallbacks(runnable);
                        } else {
                            startStopButton.setText(R.string.stop_work);
                            if (documentSnapshot.contains("startTimeMillis")) {
                                startTime = (long) documentSnapshot.get("startTimeMillis");
                                handler.postDelayed(runnable, 0);
                            }
                        }
                    }
                    Log.d(TAG, "DocumentSnapshot retrieved with ID: " + documentSnapshot.getId());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error retrieving document"));
    }

    private void saveStartTime(LocalDateTime now) {
        Map<String, String> log = new HashMap<>();
        log.put(START_TIME, now.toString());

        db.collection(user.getUid()).document(WORK_LOG).collection(now.toString().substring(0, 7))
                .add(log)
                .addOnSuccessListener(documentReference -> {
                    setLatestLog(documentReference);
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    private void saveEndTime(LocalDateTime now) {
        db.collection(user.getUid()).document(WORK_LOG).get()
                .addOnSuccessListener(documentSnapshot -> {
                    DocumentReference documentReference = (DocumentReference) documentSnapshot.get(LATEST_LOG);
                    assert documentReference != null;
                    Log.d(TAG, "Latest log path: " + documentReference.getPath());
                    saveEndTime(now, documentReference);
                });

    }

    private void saveEndTime(LocalDateTime now, DocumentReference documentReference) {
        db.document(documentReference.getPath())
                .update(END_TIME, now.toString())
                .addOnSuccessListener(aVoid -> {
                    setIsWorking(false);
                    Log.d(TAG, "End_time added in: " + documentReference.getPath());
                });
    }

    private void setStartTimeMillis(long startTime) {
        db.collection(user.getUid()).document(WORK_LOG)
                .update("startTimeMillis", startTime)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "startTimeMillis successfully updated to: " + startTime);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "startTimeMillis does not exist, adding startTimeMillis...");
                    Map<String, Long> map = new HashMap<>();
                    map.put("startTimeMillis", startTime);
                    db.collection(user.getUid()).document(WORK_LOG).set(map).addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "startTimeMillis successfully added");
                    });
                })
                .addOnCompleteListener(task -> setIsWorking(true));
    }

    private void setIsWorking(boolean isWorking) {
        db.collection(user.getUid()).document(WORK_LOG)
                .update("isWorking", isWorking)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "isWorking successfully updated to: " + isWorking);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "isWorking does not exist, adding isWorking...");
                    Map<String, Boolean> map = new HashMap<>();
                    map.put("isWorking", isWorking);
                    db.collection(user.getUid()).document(WORK_LOG).set(map);
                })
                .addOnCompleteListener(task -> updateStartStopButton());
    }

    private void setLatestLog(DocumentReference documentReference) {
        db.collection(user.getUid()).document(WORK_LOG)
                .update(LATEST_LOG, documentReference)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "latestPath successfully updated to: " + documentReference.getPath());
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "latestLog does not exist, adding latestLog...");
                    Map<String, DocumentReference> map = new HashMap<>();
                    map.put(LATEST_LOG, documentReference);
                    db.collection(user.getUid()).document(WORK_LOG).set(map);
                })
                .addOnCompleteListener(task -> setStartTimeMillis(startTime));
    }

    public void toMonthOverview(View view) {
        Intent intent = new Intent(this, OverviewActivity.class);
        startActivity(intent);
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            seconds = (int) (millisecondTime / 1000);
            hours = seconds / 3600;
            minutes = (seconds % 3600) / 60;
            seconds = (seconds % 3600) % 60;
            timer.setText("" + String.format("%02d", hours) + ":"
                    + String.format("%02d", minutes) + ":"
                    + String.format("%02d", seconds));
            handler.postDelayed(this, 0);
        }
    };
}
