package com.ykanji.reserveflow.service;

import com.ykanji.reserveflow.dto.ReservationListDto;
import com.ykanji.reserveflow.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationListDto> findReservationList() {

        return reservationRepository.findAll()
                .stream()
                .map(reservation -> new ReservationListDto(
                        reservation.getId(),

                        reservation.getCustomer().getId(),
                        reservation.getCustomer().getName(),

                        reservation.getStaff().getId(),
                        reservation.getStaff().getName(),

                        reservation.getMenu().getId(),
                        reservation.getMenu().getName(),

                        reservation.getStartTime(),
                        reservation.getEndTime()
                ))
                .toList();
    }
}