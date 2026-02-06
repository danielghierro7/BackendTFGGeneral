package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.entidades.Ciudad;
import org.springframework.cache.annotation.Cacheable;
import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.repositorios.ParadaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParadaService {

    private ParadaRepository paradaRepository;
    public ParadaService(ParadaRepository paradaRepository) {
        this.paradaRepository = paradaRepository;
    }



    public List<Parada> getParadasByIdCiudad(Long idCiudad) {
        return paradaRepository.findParadaByCiudadId(idCiudad);
    }

    @Cacheable(value = "paradas")
    public List<Parada> obtenerTodasLasParadas() {
        System.out.println("--- ACCEDIENDO A BASE DE DATOS PARA CARGAR PARADAS ---");
        return paradaRepository.findAll();
    }

}
