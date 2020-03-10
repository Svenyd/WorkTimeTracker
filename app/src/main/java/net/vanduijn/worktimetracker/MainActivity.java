package net.vanduijn.worktimetracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {

    private boolean isWorking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startStopWorking(View v) {
        LocalDateTime now = LocalDateTime.now();

        if (!isWorking) {
            saveStartTime(now);
            this.isWorking = true;
            updateStartStopButton();
            Log.d("StartTime", now.toString());
        } else {
            saveEndTime(now);
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
    }

    private void saveEndTime(LocalDateTime now) {
        //TODO: save end time to Firebase
    }
}
