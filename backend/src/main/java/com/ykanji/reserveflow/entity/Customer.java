package com.ykanji.reserveflow.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phoneNumber;

    private String memo;

    @OneToMany(mappedBy = "customer")
    private List<Reservation> reservations;
}