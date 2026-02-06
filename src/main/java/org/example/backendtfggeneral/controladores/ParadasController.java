package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.services.ParadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/paradas")
public class ParadasController {

    @Autowired
    private ParadaService paradaService;

    @GetMapping("/todas")
    public List<Parada> getTodas() {
        return paradaService.obtenerTodasLasParadas();
    }

    @GetMapping("/{id}")
    public List<Parada> obtenerParadaPorIdCiudad(@PathVariable("id") Long id) {
        return paradaService.getParadasByIdCiudad(id);
    }


}