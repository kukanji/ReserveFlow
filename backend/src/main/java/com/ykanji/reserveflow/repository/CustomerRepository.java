package com.ykanji.reserveflow.repository;

import com.ykanji.reserveflow.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}