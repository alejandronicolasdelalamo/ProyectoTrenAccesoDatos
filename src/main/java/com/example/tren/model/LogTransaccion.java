package com.example.tren.model;

import java.time.LocalDateTime;

// Una operación (crear, actualizar o eliminar) hecha sobre una entidad; se guarda como una línea del archivo de logs
public record LogTransaccion(
        OperacionLog operacion,
        // Nombre de la entidad afectada, por ejemplo "Tren"
        String entidad,
        Long entidadId,
        String detalle,
        LocalDateTime fecha) {
}
