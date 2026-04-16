package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.repositorios.ConductorRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConductorService {

    private final ConductorRepository conductorRepository;

    // AQUI ESTÁ LA MAGIA: Un mapa que asocia "bus-101" -> Ubicacion
    private final Map<String, Ubicacion> ubicacionesBuses = new ConcurrentHashMap<>();

    public ConductorService(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }

    public Mono<Boolean> validar(String busId, String password) {
        return Mono.fromCallable(() -> {
                    return conductorRepository.findByBusId(busId)
                            .map(c -> c.getPassword().equals(password))
                            .orElse(false);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    // AHORA RECIBE EL ID DEL BUS
    public Mono<Void> guardarPosicion(String busId, Ubicacion data) {
        System.out.println("🛰️ GPS Actualizado - BUS: " + busId + " | Lat: " + data.getLatitud() + " | Lon: " + data.getLongitud());
        ubicacionesBuses.put(busId, data); // Lo guardamos en el casillero de ese bus
        return Mono.empty();
    }

    // LA IA USARÁ ESTO PIDIENDO EL ID
    public Ubicacion obtenerPosicionActual(String busId) {
        // Devuelve la ubicación de ese bus, o una por defecto si aún no ha encendido el GPS
        return ubicacionesBuses.getOrDefault(busId, new Ubicacion(37.33, -5.41));
    }
}