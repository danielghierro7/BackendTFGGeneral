package org.example.backendtfggeneral.services;

import dev.langchain4j.agent.tool.Tool;
import org.example.backendtfggeneral.beans.BusLlegadaDTO;
import org.example.backendtfggeneral.beans.ParadaTiempoDTO;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.LineaParada;
import org.example.backendtfggeneral.procesos.CalcularTiempoRestanteAParada;
import org.example.backendtfggeneral.repositorios.LineaParadaRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class LineaParadaService {
    private final LineaParadaRepository lineaParadaRepository;
    private final CalcularTiempoRestanteAParada motorCalculo;

    // 1. INYECTAMOS EL SERVICIO DE CONDUCTORES
    private final ConductorService conductorService;

    private final java.util.Map<Long, Flux<List<BusLlegadaDTO>>> flujosPorParada = new java.util.concurrent.ConcurrentHashMap<>();

    // 2. ACTUALIZAMOS EL CONSTRUCTOR
    public LineaParadaService(LineaParadaRepository repo,
                              CalcularTiempoRestanteAParada motor,
                              ConductorService conductorService) {
        this.lineaParadaRepository = repo;
        this.motorCalculo = motor;
        this.conductorService = conductorService;
    }

    // --- MÉTODOS DE LÓGICA REACTIVA ---

    // Borramos el método actualizarPosicion(Ubicacion nueva) porque ahora
    // se encarga el ConductorService mediante el ConductorController.

    public Flux<List<ParadaTiempoDTO>> generarFlujoTiemposRealTime(Long idLineaBus) {
        // Reducimos el tiempo a 10 segundos para que el mapa sea fluido
        return Flux.interval(Duration.ZERO, Duration.ofSeconds(10))
                .flatMap(tick -> {
                    List<LineaParada> listaRelacion = lineaParadaRepository.findById_IdLineaOrderByOrdenAsc(idLineaBus);

                    // 3. OBTENEMOS LA POSICIÓN POR ID (Ejemplo: bus-101)
                    // En un futuro, podrías buscar en la DB qué busId está asignado a esta idLineaBus
                    Ubicacion posicionActualDelBus = conductorService.obtenerPosicionActual("bus-101");

                    return motorCalculo.calcularTiempoRestanteAVariasParadas(posicionActualDelBus, listaRelacion).map(tiempos -> {
                        List<ParadaTiempoDTO> respuesta = new ArrayList<>();
                        for (int i = 0; i < listaRelacion.size(); i++) {
                            LineaParada lp = listaRelacion.get(i);
                            Integer tiempo = (i < tiempos.size()) ? tiempos.get(i) : -1;
                            respuesta.add(new ParadaTiempoDTO(lp.getParada().getNombre(), tiempo, lp.getOrden()));
                        }
                        return respuesta;
                    });
                })
                .replay(1).refCount();
    }


    public Flux<List<BusLlegadaDTO>> obtenerBusesPorParadaFlujo(Long idParada) {
        return flujosPorParada.computeIfAbsent(idParada, id ->
                Flux.interval(Duration.ZERO, Duration.ofSeconds(10))
                        .flatMap(tick -> {
                            // Buscamos todas las líneas que pasan por esta parada
                            List<LineaParada> lineasQuePasan = lineaParadaRepository.findById_IdParada(id);

                            return Flux.fromIterable(lineasQuePasan)
                                    .flatMap(lp -> {
                                        // Obtenemos la ubicación del bus (bus-101 de ejemplo)
                                        Ubicacion busPos = conductorService.obtenerPosicionActual("bus-101");

                                        // Calculamos el tiempo entre el bus y esta parada específica
                                        return motorCalculo.calcularTiempoRestanteEntrePuntos(busPos, lp.getParada().getUbicacion())
                                                .onErrorResume(e -> Mono.just(-1))
                                                .map(tiempo -> new BusLlegadaDTO(
                                                        lp.getLinea().getNombreLinea(),
                                                        tiempo,
                                                        lp.getLinea().getCiudadDestino() != null ?
                                                                lp.getLinea().getCiudadDestino().getNombre() : "Destino Final"  ));
                                    })
                                    .collectList();
                        })
                        .replay(1).refCount()
                        .doFinally(signal -> flujosPorParada.remove(id))
        );
    }

    // --- MÉTODOS @TOOL (Para la IA) ---

    @Tool("Devuelve la ubicación actual del autobús usando su identificador (ej: 'bus-101')")
    public String obtenerUbicacionBusParaIA(String busId) {
        Ubicacion u = conductorService.obtenerPosicionActual(busId);
        return "El bus " + busId + " está en Lat: " + u.getLatitud() + ", Lon: " + u.getLongitud();
    }

    @Tool("Calcula cuánto tardará un bus específico (ej: 'bus-101') en llegar a una parada por su nombre")
    public String cuantoFaltaParaParada(String busId, String nombreParada) {
        List<LineaParada> relaciones = lineaParadaRepository.findByParada_Nombre(nombreParada);

        if (relaciones.isEmpty()) {
            return "No he encontrado ninguna parada llamada " + nombreParada;
        }

        LineaParada lp = relaciones.get(0);
        Ubicacion destino = lp.getParada().getUbicacion();

        // 4. USAMOS LA UBICACIÓN DEL BUS ESPECÍFICO
        Ubicacion origen = conductorService.obtenerPosicionActual(busId);

        Integer tiempoReal = motorCalculo.calcularTiempoRestanteEntrePuntos(origen, destino).block();

        return "El bus " + busId + " llegará a " + nombreParada + " en aproximadamente " + tiempoReal + " minutos.";
    }

    // ... el resto de tus herramientas se mantienen igual
}