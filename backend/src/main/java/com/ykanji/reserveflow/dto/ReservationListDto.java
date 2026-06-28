package com.ykanji.reserveflow.dto;

import java.time.LocalDateTime;

public class ReservationListDto {

    private Long id;

    private Long customerId;

    private String customerName;

    private Long staffId;

    private String staffName;

    private Long menuId;

    private String menuName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public ReservationListDto(
            Long id,
            Long customerId,
            String customerName,
            Long staffId,
            String staffName,
            Long menuId,
            String menuName,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.staffId = staffId;
        this.staffName = staffName;
        this.menuId = menuId;
        this.menuName = menuName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getStaffId() {
        return staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public Long getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}