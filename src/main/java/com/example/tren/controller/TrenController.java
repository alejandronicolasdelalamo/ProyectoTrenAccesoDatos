package com.example.tren.controller;

import com.example.tren.model.Propulsion;
import com.example.tren.model.TipoServicio;
import com.example.tren.model.Tren;
import com.example.tren.service.TrenService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/trenes")
public class TrenController {

    private final TrenService trenService;

    public TrenController(TrenService trenService) {
        this.trenService = trenService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Tren> pagina = trenService.listarPaginado(PageRequest.of(page, 10));
        model.addAttribute("trenes", pagina.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pagina.getTotalPages());
        return "trenes/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("tren", new Tren());
        agregarListasDeApoyo(model);
        return "trenes/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("tren", trenService.buscarPorId(id));
        agregarListasDeApoyo(model);
        return "trenes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("tren") Tren tren, BindingResult result, Model model) {
        if (result.hasErrors()) {
            agregarListasDeApoyo(model);
            return "trenes/formulario";
        }
        trenService.guardar(tren);
        return "redirect:/trenes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        trenService.eliminar(id);
        return "redirect:/trenes";
    }

    private void agregarListasDeApoyo(Model model) {
        model.addAttribute("propulsiones", Propulsion.values());
        model.addAttribute("tiposServicio", TipoServicio.values());
    }
}