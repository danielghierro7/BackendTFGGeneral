package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.entidades.LineaBus;
import org.example.backendtfggeneral.entidades.LineaParada;
import org.example.backendtfggeneral.entidades.LineaParadaId;
import org.example.backendtfggeneral.repositorios.LineaParadaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LineaParadaService {

    private final LineaParadaRepository lineaParadaRepository;
    public LineaParadaService(LineaParadaRepository lineaParadaRepository) {
        this.lineaParadaRepository = lineaParadaRepository;
    }

    public List<LineaParada> obtenerRutaPorIdLinea(Long idLineaBus) {
        return lineaParadaRepository.findById_IdLineaOrderByOrdenAsc(idLineaBus);
    }


    public List<LineaParada> obtenerLineasPorParada(Long idParada) {
        // Esto nos devuelve todas las líneas que tienen esta parada en su ruta
        return lineaParadaRepository.findById_IdParada(idParada);
    }
}
