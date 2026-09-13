package com.ykanji.reserveflow.service;

import com.ykanji.reserveflow.dto.StaffCreateRequest;
import com.ykanji.reserveflow.dto.StaffListDto;
import com.ykanji.reserveflow.entity.Staff;
import com.ykanji.reserveflow.repository.ShiftRepository;
import com.ykanji.reserveflow.repository.StaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final ShiftRepository shiftRepository;

    public StaffService(StaffRepository staffRepository, ShiftRepository shiftRepository) {
        this.staffRepository = staffRepository;
        this.shiftRepository = shiftRepository;
    }

    public List<StaffListDto> findStaffList() {

        return staffRepository.findByActiveTrue()
                .stream()
                .map(staff ->
                        new StaffListDto(
                                staff.getId(),
                                staff.getName()
                        )
                )
                .toList();
    }

    public Long createStaff(StaffCreateRequest request) {

        Staff staff = new Staff(request.getName());
        return staffRepository.save(staff).getId();
    }

    public void deactivateStaff(Long id) {

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        staff.setActive(false);
        staffRepository.save(staff);

        shiftRepository.deleteByStaffIdAndWorkDateGreaterThanEqual(id, LocalDate.now());
    }
}
