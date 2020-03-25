package net.vanduijn.worktimetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        updateStartStopButton();
    }

    public void startStopWorking(View v) {
        LocalDateTime now = LocalDateTime.now();

        if (!isWorking) {
            saveStartTime(now);
            Log.d("StartTime", now.toString());
        } else {
            saveEndTime(now);
            Log.d("EndTime", now.toString());
        }

        updateStartStopButton();
    }

    private void updateStartStopButton() {
        Button startStopButton = findViewById(R.id.btn_start_stop);

        db.collection(user.getUid()).document(WORK_LOG).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.contains("isWorking")) {
                        isWorking = (boolean) documentSnapshot.get("isWorking");

                        if (!isWorking) {
                            startStopButton.setText(R.string.start_work);
                        } else {
                            startStopButton.setText(R.string.stop_work);
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
                    setIsWorking(true);
                    setLatestLog(documentReference);
                    updateStartStopButton();
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
                    updateStartStopButton();
                    Log.d(TAG, "End_time added in: " + documentReference.getPath());
                });
    }

    private void setIsWorking(boolean isWorking) {
        db.collection(user.getUid()).document(WORK_LOG)
                .update("isWorking", isWorking)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "isWorking successfully updated to: " + isWorking);
                });
    }

    private void setLatestLog(DocumentReference documentReference) {
        db.collection(user.getUid()).document(WORK_LOG)
                .update(LATEST_LOG, documentReference)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "latestPath successfully updated to: " + documentReference.getPath());
                });
    }
}
