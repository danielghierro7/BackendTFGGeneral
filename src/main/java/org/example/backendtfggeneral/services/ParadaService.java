package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.repositorios.ParadaRepository;

public class ParadaService {

    private ParadaRepository paradaRepository;
    public ParadaService(ParadaRepository paradaRepository) {
        this.paradaRepository = paradaRepository;
    }
}
