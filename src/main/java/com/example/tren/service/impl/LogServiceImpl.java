package com.example.tren.service.impl;

import com.example.tren.model.LogTransaccion;
import com.example.tren.model.OperacionLog;
import com.example.tren.service.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    // Formato de cada línea: fecha | operacion | entidad | entidadId | detalle
    private static final String SEPARADOR = " | ";
    private static final String SEPARADOR_REGEX = " \\| ";

    private final Path archivo;

    public LogServiceImpl(@Value("${app.log.ruta:logs/transacciones.log}") String ruta) throws IOException {
        this.archivo = Path.of(ruta).toAbsolutePath();
        Files.createDirectories(archivo.getParent());
    }

    // Si hay una transacción en curso, la línea se escribe solo cuando ésta hace commit:
    // así, si la operación falla y se revierte, no queda rastro en el archivo
    @Override
    public void registrar(OperacionLog operacion, String entidad, Long entidadId, String detalle) {
        String linea = LocalDateTime.now() + SEPARADOR
                + operacion + SEPARADOR
                + entidad + SEPARADOR
                + (entidadId == null ? "" : entidadId) + SEPARADOR
                + limpiar(detalle);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    escribir(linea);
                }
            });
        } else {
            escribir(linea);
        }
    }

    // Lee el archivo y devuelve los logs con los más recientes primero
    @Override
    public List<LogTransaccion> listarTodos() {
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try {
            List<LogTransaccion> logs = new ArrayList<>();
            for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                if (!linea.isBlank()) {
                    logs.add(parsear(linea));
                }
            }
            // El archivo se escribe siempre al final, así que invertir el orden da los más recientes primero
            Collections.reverse(logs);
            return logs;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo de logs: " + archivo, e);
        }
    }

    // synchronized: evita que dos peticiones simultáneas mezclen sus líneas
    private synchronized void escribir(String linea) {
        try {
            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir en el archivo de logs: " + archivo, e);
        }
    }

    private LogTransaccion parsear(String linea) {
        // El límite de 5 partes deja intacto el detalle aunque contenga " | "
        String[] partes = linea.split(SEPARADOR_REGEX, 5);
        Long entidadId = partes[3].isEmpty() ? null : Long.valueOf(partes[3]);
        return new LogTransaccion(OperacionLog.valueOf(partes[1]), partes[2], entidadId,
                partes.length > 4 ? partes[4] : "", LocalDateTime.parse(partes[0]));
    }

    // Un salto de línea dentro del detalle rompería el formato de una línea por log
    private String limpiar(String texto) {
        return texto == null ? "" : texto.replace("\r", " ").replace("\n", " ");
    }
}
