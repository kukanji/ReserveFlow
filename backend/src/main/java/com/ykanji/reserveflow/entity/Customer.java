package com.ykanji.reserveflow.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
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
    private List<Reservation> reservations = new ArrayList<>();

    // JPAが使用するため
    protected Customer() {
    }

    // 必要に応じて使用するコンストラクタ
    public Customer(String name, String phoneNumber, String memo) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.memo = memo;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMemo() {
        return memo;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}