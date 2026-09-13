package com.ykanji.reserveflow.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ShiftListDto {

    private Long id;

    private Long staffId;

    private String staffName;

    private LocalDate workDate;

    private boolean off;

    private LocalTime startTime;

    private LocalTime endTime;

    private String task;

    public ShiftListDto(
            Long id,
            Long staffId,
            String staffName,
            LocalDate workDate,
            boolean off,
            LocalTime startTime,
            LocalTime endTime,
            String task
    ) {
        this.id = id;
        this.staffId = staffId;
        this.staffName = staffName;
        this.workDate = workDate;
        this.off = off;
        this.startTime = startTime;
        this.endTime = endTime;
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public Long getStaffId() {
        return staffId;
    }

    public String getStaffName() {
        return staffName;
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
