package com.ykanji.reserveflow.controller;

import com.ykanji.reserveflow.dto.ShiftBulkUpsertRequest;
import com.ykanji.reserveflow.dto.ShiftListDto;
import com.ykanji.reserveflow.service.ShiftService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping
    public List<ShiftListDto> getShifts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return shiftService.findByDateRange(start, end);
    }

    @PostMapping("/bulk")
    public void bulkUpsertShifts(@RequestBody ShiftBulkUpsertRequest request) {
        shiftService.bulkUpsertShifts(request);
    }

    @DeleteMapping("/{id}")
    public void deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
    }
}
