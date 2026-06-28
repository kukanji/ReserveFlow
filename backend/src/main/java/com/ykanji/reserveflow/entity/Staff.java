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

    protected Staff() {
    }

    public Staff(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}