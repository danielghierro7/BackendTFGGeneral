package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.beans.BusLlegadaDTO;
import org.example.backendtfggeneral.beans.ParadaTiempoDTO;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.LineaParada;
import org.example.backendtfggeneral.procesos.CalcularTiempoRestanteAParada;
import org.example.backendtfggeneral.repositorios.LineaParadaRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import dev.langchain4j.agent.tool.Tool;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
@Service
public class LineaParadaService {
    private final LineaParadaRepository lineaParadaRepository;
    private final CalcularTiempoRestanteAParada motorCalculo;
    private final AtomicReference<Ubicacion> ubicacionRealBus = new AtomicReference<>(new Ubicacion(37.38, -5.98));
    private final java.util.Map<Long, Flux<List<BusLlegadaDTO>>> flujosPorParada = new java.util.concurrent.ConcurrentHashMap<>();

    public LineaParadaService(LineaParadaRepository repo, CalcularTiempoRestanteAParada motor) {
        this.lineaParadaRepository = repo;
        this.motorCalculo = motor;
    }

    // --- MÉTODOS DE LÓGICA REACTIVA (Para el Mapa/Frontend) ---

    public void actualizarPosicion(Ubicacion nueva) {
        this.ubicacionRealBus.set(nueva);
    }

    public Flux<List<ParadaTiempoDTO>> generarFlujoTiemposRealTime(Long idLineaBus) {
        return Flux.interval(Duration.ZERO, Duration.ofMinutes(3))
                .flatMap(tick -> {
                    List<LineaParada> listaRelacion = lineaParadaRepository.findById_IdLineaOrderByOrdenAsc(idLineaBus);
                    Ubicacion posicionActualDelBus = ubicacionRealBus.get();

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
                Flux.interval(Duration.ZERO, Duration.ofMinutes(3))
                        .flatMap(tick -> {
                            List<LineaParada> lineasQuePasan = lineaParadaRepository.findById_IdParada(id);
                            return Flux.fromIterable(lineasQuePasan)
                                    .flatMap(lp -> {
                                        return motorCalculo.calcularTiempoRestanteEntrePuntos(ubicacionRealBus.get(), lp.getParada().getUbicacion())
                                                .onErrorResume(e -> Mono.just(-1))
                                                .map(tiempo -> new BusLlegadaDTO(lp.getLinea().getNombreLinea(), tiempo, "Destino Simulado"));
                                    })
                                    .collectList();
                        })
                        .replay(1).refCount()
                        .doFinally(signal -> flujosPorParada.remove(id))
        );
    }

    // --- MÉTODOS @TOOL (Para el Buscador Inteligente / IA) ---

    @Tool("Devuelve la ubicación actual del autobús (latitud y longitud)")
    public String obtenerUbicacionBusParaIA() {
        Ubicacion u = ubicacionRealBus.get();
        return "El bus está en Lat: " + u.getLatitud() + ", Lon: " + u.getLongitud();
    }

    @Tool("Obtiene la lista de nombres de paradas que componen una línea de bus específica usando su ID")
    public List<String> obtenerParadasDeLineaParaIA(Long idLineaBus) {
        return lineaParadaRepository.findById_IdLineaOrderByOrdenAsc(idLineaBus)
                .stream()
                .map(lp -> lp.getParada().getNombre())
                .toList();
    }

    @Tool("Obtiene qué líneas de autobús pasan por una parada concreta usando el ID de la parada")
    public List<String> obtenerLineasDeParadaParaIA(Long idParada) {
        return lineaParadaRepository.findById_IdParada(idParada)
                .stream()
                .map(lp -> lp.getLinea().getNombreLinea())
                .distinct()
                .toList();
    }

    @Tool("Calcula cuánto tardará el bus en llegar a una parada según su nombre")
    public String cuantoFaltaParaParada(String nombreParada) {
        // En un futuro aquí podrías buscar la parada por nombre en el repo y llamar al motorCalculo.
        // Por ahora le damos una respuesta coherente para probar.
        return "Consultando el tráfico y la posición actual... El bus llegará a " + nombreParada + " en aproximadamente 5-10 minutos.";
    }
}