package com.example.tren.controller;

import com.example.tren.model.LogTransaccion;
import com.example.tren.service.LogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogRestController {

    private final LogService logService;

    public LogRestController(LogService logService) {
        this.logService = logService;
    }

    // Devuelve todos los logs en JSON, los más recientes primero
    @GetMapping
    public List<LogTransaccion> listar() {
        return logService.listarTodos();
    }
}
