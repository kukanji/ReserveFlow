package com.ykanji.reserveflow.repository;

import com.ykanji.reserveflow.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    @Query("""
            select case when count(r) > 0 then true else false end
            from Reservation r
            where r.staff.id = :staffId
              and r.startTime < :endTime
              and r.endTime > :startTime
            """)
    boolean existsOverlapping(
            @Param("staffId") Long staffId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
