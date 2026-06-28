package com.ykanji.reserveflow.service;

import com.ykanji.reserveflow.dto.CustomerDetailDto;
import com.ykanji.reserveflow.repository.CustomerRepository;
import org.springframework.stereotype.Service;

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
}