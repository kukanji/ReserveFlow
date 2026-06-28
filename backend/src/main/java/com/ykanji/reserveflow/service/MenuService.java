package com.ykanji.reserveflow.service;

import com.ykanji.reserveflow.dto.MenuListDto;
import com.ykanji.reserveflow.repository.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuListDto> findMenuList() {

        return menuRepository.findAll()
                .stream()
                .map(menu ->
                        new MenuListDto(
                                menu.getId(),
                                menu.getName(),
                                menu.getDurationMinutes(),
                                menu.getPrice()
                        )
                )
                .toList();
    }
}