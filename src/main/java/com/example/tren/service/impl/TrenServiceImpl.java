package com.example.tren.service.impl;

import com.example.tren.exception.TrenNoEncontradoException;
import com.example.tren.model.OperacionLog;
import com.example.tren.model.Tren;
import com.example.tren.repository.TrenRepository;
import com.example.tren.service.LogService;
import com.example.tren.service.TrenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrenServiceImpl implements TrenService {

    private final TrenRepository trenRepository;
    private final LogService logService;

    public TrenServiceImpl(TrenRepository trenRepository, LogService logService) {
        this.trenRepository = trenRepository;
        this.logService = logService;
    }

    @Override
    public List<Tren> listarTodos() {
        return trenRepository.findAll();
    }

    @Override
    public Page<Tren> listarPaginado(Pageable pageable) {
        return trenRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Tren guardar(Tren tren) {
        boolean esNuevo = (tren.getId() == null);
        Tren guardado = trenRepository.save(tren);

        OperacionLog operacion;
        if (esNuevo) {
            operacion = OperacionLog.CREAR;
        } else {
            operacion = OperacionLog.ACTUALIZAR;
        }
        logService.registrar(operacion, "Tren", guardado.getId(), descripcion(guardado));
        return guardado;
    }

    @Override
    public Tren buscarPorId(Long id) {
        return trenRepository.findById(id)
                .orElseThrow(() -> new TrenNoEncontradoException("Tren no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!trenRepository.existsById(id)) {
            throw new TrenNoEncontradoException("Tren no encontrado con ID: " + id);
        }
        Tren tren = trenRepository.findById(id).orElse(null);
        trenRepository.deleteById(id);
        logService.registrar(OperacionLog.ELIMINAR, "Tren", id, descripcion(tren));
    }

    @Override
    public boolean existePorMatricula(String matricula) {
        return trenRepository.existsByMatricula(matricula);
    }

    private String descripcion(Tren tren) {
        if (tren == null) {
            return "Tren eliminado";
        }
        return tren.getFabricante() + " " + tren.getModelo() + " (matrícula " + tren.getMatricula() + ")";
    }
}
