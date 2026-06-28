package com.ykanji.reserveflow.repository;

import com.ykanji.reserveflow.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {
}