package net.vanduijn.worktimetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.vanduijn.worktimetracker.R;
import net.vanduijn.worktimetracker.models.WorkLog;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryAdapter.ViewHolder> {

    private List<WorkLog> data;
    private LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;

    // data is passed into the constructor
    public LogEntryAdapter(Context context, ArrayList<WorkLog> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.log_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkLog workLog = data.get(position);

        if (workLog.getEnd_time() == null) {
            workLog.setEnd_time(LocalDateTime.now().toString());
        }

        String date = workLog.getStart_time().toString().substring(0, 10);
        String time = workLog.getStart_time().toString().substring(11, 16) + " - " + workLog.getEnd_time().toString().substring(11,16);
        String totalHours = getTotalHoursWorked(workLog);

        holder.txtDate.setText(date);
        holder.txtTime.setText(time);
        holder.txtTotalHours.setText(totalHours);
    }

    private String getTotalHoursWorked(WorkLog workLog) {
        long diffMinutes = ChronoUnit.MINUTES.between(workLog.getStart_time(), workLog.getEnd_time());

        String hours = Math.floor(diffMinutes/60) < 10 ? "0" + (int) Math.floor(diffMinutes/60) : (int) Math.floor(diffMinutes/60) + "";
        String minutes = diffMinutes % 60 < 10 ? "0" + diffMinutes % 60 : diffMinutes % 60 + "";

        return hours + ":" + minutes;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // convenience method for getting data at click position
    public WorkLog getItem(int id) {
        return data.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtDate;
        TextView txtTime;
        TextView txtTotalHours;

        ViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txt_day);
            txtTime = itemView.findViewById(R.id.txt_start_end_time);
            txtTotalHours = itemView.findViewById(R.id.txt_total_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
