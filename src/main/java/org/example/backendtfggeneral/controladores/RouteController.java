package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.beans.BusLlegadaDTO;
import org.example.backendtfggeneral.beans.ParadaTiempoDTO;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.services.LineaParadaService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/ruta")
public class RouteController {

    private final LineaParadaService lineaParadaService;

    public RouteController(LineaParadaService lineaParadaService) {
        this.lineaParadaService = lineaParadaService;
    }

    // 1. El conductor sigue mandando aquí, pero el controlador delega al service
    @PostMapping("/actualizar-posicion-bus")
    public Mono<Void> actualizarPosicion(@RequestBody Ubicacion nueva) {
        lineaParadaService.actualizarPosicion(nueva);
        return Mono.empty();
    }

    // 2. Mantenemos el flujo para el mapa del pasajero
    @GetMapping(value = "/tiempos-flujo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<ParadaTiempoDTO>> obtenerTiemposRealTime(@RequestParam Long idLineaBus) {
        // Llamamos al método lógico que ahora vive en el Service
        return lineaParadaService.generarFlujoTiemposRealTime(idLineaBus);
    }

    // 3. Mantenemos el flujo por parada específica
    @GetMapping(value = "/parada-tiempos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<BusLlegadaDTO>> obtenerBusesPorParada(@RequestParam Long idParada) {
        // Delegamos la gestión de la caché y el flujo al Service
        return lineaParadaService.obtenerBusesPorParadaFlujo(idParada);
    }
}