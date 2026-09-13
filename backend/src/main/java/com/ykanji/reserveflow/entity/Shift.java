package com.ykanji.reserveflow.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "shifts")
public class Shift extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    private LocalDate workDate;

    @Column(name = "is_off")
    private boolean off;

    private LocalTime startTime;

    private LocalTime endTime;

    private String task;

    // JPA用
    protected Shift() {
    }

    public Shift(
            Staff staff,
            LocalDate workDate,
            boolean off,
            LocalTime startTime,
            LocalTime endTime,
            String task
    ) {
        this.staff = staff;
        this.workDate = workDate;
        this.off = off;
        this.startTime = startTime;
        this.endTime = endTime;
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public Staff getStaff() {
        return staff;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public boolean isOff() {
        return off;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getTask() {
        return task;
    }

    public void setOff(boolean off) {
        this.off = off;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
