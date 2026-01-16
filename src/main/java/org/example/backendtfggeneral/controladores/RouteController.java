package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/ruta")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @GetMapping("/tiempos")
    public Mono<List<Double>> obtenerTiempos(
            @RequestParam double latBus,
            @RequestParam double lonBus
    ) {
        Ubicacion bus = new Ubicacion(latBus, lonBus);
        List<Ubicacion> paradas = ... // obtenlas de tu BD o hardcodeadas
        return rutaService.calcularTiemposParaParadas(bus, paradas)
                .collectList(); // convierte Flux<Double> en Mono<List<Double>>
    }
}
