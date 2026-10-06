package com.example.tren.repository;

import com.example.tren.model.LogTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogTransaccionRepository extends JpaRepository<LogTransaccion, Long> {
    // Más recientes primero; si dos logs tienen la misma fecha, el de id mayor va antes
    List<LogTransaccion> findAllByOrderByFechaDescIdDesc();
}
