package com.ykanji.reserveflow.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "menus")
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer durationMinutes;

    private Integer price;

    @OneToMany(mappedBy = "menu")
    private List<Reservation> reservations;

    protected Menu() {
    }

    public Menu(String name, Integer durationMinutes, Integer price) {
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Integer getPrice() {
        return price;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}