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

    private static final String SEPARADOR = " | ";
    private static final String SEPARADOR_REGEX = " \\| ";

    private final Path archivo;

    public LogServiceImpl(@Value("${app.log.ruta:logs/transacciones.log}") String ruta) throws IOException {
        this.archivo = Path.of(ruta).toAbsolutePath();
        Files.createDirectories(archivo.getParent());
    }

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
            Collections.reverse(logs);
            return logs;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo de logs: " + archivo, e);
        }
    }

    private synchronized void escribir(String linea) {
        try {
            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir en el archivo de logs: " + archivo, e);
        }
    }

    private LogTransaccion parsear(String linea) {
        String[] partes = linea.split(SEPARADOR_REGEX, 5);
        Long entidadId = partes[3].isEmpty() ? null : Long.valueOf(partes[3]);
        return new LogTransaccion(OperacionLog.valueOf(partes[1]), partes[2], entidadId,
                partes.length > 4 ? partes[4] : "", LocalDateTime.parse(partes[0]));
    }

    private String limpiar(String texto) {
        return texto == null ? "" : texto.replace("\r", " ").replace("\n", " ");
    }
}
