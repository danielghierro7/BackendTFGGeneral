package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.entidades.LineaBus;
import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.repositorios.LineaBusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LineaBusService {

    private final LineaBusRepository lineaBusRepository;
    public LineaBusService(LineaBusRepository lineaBusRepository) {
        this.lineaBusRepository = lineaBusRepository;
    }

    public List<Parada> devolverParadasDeLineaBus(long idLineaBus) {
        LineaBus lineaBus =lineaBusRepository.findById(idLineaBus).orElseThrow(() -> new RuntimeException("LineaBus no encontrado"));

        return lineaBus.getParadas();
    }


}
