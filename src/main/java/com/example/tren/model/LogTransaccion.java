package com.example.tren.model;

import java.time.LocalDateTime;

public record LogTransaccion(
        OperacionLog operacion,
        String entidad,
        Long entidadId,
        String detalle,
        LocalDateTime fecha) {
}
