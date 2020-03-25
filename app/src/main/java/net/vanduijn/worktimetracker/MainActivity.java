package net.vanduijn.worktimetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private boolean isWorking = false;
    private String logRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void startStopWorking(View v) {
        LocalDateTime now = LocalDateTime.now();

        if (!isWorking) {
            saveStartTime(now);
            this.isWorking = true;
            updateStartStopButton();
            Log.d("StartTime", now.toString());
        } else {
            saveEndTime(now);
            this.isWorking = false;
            updateStartStopButton();
            Log.d("EndTime", now.toString());
        }
    }

    private void updateStartStopButton() {
        Button startStopButton = findViewById(R.id.btn_start_stop);
        if (!isWorking) {
            startStopButton.setText(R.string.start_work);
        } else {
            startStopButton.setText(R.string.stop_work);
        }
    }

    private void saveStartTime(LocalDateTime now) {
        //TODO: save startTime to Firebase
        Map<String, String> log = new HashMap<>();
        log.put("start_time", now.toString());

        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        db.collection(user.getUid()).document("work_log").collection(now.toString().substring(0, 7))
                .add(log)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        logRef = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void saveEndTime(LocalDateTime now) {
        //TODO: save end time to Firebase
    }
}
