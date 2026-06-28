package com.ykanji.reserveflow.repository;

import com.ykanji.reserveflow.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}