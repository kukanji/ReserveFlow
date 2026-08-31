package com.ykanji.reserveflow.dto;

import java.time.LocalDateTime;

public class ReservationCreateRequest {

    private Long customerId;

    private Long staffId;

    private Long menuId;

    private LocalDateTime startTime;

    private String memo;

    public ReservationCreateRequest() {
    }

    public ReservationCreateRequest(
            Long customerId,
            Long staffId,
            Long menuId,
            LocalDateTime startTime,
            String memo
    ) {
        this.customerId = customerId;
        this.staffId = staffId;
        this.menuId = menuId;
        this.startTime = startTime;
        this.memo = memo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getMemo() {
        return memo;
    }
}
