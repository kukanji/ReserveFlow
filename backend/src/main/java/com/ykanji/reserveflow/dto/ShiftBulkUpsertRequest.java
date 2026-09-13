package com.ykanji.reserveflow.dto;

import java.util.List;

public class ShiftBulkUpsertRequest {

    private Long staffId;

    private List<ShiftDayEntry> days;

    protected ShiftBulkUpsertRequest() {
    }

    public ShiftBulkUpsertRequest(Long staffId, List<ShiftDayEntry> days) {
        this.staffId = staffId;
        this.days = days;
    }

    public Long getStaffId() {
        return staffId;
    }

    public List<ShiftDayEntry> getDays() {
        return days;
    }
}
