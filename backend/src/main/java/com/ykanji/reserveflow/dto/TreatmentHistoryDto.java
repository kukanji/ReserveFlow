package com.ykanji.reserveflow.dto;

import java.time.LocalDate;

public class TreatmentHistoryDto {

    private LocalDate visitDate;

    private String menuName;

    private String staffName;

    private String memo;

    public TreatmentHistoryDto(
            LocalDate visitDate,
            String menuName,
            String staffName,
            String memo
    ) {
        this.visitDate = visitDate;
        this.menuName = menuName;
        this.staffName = staffName;
        this.memo = memo;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getMemo() {
        return memo;
    }
}