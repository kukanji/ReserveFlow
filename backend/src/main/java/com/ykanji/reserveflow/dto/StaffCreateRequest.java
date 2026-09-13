package com.ykanji.reserveflow.dto;

public class StaffCreateRequest {

    private String name;

    protected StaffCreateRequest() {
    }

    public StaffCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
