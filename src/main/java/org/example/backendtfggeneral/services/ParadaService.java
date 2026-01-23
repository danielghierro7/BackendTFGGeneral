package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.repositorios.ParadaRepository;
import org.springframework.stereotype.Service;

@Service
public class ParadaService {

    private ParadaRepository paradaRepository;
    public ParadaService(ParadaRepository paradaRepository) {
        this.paradaRepository = paradaRepository;
    }



}
