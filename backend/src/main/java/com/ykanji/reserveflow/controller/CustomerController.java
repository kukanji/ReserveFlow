package com.ykanji.reserveflow.controller;

import com.ykanji.reserveflow.entity.Customer;
import com.ykanji.reserveflow.repository.CustomerRepository;
import com.ykanji.reserveflow.dto.*;
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

        return customerRepository.findById(id)
        // Optional型の中身が空でなければ.map()が実行される。中身が空であれば.orElse()が実行される。
            .map(customer -> {

                CustomerDetailDto dto =
                        new CustomerDetailDto(
                                customer.getId(),
                                customer.getName());

                return ResponseEntity.ok(dto);

            })
                .orElse(ResponseEntity.notFound().build());
    }
}