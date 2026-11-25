package com.example.tren.service;

import com.example.tren.model.LogTransaccion;
import com.example.tren.model.OperacionLog;

import java.util.List;

public interface LogService {
    void registrar(OperacionLog operacion, String entidad, Long entidadId, String detalle);
    List<LogTransaccion> listarTodos();
}
