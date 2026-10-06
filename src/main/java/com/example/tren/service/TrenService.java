package com.example.tren.service;

import com.example.tren.model.Tren;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TrenService {
    List<Tren> listarTodos();
    Page<Tren> listarPaginado(Pageable pageable);
    Tren guardar(Tren tren);
    Tren buscarPorId(Long id);
    void eliminar(Long id);
    boolean existePorMatricula(String matricula);
}