package org.example.backendtfggeneral.controladores;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.beans.LoginRequest;
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
                .map(ok -> ok ? ResponseEntity.ok("Acceso concedido")
                        : ResponseEntity.status(401).body("Error"));
    }

    // AÑADIMOS EL {busId} A LA RUTA
    @PostMapping("/ubicacion/{busId}")
    public Mono<Void> actualizarUbicacion(@PathVariable String busId, @RequestBody Ubicacion ubicacion) {
        return conductorService.guardarPosicion(busId, ubicacion);
    }
}