package com.example.tren.service.impl;

import com.example.tren.model.LogTransaccion;
import com.example.tren.model.OperacionLog;
import com.example.tren.repository.LogTransaccionRepository;
import com.example.tren.service.LogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    private final LogTransaccionRepository logRepository;

    public LogServiceImpl(LogTransaccionRepository logRepository) {
        this.logRepository = logRepository;
    }

    // Se une a la transacción de la operación que lo llama: si ésta falla, el log tampoco se guarda
    @Override
    @Transactional
    public void registrar(OperacionLog operacion, String entidad, Long entidadId, String detalle) {
        LogTransaccion log = new LogTransaccion(operacion, entidad, entidadId, detalle, LocalDateTime.now());
        logRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogTransaccion> listarTodos() {
        return logRepository.findAllByOrderByFechaDescIdDesc();
    }
}
