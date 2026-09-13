package com.ykanji.reserveflow.service;

import com.ykanji.reserveflow.dto.ShiftBulkUpsertRequest;
import com.ykanji.reserveflow.dto.ShiftDayEntry;
import com.ykanji.reserveflow.dto.ShiftListDto;
import com.ykanji.reserveflow.entity.Shift;
import com.ykanji.reserveflow.entity.Staff;
import com.ykanji.reserveflow.repository.ShiftRepository;
import com.ykanji.reserveflow.repository.StaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final StaffRepository staffRepository;

    public ShiftService(ShiftRepository shiftRepository, StaffRepository staffRepository) {
        this.shiftRepository = shiftRepository;
        this.staffRepository = staffRepository;
    }

    public List<ShiftListDto> findByDateRange(LocalDate start, LocalDate end) {

        return shiftRepository.findByWorkDateBetween(start, end)
                .stream()
                .map(shift -> new ShiftListDto(
                        shift.getId(),
                        shift.getStaff().getId(),
                        shift.getStaff().getName(),
                        shift.getWorkDate(),
                        shift.isOff(),
                        shift.getStartTime(),
                        shift.getEndTime(),
                        shift.getTask()
                ))
                .toList();
    }

    public void bulkUpsertShifts(ShiftBulkUpsertRequest request) {

        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        for (ShiftDayEntry entry : request.getDays()) {

            if (entry.getWorkDate() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "workDate is required");
            }

            Shift shift = shiftRepository.findByStaffIdAndWorkDate(staff.getId(), entry.getWorkDate())
                    .orElseGet(() -> new Shift(staff, entry.getWorkDate(), false, null, null, null));

            shift.setOff(entry.isOff());
            shift.setStartTime(entry.isOff() ? null : entry.getStartTime());
            shift.setEndTime(entry.isOff() ? null : entry.getEndTime());
            shift.setTask(entry.isOff() ? null : entry.getTask());

            shiftRepository.save(shift);
        }
    }

    public void deleteShift(Long id) {
        shiftRepository.deleteById(id);
    }
}
