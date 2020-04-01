package net.vanduijn.worktimetracker.models;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class WorkLog implements Comparable {
    private LocalDateTime start_time;
    private LocalDateTime end_time;

    public WorkLog() {
    }

    public WorkLog(String startTime, String endTime) {
        this.start_time = LocalDateTime.parse(startTime);
        this.end_time = LocalDateTime.parse(endTime);
    }

    public int getTimeWorked() {
        return end_time != null ? (int) (end_time.toEpochSecond(ZoneOffset.UTC) - start_time.toEpochSecond(ZoneOffset.UTC)) : 0;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = LocalDateTime.parse(start_time);
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = LocalDateTime.parse(end_time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkLog workLog = (WorkLog) o;
        return Objects.equals(start_time, workLog.start_time) &&
                Objects.equals(end_time, workLog.end_time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start_time, end_time);
    }

    @Override
    public int compareTo(Object o) {
        int comparage = (int) ((WorkLog) o).start_time.toEpochSecond(ZoneOffset.UTC);
        return (int) (this.start_time.toEpochSecond(ZoneOffset.UTC)-comparage);
    }
}
