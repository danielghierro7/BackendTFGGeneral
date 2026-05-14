package org.example.backendtfggeneral.services;

import dev.langchain4j.agent.tool.Tool;
import org.example.backendtfggeneral.beans.BusLlegadaDTO;
import org.example.backendtfggeneral.beans.ParadaTiempoDTO;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.LineaParada;
import org.example.backendtfggeneral.procesos.CalcularTiempoRestanteAParada;
import org.example.backendtfggeneral.repositorios.LineaParadaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LineaParadaService {

    private final LineaParadaRepository lineaParadaRepository;
    private final CalcularTiempoRestanteAParada motorCalculo;
    private final ConductorService conductorService;

    // --- CACHÉ EN MEMORIA (La "Vitrina") ---
    private final Map<Long, List<ParadaTiempoDTO>> cacheTiemposLinea = new ConcurrentHashMap<>();
    private final Map<Long, List<BusLlegadaDTO>> cacheTiemposParada = new ConcurrentHashMap<>();

    public LineaParadaService(LineaParadaRepository repo,
                              CalcularTiempoRestanteAParada motor,
                              ConductorService conductorService) {
        this.lineaParadaRepository = repo;
        this.motorCalculo = motor;
        this.conductorService = conductorService;
    }

    /**
     * TAREA PROGRAMADA: Actualiza la información cada 2 minutos.
     * Este es el ÚNICO sitio donde se hace el trabajo pesado de BD y API.
     */
    @Scheduled(fixedRate = 120000) // 120.000 ms = 2 minutos
    public void refrescarDatosGlobales() {
        System.out.println("🔄 [BACKEND] Iniciando actualización de tiempos en caché...");

        // 1. Actualizar caché de LÍNEAS (Ejemplo para línea 140)
        Long idLineaEjemplo = 140L;
        List<LineaParada> listaRelacion = lineaParadaRepository.findById_IdLineaOrderByOrdenAsc(idLineaEjemplo);
        Ubicacion posBus = conductorService.obtenerPosicionActual("bus-101");

        motorCalculo.calcularTiempoRestanteAVariasParadas(posBus, listaRelacion)
                .subscribe(tiempos -> {
                    List<ParadaTiempoDTO> respuesta = new ArrayList<>();
                    for (int i = 0; i < listaRelacion.size(); i++) {
                        LineaParada lp = listaRelacion.get(i);
                        Integer tiempo = (i < tiempos.size()) ? tiempos.get(i) : -1;
                        respuesta.add(new ParadaTiempoDTO(lp.getParada().getNombre(), tiempo, lp.getOrden()));
                    }
                    cacheTiemposLinea.put(idLineaEjemplo, respuesta);

                    // 2. Aprovechamos para actualizar la caché de PARADAS individualmente
                    actualizarCachePorParadas(listaRelacion, tiempos);
                });
    }

    private void actualizarCachePorParadas(List<LineaParada> relaciones, List<Integer> tiempos) {
        // 1. Opcional: Si quieres limpiar TODA la caché de paradas antes de rellenar
        // cacheTiemposParada.clear();

        for (int i = 0; i < relaciones.size(); i++) {
            LineaParada lp = relaciones.get(i);
            Long idParada = lp.getParada().getId();
            Integer tiempoLlegada = (i < tiempos.size()) ? tiempos.get(i) : -1;

            BusLlegadaDTO dto = new BusLlegadaDTO(
                    lp.getLinea().getNombreLinea(),
                    tiempoLlegada,
                    lp.getLinea().getCiudadDestino() != null ? lp.getLinea().getCiudadDestino().getNombre() : "Destino Final"
            );

            // SOLUCIÓN: En lugar de usar computeIfAbsent que siempre añade,
            // creamos una lista nueva o limpiamos la existente para esta parada específica.
            List<BusLlegadaDTO> listaNueva = new ArrayList<>();
            listaNueva.add(dto);
            cacheTiemposParada.put(idParada, listaNueva);
        }
    }

    // --- MÉTODOS DE FLUJO (Solo leen la caché) ---

    public Flux<List<ParadaTiempoDTO>> generarFlujoTiemposRealTime(Long idLineaBus) {
        // El Flux emite cada 10 segundos, pero NO hace consultas SQL
        return Flux.interval(Duration.ZERO, Duration.ofSeconds(10))
                .map(tick -> cacheTiemposLinea.getOrDefault(idLineaBus, new ArrayList<>()))
                .replay(1).refCount();
    }

    public Flux<List<BusLlegadaDTO>> obtenerBusesPorParadaFlujo(Long idParada) {
        return Flux.interval(Duration.ZERO, Duration.ofSeconds(10))
                .map(tick -> cacheTiemposParada.getOrDefault(idParada, new ArrayList<>()))
                .replay(1).refCount();
    }

    // --- MÉTODOS @TOOL (Para la IA) ---

    @Tool("Devuelve la ubicación actual del autobús usando su identificador (ej: 'bus-101')")
    public String obtenerUbicacionBusParaIA(String busId) {
        Ubicacion u = conductorService.obtenerPosicionActual(busId);
        return "El bus " + busId + " está en Lat: " + u.getLatitud() + ", Lon: " + u.getLongitud();
    }

    @Tool("Calcula cuánto tardará un bus específico en llegar a una parada")
    public String cuantoFaltaParaParada(String busId, String nombreParada) {
        // Para la IA, podemos permitir un .block() puntual o leer la caché
        List<LineaParada> relaciones = lineaParadaRepository.findByParada_Nombre(nombreParada);
        if (relaciones.isEmpty()) return "No encuentro esa parada.";

        Ubicacion origen = conductorService.obtenerPosicionActual(busId);
        Ubicacion destino = relaciones.get(0).getParada().getUbicacion();

        Integer tiempoReal = motorCalculo.calcularTiempoRestanteEntrePuntos(origen, destino).block();
        return "El bus " + busId + " llegará a " + nombreParada + " en " + tiempoReal + " minutos.";
    }
}