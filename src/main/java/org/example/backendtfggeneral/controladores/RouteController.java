package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.beans.BusLlegadaDTO;
import org.example.backendtfggeneral.beans.ParadaTiempoDTO;
import org.example.backendtfggeneral.services.ConductorService;
import org.example.backendtfggeneral.services.LineaParadaService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/ruta")
public class RouteController {

    private final LineaParadaService lineaParadaService;
    private final ConductorService conductorService; // <--- Inyectamos esto

    public RouteController(LineaParadaService lineaParadaService,
                           ConductorService conductorService) {
        this.lineaParadaService = lineaParadaService;
        this.conductorService = conductorService;
    }

    // 1. ELIMINADO: Ya no actualizamos posición desde aquí,
    // lo hacemos desde ConductorController por ID.

    // 2. Tiempos para el mapa del pasajero
    @GetMapping(value = "/tiempos-flujo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<ParadaTiempoDTO>> obtenerTiemposRealTime(@RequestParam Long idLineaBus) {
        // Ejemplo: Si el pasajero mira la línea 140,
        // podrías buscar qué bus tiene asignada esa línea
        return lineaParadaService.generarFlujoTiemposRealTime(idLineaBus);
    }

    // 3. Tiempos por parada
    @GetMapping(value = "/parada-tiempos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<BusLlegadaDTO>> obtenerBusesPorParada(@RequestParam Long idParada) {
        return lineaParadaService.obtenerBusesPorParadaFlujo(idParada);
    }



}