package com.ykanji.reserveflow.dto;

public class MenuListDto {

    private Long id;

    private String name;

    private Integer durationMinutes;

    private Integer price;

    public MenuListDto(
            Long id,
            String name,
            Integer durationMinutes,
            Integer price
    ) {
        this.id = id;
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
}