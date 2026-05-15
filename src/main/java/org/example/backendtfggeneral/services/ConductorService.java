package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.repositorios.ConductorRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ConductorService {

    private final ConductorRepository conductorRepository;

    // Mapa dinámico para el seguimiento en tiempo real
    // Usamos este record interno para guardar la ubicación y la línea juntas
    private final Map<String, EstadoBus> busesEnServicio = new ConcurrentHashMap<>();

    public ConductorService(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }

    // --- MÉTODOS DE VALIDACIÓN ---

    public Mono<Boolean> validar(String busId, String password) {
        return Mono.fromCallable(() -> {
            return conductorRepository.findByBusId(busId)
                    .map(conductor -> conductor.getPassword().equals(password))
                    .orElse(false);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // --- MÉTODOS DE GESTIÓN DE ESTADO (TIEMPO REAL) ---

    public void asignarLineaABus(String busId, Long idLinea) {
        // Inicializamos con una ubicación por defecto (Marchena)
        busesEnServicio.put(busId, new EstadoBus(new Ubicacion(37.33, -5.41), idLinea));
    }

    public Mono<Void> guardarPosicion(String busId, Long idLinea, Ubicacion data) {
        busesEnServicio.put(busId, new EstadoBus(data, idLinea));
        return Mono.empty();
    }

    // --- MÉTODOS DE CONSULTA (LOS QUE NECESITA EL CONTROLADOR) ---

    // Este es el que usa el GET del controlador para APEX
    public Mono<Ubicacion> obtenerUltimaUbicacion(String busId) {
        EstadoBus estado = busesEnServicio.get(busId);
        return (estado != null) ? Mono.just(estado.ubicacion()) : Mono.empty();
    }

    public Long obtenerLineaDeBus(String busId) {
        EstadoBus estado = busesEnServicio.get(busId);
        return (estado != null) ? estado.idLinea() : null;
    }

    // Tu método original que devuelve el objeto directamente
    public Ubicacion obtenerPosicionActual(String busId) {
        EstadoBus estado = busesEnServicio.get(busId);
        return (estado != null) ? estado.ubicacion() : new Ubicacion(37.33, -5.41);
    }

    // --- MÉTODOS DE FILTRADO ---

    public Map<String, Ubicacion> obtenerBusesActivosPorLinea(Long idLinea) {
        return busesEnServicio.entrySet().stream()
                .filter(e -> e.getValue().idLinea().equals(idLinea))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().ubicacion()));
    }

    public Set<Long> obtenerLineasActivas() {
        return busesEnServicio.values().stream()
                .map(EstadoBus::idLinea)
                .collect(Collectors.toSet());
    }

    // Record interno para agrupar los datos en el mapa
    private record EstadoBus(Ubicacion ubicacion, Long idLinea) {}
}