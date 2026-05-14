package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.beans.LoginRequest;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.services.ConductorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/conductor")
@CrossOrigin(origins = "*")
public class ConductorController {

    private final ConductorService conductorService;

    public ConductorController(ConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequest req) {
        return conductorService.validar(req.getBusId(), req.getPassword())
                .map(ok -> {
                    if (ok) {
                        // Importante: Registramos la línea que el bus va a realizar
                        conductorService.asignarLineaABus(req.getBusId(), req.getIdLinea());
                        return ResponseEntity.ok("Acceso concedido");
                    }
                    return ResponseEntity.status(401).body("Error de credenciales");
                });
    }

    @PostMapping("/ubicacion/{busId}")
    public Mono<Void> actualizarUbicacion(@PathVariable String busId, @RequestBody Ubicacion ubicacion) {
        // 1. Buscamos qué línea tiene asignada este bus en el mapa de memoria
        Long idLinea = conductorService.obtenerLineaDeBus(busId);

        // 2. Si el bus tiene una línea asignada, guardamos la posición con ese ID
        if (idLinea != null) {
            return conductorService.guardarPosicion(busId, idLinea, ubicacion);
        }

        // Si el bus no está registrado (no hizo login), no hacemos nada
        return Mono.empty();
    }
}