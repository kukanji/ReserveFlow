package com.ykanji.reserveflow.controller;

import com.ykanji.reserveflow.dto.StaffListDto;
import com.ykanji.reserveflow.service.StaffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/staffs")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public List<StaffListDto> getStaffList() {
        return staffService.findStaffList();
    }
}