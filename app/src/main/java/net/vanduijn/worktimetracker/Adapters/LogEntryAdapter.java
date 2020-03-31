package net.vanduijn.worktimetracker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.vanduijn.worktimetracker.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryAdapter.ViewHolder> {

    private List<String> data;
    private LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;

    // data is passed into the constructor
    public LogEntryAdapter(Context context, List<String> data) {
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
        String date = data.get(position);
        holder.txtDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
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

        ViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txt_day);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
