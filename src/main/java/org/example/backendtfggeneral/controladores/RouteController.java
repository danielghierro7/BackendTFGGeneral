package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.beans.BusLlegadaDTO;
import org.example.backendtfggeneral.beans.ParadaTiempoDTO;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.procesos.CalcularTiempoRestanteAParada;
import org.example.backendtfggeneral.services.LineaParadaService;
import org.example.backendtfggeneral.entidades.LineaParada;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
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


    //Este es el de sacar cuanto le queda al bus para llegar
    @GetMapping(value = "/tiempos-flujo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<ParadaTiempoDTO>> obtenerTiemposRealTime(@RequestParam Long idLineaBus,
                                                              @RequestParam double lat1,
                                                              @RequestParam double lon1) {

        return Flux.interval(Duration.ZERO, Duration.ofMinutes(3))
                .flatMap(tick -> {
                    // 1. Obtenemos las entidades LineaParada (traen la Parada dentro)
                    List<LineaParada> listaRelacion = lineaParadaService.obtenerRutaPorIdLinea(idLineaBus);

                    // 2. Calculamos los tiempos (Mono<List<Integer>>)
                    return motorCalculo.calcularTiempoRestanteAVariasParadas(
                            new org.example.backendtfggeneral.beans.Ubicacion(lat1, lon1),
                            listaRelacion
                    ).map(tiempos -> {
                        // 3. Cruzamos los datos
                        List<ParadaTiempoDTO> respuesta = new ArrayList<>();
                        for (int i = 0; i < listaRelacion.size(); i++) {
                            LineaParada lp = listaRelacion.get(i);
                            String nombre = lp.getParada().getNombre();
                            Integer tiempo = (i < tiempos.size()) ? tiempos.get(i) : -1;

                            respuesta.add(new ParadaTiempoDTO(nombre, tiempo, lp.getOrden()));
                        }
                        return respuesta;
                    });
                })
                .share() // Compartir el flujo entre todos los usuarios
                .log(); //mas que nada para meterle un log
    }
    @GetMapping(value = "/parada-tiempos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<BusLlegadaDTO>> obtenerBusesPorParada(@RequestParam Long idParada) {

        return Flux.interval(Duration.ZERO, Duration.ofMinutes(3))
                .flatMap(tick -> {
                    // 1. Buscamos qué líneas pasan por esta parada (Usando tu repositorio corregido)
                    List<LineaParada> lineasQuePasan = lineaParadaService.obtenerLineasPorParada(idParada);

                    // 2. Convertimos la lista de líneas en un flujo para calcular tiempos
                    return Flux.fromIterable(lineasQuePasan)
                            .flatMap(lp -> {
                                // SIMULACIÓN: Como no tenemos busService, creamos una ubicación falsa
                                // Puedes poner una lat/lon fija o que cambie un poco con el 'tick'
                                org.example.backendtfggeneral.beans.Ubicacion busUbicFalsa =
                                        new org.example.backendtfggeneral.beans.Ubicacion(37.38, -5.98);

                                // 3. Calculamos el tiempo real entre esa ubicación falsa y la parada real
                                return motorCalculo.calcularTiempoRestanteEntrePuntos(busUbicFalsa, lp.getParada().getUbicacion())
                                        .map(tiempo -> new BusLlegadaDTO(
                                                lp.getLinea().getNombreLinea(),
                                                tiempo,
                                                "Destino Simulado"
                                        ));
                            })
                            .collectList()
                            .map(lista -> {
                                // 4. ORDENAR: Los que llegan antes, arriba
                                lista.sort(Comparator.comparingInt(BusLlegadaDTO::getMinutosRestantes)); //ESTO ES CODIGO LIMPIO
                                return lista;
                            });
                })
                .share()
                .log();
    }








}