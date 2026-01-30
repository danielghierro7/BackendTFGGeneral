package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.LineaParada;
import org.example.backendtfggeneral.entidades.LineaParadaId;
import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.repositorios.LineaBusRepository;
import org.example.backendtfggeneral.repositorios.LineaParadaRepository;
import org.example.backendtfggeneral.repositorios.ParadaRepository;
import org.example.backendtfggeneral.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/ruta")
public class RouteController {

    private final RouteService routaService;
    private final ParadaService paradaService;
    private final LineaBusService lineaBusService;
    private final TiempoAParada tiempoAParada;
    private final LineaParadaService lineaParadaService;

    public RouteController(RouteService rutaService, ParadaService paradaService, LineaBusService lineaBusService, TiempoAParada tiempoAParada, LineaBusService lineaParadaId, LineaParadaService lineaParadaService) {
        this.routaService = rutaService;
        this.paradaService = paradaService;
        this.lineaBusService = lineaBusService;
        this.tiempoAParada = tiempoAParada;
        this.lineaParadaService= lineaParadaService;
    }

    @GetMapping("/tiempos")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<Integer>> obtenerTiempos(@RequestParam Long idLineaBus,
            @RequestParam double lat1,
            @RequestParam double lon1) {


        Ubicacion ubicacionBus = new Ubicacion(lat1, lon1);
        List<LineaParada> paradas=lineaParadaService.obtenerRutaPorIdLinea(140L);
        return tiempoAParada.calcularTiempoRestanteAParadas(ubicacionBus,paradas);


    }
}