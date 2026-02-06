package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.entidades.Ciudad;
import org.example.backendtfggeneral.services.CiudadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/ciudad")
public class CiudadController {

    @Autowired
    private CiudadService ciudadService;



    @GetMapping("/")
    public List<Ciudad> obtenerTodasCiudades() {
        return ciudadService.obtenerTodas();
    }
}
