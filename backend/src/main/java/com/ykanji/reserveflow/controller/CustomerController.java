package com.ykanji.reserveflow.controller;

import com.ykanji.reserveflow.service.CustomerService;
import com.ykanji.reserveflow.dto.*;
import com.ykanji.reserveflow.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetailDto> getCustomer(@PathVariable Long id) {
        return customerService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CustomerListDto> searchCustomers(@RequestParam String name) {
        return customerService.searchByName(name);
    }

    @PostMapping
    public ResponseEntity<Long> createCustomer(
            @RequestBody CustomerCreateRequest request
    ) {
        Long id = customerService.createCustomer(request);
        return ResponseEntity.ok(id);
    }
}
