package com.ykanji.reserveflow.repository;

import com.ykanji.reserveflow.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {
}