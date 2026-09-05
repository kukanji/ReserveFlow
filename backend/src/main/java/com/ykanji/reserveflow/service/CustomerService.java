package com.ykanji.reserveflow.service;

import com.ykanji.reserveflow.dto.*;
import com.ykanji.reserveflow.entity.*;
import com.ykanji.reserveflow.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<CustomerDetailDto> findById(Long id) {

        return customerRepository.findById(id)
                .map(customer ->
                        new CustomerDetailDto(
                                customer.getId(),
                                customer.getName(),
                                customer.getPhoneNumber(),
                                customer.getMemo(),
                                null,
                                null
                        )
                );
    }

    public Long createCustomer(CustomerCreateRequest request) {

        Customer customer = new Customer(
                request.getName(),
                request.getPhoneNumber(),
                request.getMemo()
        );

        return customerRepository.save(customer).getId();
    }

    public List<CustomerListDto> searchByName(String name) {

        return customerRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(customer -> new CustomerListDto(
                        customer.getId(),
                        customer.getName(),
                        customer.getPhoneNumber()
                ))
                .toList();
    }
}
