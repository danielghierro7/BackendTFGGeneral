package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.procesos.CalcularTiempoRestanteAParada;
import org.example.backendtfggeneral.services.LineaParadaService;
import org.example.backendtfggeneral.entidades.LineaParada;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/ruta")
public class RouteController {

    private final CalcularTiempoRestanteAParada motorCalculo;
    private final LineaParadaService lineaParadaService;

    public RouteController(CalcularTiempoRestanteAParada motorCalculo, LineaParadaService lineaParadaService) {
        this.motorCalculo = motorCalculo;
        this.lineaParadaService = lineaParadaService;
    }

    @GetMapping(value = "/tiempos-flujo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<Integer>> obtenerTiemposRealTime(@RequestParam Long idLineaBus,
                                                      @RequestParam double lat1,
                                                      @RequestParam double lon1) {

        // 1. Creamos el latido (cada 2 minutos)
        return Flux.interval(Duration.ZERO, Duration.ofMinutes(3))
                .flatMap(tick -> {
                    // 2. Ejecutamos tu lógica actual de cálculo
                    List<LineaParada> paradas = lineaParadaService.obtenerRutaPorIdLinea(idLineaBus);

                    // Invocamos tu Mono y lo metemos en el flujo
                    return motorCalculo.calcularTiempoRestanteAVariasParadas(
                            new org.example.backendtfggeneral.beans.Ubicacion(lat1, lon1),
                            paradas);
                })
                // 3. MULTICASTING: Si 10 personas piden la misma línea,
                // se comparte el mismo cálculo para no saturar la API de ORS
                .share()
                .log(); // Opcional: para ver en la consola de IntelliJ cuándo se envían datos
    }
}