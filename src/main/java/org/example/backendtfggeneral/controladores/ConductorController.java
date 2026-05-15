package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.beans.LoginRequest;
import org.example.backendtfggeneral.beans.ProximaParadaDto;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.services.ConductorService;
import org.example.backendtfggeneral.repositorios.LineaParadaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/conductor")
@CrossOrigin(origins = "*")
public class ConductorController {

    private final ConductorService conductorService;
    private final LineaParadaRepository lineaParadaRepository;

    public ConductorController(ConductorService conductorService,
                               LineaParadaRepository lineaParadaRepository) {
        this.conductorService = conductorService;
        this.lineaParadaRepository = lineaParadaRepository;
    }

    /**
     * GET para Oracle APEX: Obtiene la posición y la próxima parada calculada por Spatial.
     */
    @GetMapping("/ubicacion/{busId}")
    public Mono<ResponseEntity<ProximaParadaDto>> obtenerUbicacion(@PathVariable String busId) {
        return conductorService.obtenerUltimaUbicacion(busId)
                .flatMap(ubi -> {
                    Long idLinea = conductorService.obtenerLineaDeBus(busId);

                    if (idLinea == null) {
                        return Mono.just(ResponseEntity.ok(
                                new ProximaParadaDto(ubi.getLatitud(), ubi.getLongitud(), busId, "Línea no asignada")
                        ));
                    }

                    // Ejecutamos la lógica de base de datos (bloqueante) en un hilo elástico
                    return Mono.fromCallable(() -> {
                                // 1. Buscamos el ID de la siguiente parada con Oracle Spatial
                                Long idSiguienteParada = lineaParadaRepository.encontrarSiguienteParadaId(
                                        idLinea, ubi.getLatitud(), ubi.getLongitud());

                                if (idSiguienteParada != null) {
                                    // 2. Obtenemos el nombre humano de la parada
                                    String nombreParada = lineaParadaRepository.encontrarNombreParada(idSiguienteParada, idLinea)
                                            .orElse("Parada ID: " + idSiguienteParada);

                                    return new ProximaParadaDto(ubi.getLatitud(), ubi.getLongitud(), busId, nombreParada);
                                }

                                return new ProximaParadaDto(ubi.getLatitud(), ubi.getLongitud(), busId, "Fin de trayecto");
                            })
                            .subscribeOn(Schedulers.boundedElastic()) // Evita bloquear el hilo principal de Netty
                            .map(ResponseEntity::ok);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * POST: El conductor envía su ubicación desde la App móvil.
     */
    @PostMapping("/ubicacion/{busId}")
    public Mono<ResponseEntity<Void>> actualizarUbicacion(@PathVariable String busId, @RequestBody Ubicacion ubicacion) {
        System.out.println(">>> Recibida ubicación para Bus: " + busId);
        Long idLinea = conductorService.obtenerLineaDeBus(busId);

        if (idLinea != null) {
            return conductorService.guardarPosicion(busId, idLinea, ubicacion)
                    .then(Mono.just(ResponseEntity.ok().build()));
        } else {
            System.err.println("   [ERROR] Actualización denegada: Bus sin Login.");
            return Mono.just(ResponseEntity.status(403).build());
        }
    }

    /**
     * POST: Login del conductor para asignar línea y habilitar el rastreo.
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequest req) {
        return conductorService.validar(req.getBusId(), req.getPassword())
                .map(ok -> {
                    if (ok) {
                        conductorService.asignarLineaABus(req.getBusId(), req.getIdLinea());
                        return ResponseEntity.ok("Acceso concedido");
                    }
                    return ResponseEntity.status(401).body("Error de credenciales");
                });
    }
}