package com.ykanji.reserveflow.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ShiftDayEntry {

    private LocalDate workDate;

    private boolean off;

    private LocalTime startTime;

    private LocalTime endTime;

    private String task;

    protected ShiftDayEntry() {
    }

    public ShiftDayEntry(
            LocalDate workDate,
            boolean off,
            LocalTime startTime,
            LocalTime endTime,
            String task
    ) {
        this.workDate = workDate;
        this.off = off;
        this.startTime = startTime;
        this.endTime = endTime;
        this.task = task;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public boolean isOff() {
        return off;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getTask() {
        return task;
    }
}
