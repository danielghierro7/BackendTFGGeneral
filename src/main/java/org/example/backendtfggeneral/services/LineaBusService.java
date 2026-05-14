package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.LineaBus;
import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.procesos.CalcularTiempoRestanteAParada;
import org.example.backendtfggeneral.repositorios.LineaBusRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class LineaBusService {

    private final LineaBusRepository lineaBusRepository;
    private final CalcularTiempoRestanteAParada calculador;

    public LineaBusService(LineaBusRepository lineaBusRepository,
                           CalcularTiempoRestanteAParada calculador) {
        this.lineaBusRepository = lineaBusRepository;
        this.calculador = calculador;
    }

    // Tu método original para devolver la lista estática
    public List<Parada> devolverParadasDeLineaBus(long idLineaBus) {
        LineaBus lineaBus = lineaBusRepository.findById(idLineaBus)
                .orElseThrow(() -> new RuntimeException("LineaBus no encontrado"));
        return lineaBus.getParadas();
    }

    /**
     * MÉTODO ACTUALIZADO:
     * Ya no calcula distancias manualmente.
     * Delega todo el trabajo inteligente al calculador que usa Oracle Spatial.
     */
    public Mono<List<Integer>> obtenerPrediccionTiempos(long idLineaBus, Ubicacion ubicacionBus) {
        // Simplemente llamamos al proceso que ya sabe identificar la parada por LRS
        // y calcular los tiempos acumulados con la API.
        return calculador.calcularTiempoRestanteAVariasParadas(idLineaBus, ubicacionBus);
    }
}