package com.ykanji.reserveflow.dto;

import java.util.List;

public class CustomerDetailDto {

    private Long id;

    private String name;

    private String phoneNumber;

    private String memo;

    private TreatmentHistoryDto lastTreatment;

    private List<TreatmentHistoryDto> visitHistories;

    // constructor
    // getter
}