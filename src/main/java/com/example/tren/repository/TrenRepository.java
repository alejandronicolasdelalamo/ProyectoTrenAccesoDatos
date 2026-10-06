package com.example.tren.repository;

import com.example.tren.model.Tren;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrenRepository extends JpaRepository<Tren, Long> {
    boolean existsByMatricula(String matricula);
}