package com.ykanji.reserveflow.dto;

public class CustomerCreateRequest {

    private String name;

    private String phoneNumber;

    private String memo;

    // JPA(Jackson)用
    protected CustomerCreateRequest() {
    }

    public CustomerCreateRequest(
            String name,
            String phoneNumber,
            String memo
    ) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.memo = memo;
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
}