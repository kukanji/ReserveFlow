package com.ykanji.reserveflow.dto;

import java.util.List;

public class CustomerDetailDto {

    private Long id;

    private String name;

    private String phoneNumber;

    private String memo;

    private TreatmentHistoryDto lastTreatment;

    private List<TreatmentHistoryDto> visitHistories;

    public CustomerDetailDto(
            Long id,
            String name,
            String phoneNumber,
            String memo,
            TreatmentHistoryDto lastTreatment,
            List<TreatmentHistoryDto> visitHistories
    ) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.memo = memo;
        this.lastTreatment = lastTreatment;
        this.visitHistories = visitHistories;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMemo() {
        return memo;
    }

    public TreatmentHistoryDto getLastTreatment() {
        return lastTreatment;
    }

    public List<TreatmentHistoryDto> getVisitHistories() {
        return visitHistories;
    }
}