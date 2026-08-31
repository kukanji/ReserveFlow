package com.ykanji.reserveflow.controller;

import com.ykanji.reserveflow.dto.ReservationListDto;
import com.ykanji.reserveflow.service.ReservationService;
import com.ykanji.reserveflow.dto.ReservationCreateRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationListDto> getReservationList() {
        return reservationService.findReservationList();
    }

    @PostMapping
    public ResponseEntity<Long> createReservation(
            @RequestBody ReservationCreateRequest request
    ) {
        Long id = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
}
