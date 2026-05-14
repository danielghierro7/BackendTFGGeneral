package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.repositorios.ConductorRepository; // Asegúrate de que existe
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ConductorService {

    // Necesitamos el repositorio para consultar la base de datos
    private final ConductorRepository conductorRepository;

    // Mapa dinámico para el seguimiento en tiempo real
    private final Map<String, EstadoBus> busesEnServicio = new ConcurrentHashMap<>();

    // Constructor para la inyección de dependencias
    public ConductorService(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }

    /**
     * Valida las credenciales del conductor en la BD de forma reactiva.
     */
    public Mono<Boolean> validar(String busId, String password) {
        return Mono.fromCallable(() -> {
            return conductorRepository.findByBusId(busId)
                    .map(conductor -> conductor.getPassword().equals(password))
                    .orElse(false);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 1. Método para que el conductor "ficho" en una línea al empezar
    public void asignarLineaABus(String busId, Long idLinea) {
        // Inicializamos con una ubicación por defecto hasta que llegue el primer GPS
        busesEnServicio.put(busId, new EstadoBus(new Ubicacion(37.33, -5.41), idLinea));
    }

    // 2. Guardar posición actualizando también el ID de línea que está operando
    public Mono<Void> guardarPosicion(String busId, Long idLinea, Ubicacion data) {
        busesEnServicio.put(busId, new EstadoBus(data, idLinea));
        return Mono.empty();
    }

    // 3. Obtener el ID de la línea que está haciendo un bus concreto
    public Long obtenerLineaDeBus(String busId) {
        EstadoBus estado = busesEnServicio.get(busId);
        return (estado != null) ? estado.idLinea() : null;
    }

    public Ubicacion obtenerPosicionActual(String busId) {
        EstadoBus estado = busesEnServicio.get(busId);
        return (estado != null) ? estado.ubicacion() : new Ubicacion(37.33, -5.41);
    }

    // 4. Obtener todos los buses que están haciendo una línea específica
    public Map<String, Ubicacion> obtenerBusesActivosPorLinea(Long idLinea) {
        return busesEnServicio.entrySet().stream()
                .filter(e -> e.getValue().idLinea().equals(idLinea))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().ubicacion()));
    }

    // 5. Extrae los IDs de línea únicos que tienen actividad
    public Set<Long> obtenerLineasActivas() {
        return busesEnServicio.values().stream()
                .map(EstadoBus::idLinea)
                .collect(Collectors.toSet());
    }
}