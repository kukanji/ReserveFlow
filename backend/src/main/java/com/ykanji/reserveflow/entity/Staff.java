package com.ykanji.reserveflow.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "staffs")
public class Staff extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "staff")
    private List<Reservation> reservations;
}