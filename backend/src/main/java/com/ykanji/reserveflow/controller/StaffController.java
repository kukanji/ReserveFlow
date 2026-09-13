package com.ykanji.reserveflow.controller;

import com.ykanji.reserveflow.dto.StaffCreateRequest;
import com.ykanji.reserveflow.dto.StaffListDto;
import com.ykanji.reserveflow.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Long> createStaff(@RequestBody StaffCreateRequest request) {
        Long id = staffService.createStaff(request);
        return ResponseEntity.status(201).body(id);
    }

    @DeleteMapping("/{id}")
    public void deleteStaff(@PathVariable Long id) {
        staffService.deactivateStaff(id);
    }
}
