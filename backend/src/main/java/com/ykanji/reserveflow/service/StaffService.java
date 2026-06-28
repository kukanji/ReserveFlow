package com.ykanji.reserveflow.service;

import com.ykanji.reserveflow.dto.StaffListDto;
import com.ykanji.reserveflow.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<StaffListDto> findStaffList() {

        return staffRepository.findAll()
                .stream()
                .map(staff ->
                        new StaffListDto(
                                staff.getId(),
                                staff.getName()
                        )
                )
                .toList();
    }
}