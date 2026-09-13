package com.ykanji.reserveflow.repository;

import com.ykanji.reserveflow.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByWorkDateBetween(LocalDate start, LocalDate end);

    Optional<Shift> findByStaffIdAndWorkDate(Long staffId, LocalDate workDate);

    void deleteByStaffIdAndWorkDateGreaterThanEqual(Long staffId, LocalDate workDate);
}
