package com.ykanji.reserveflow.dto;

public class CustomerListDto {

    private Long id;

    private String name;

    private String phoneNumber;

    public CustomerListDto(Long id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
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
}
