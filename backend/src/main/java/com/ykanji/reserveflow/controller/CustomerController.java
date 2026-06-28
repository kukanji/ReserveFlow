package com.ykanji.reserveflow.controller;

import com.ykanji.reserveflow.entity.Customer;
import com.ykanji.reserveflow.repository.CustomerRepository;
import com.ykanji.reserveflow.dto.*;
import com.ykanji.reserveflow.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetailDto> getCustomer(@PathVariable Long id) {
        return customerService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}