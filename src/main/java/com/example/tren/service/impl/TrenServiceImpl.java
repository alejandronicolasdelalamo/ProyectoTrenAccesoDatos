package com.example.tren.service.impl;

import com.example.tren.exception.TrenNoEncontradoException;
import com.example.tren.model.Tren;
import com.example.tren.repository.TrenRepository;
import com.example.tren.service.TrenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrenServiceImpl implements TrenService {

    private final TrenRepository trenRepository;

    public TrenServiceImpl(TrenRepository trenRepository) {
        this.trenRepository = trenRepository;
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
    public Tren guardar(Tren tren) {
        return trenRepository.save(tren);
    }

    @Override
    public Tren buscarPorId(Long id) {
        return trenRepository.findById(id)
                .orElseThrow(() -> new TrenNoEncontradoException("Tren no encontrado con ID: " + id));
    }

    @Override
    public void eliminar(Long id) {
        if (!trenRepository.existsById(id)) {
            throw new TrenNoEncontradoException("Tren no encontrado con ID: " + id);
        }
        trenRepository.deleteById(id);
    }

    @Override
    public boolean existePorMatricula(String matricula) {
        return trenRepository.existsByMatricula(matricula);
    }
}