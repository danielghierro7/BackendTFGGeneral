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
import java.util.Set;
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

    @Scheduled(fixedRate = 120000) // Cada 2 minutos refresca la "vitrina"
    public void refrescarDatosGlobales() {
        System.out.println("🔄 [BACKEND] Refrescando tiempos para todas las líneas activas...");

        // 1. Obtenemos solo los IDs de las líneas con buses en movimiento
        Set<Long> lineasActivas = conductorService.obtenerLineasActivas();

        if (lineasActivas.isEmpty()) {
            System.out.println("💤 No hay buses activos. Standby...");
            return;
        }

        for (Long idLinea : lineasActivas) {
            Map<String, Ubicacion> busesDeLaLinea = conductorService.obtenerBusesActivosPorLinea(idLinea);

            if (!busesDeLaLinea.isEmpty()) {
                // Tomamos la posición del primer bus encontrado para esta línea
                Ubicacion posBus = busesDeLaLinea.values().iterator().next();

                motorCalculo.calcularTiempoRestanteAVariasParadas(idLinea, posBus)
                        .subscribe(tiempos -> {
                            List<LineaParada> listaRelacion = lineaParadaRepository.findById_IdLineaOrderByOrdenAsc(idLinea);

                            // Actualizamos las dos cachés: la de la línea completa y la de paradas individuales
                            actualizarCacheEnMemoria(idLinea, listaRelacion, tiempos);

                            System.out.println("✅ Caché actualizada para línea: " + idLinea + " (" + tiempos.size() + " paradas pendientes)");
                        });
            }
        }
    }

    /**
     * Método de apoyo para organizar los datos en la "vitrina" (caché)
     */
    /**
     * Método de apoyo para organizar los datos en la "vitrina" (caché)
     */
    private void actualizarCacheEnMemoria(Long idLinea, List<LineaParada> relaciones, List<Integer> tiempos) {
        List<ParadaTiempoDTO> listaLinea = new ArrayList<>();

        // El offset nos dice cuántas paradas ya han quedado atrás
        int offset = relaciones.size() - tiempos.size();

        for (int i = 0; i < relaciones.size(); i++) {
            LineaParada lp = relaciones.get(i);
            Integer minutos = (i >= offset) ? tiempos.get(i - offset) : 0; // 0 si ya pasó

            ParadaTiempoDTO dto = new ParadaTiempoDTO(lp.getParada().getNombre(), minutos, lp.getOrden());
            listaLinea.add(dto);

            // --- CORRECCIÓN AQUÍ: Ahora pasamos 4 argumentos (idLinea al principio) ---
            actualizarCacheIndividualParada(idLinea, lp.getParada().getId(), lp.getLinea().getNombreLinea(), minutos);
        }

        cacheTiemposLinea.put(idLinea, listaLinea);
    }

    private void actualizarCacheIndividualParada(Long idLinea, Long idParada, String nombreLinea, Integer minutos) {
        // 1. Buscamos el nombre de la última parada de esta línea (Destino Final) usando el idLinea
        String destinoFinal = lineaParadaRepository.obtenerNombreDestinoFinal(idLinea)
                .orElse("Cargando...");

        // 2. Ahora creamos el DTO con el nombre real del destino
        BusLlegadaDTO llegada = new BusLlegadaDTO(
                nombreLinea,
                minutos,
                destinoFinal
        );

        List<BusLlegadaDTO> listaNueva = new ArrayList<>();
        listaNueva.add(llegada);

        // Guardamos en el mapa cacheTiemposParada
        cacheTiemposParada.put(idParada, listaNueva);
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
    @Tool("Calcula cuánto tardará un bus específico en llegar a una parada buscando por su nombre")
    public String cuantoFaltaParaParada(String busId, String nombreParada) {
        // 1. Obtenemos la línea en la que está trabajando ese bus actualmente
        Long idLinea = conductorService.obtenerLineaDeBus(busId);
        if (idLinea == null) return "El bus " + busId + " no está asignado a ninguna línea activa ahora mismo.";

        // 2. Buscamos en la CACHÉ que ya tienes actualizada (cacheTiemposLinea)
        // Esto es mucho más rápido y no bloquea el hilo
        List<ParadaTiempoDTO> tiemposLinea = cacheTiemposLinea.get(idLinea);

        if (tiemposLinea != null) {
            return tiemposLinea.stream()
                    // Buscamos la parada ignorando mayúsculas/minúsculas
                    .filter(p -> p.getNombreParada().equalsIgnoreCase(nombreParada))
                    .findFirst()
                    .map(p -> "El bus " + busId + " llegará a " + nombreParada + " en aproximadamente " + p.getMinutosFaltantes() + " minutos.")
                    .orElse("La parada '" + nombreParada + "' no pertenece a la línea actual del bus " + busId + ".");
        }

        return "Lo siento, todavía estoy calculando los tiempos para esa línea. Inténtalo en unos segundos.";
    }
}