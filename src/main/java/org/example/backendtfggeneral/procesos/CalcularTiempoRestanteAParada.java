package org.example.backendtfggeneral.procesos;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.LineaParada;
import org.example.backendtfggeneral.entidades.LineaParadaId;
import org.example.backendtfggeneral.repositorios.LineaParadaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CalcularTiempoRestanteAParada {

    @Value("${ors.api.key}") String apiKey;

    private final WebClient webClient;
    private final LineaParadaRepository lineaParadaRepository;

    public CalcularTiempoRestanteAParada(WebClient.Builder builder, LineaParadaRepository lineaParadaRepository) {
        this.webClient = builder.baseUrl("https://api.openrouteservice.org/v2").build();
        this.lineaParadaRepository = lineaParadaRepository;
    }

    public Mono<Integer> calcularTiempoRestanteEntrePuntos(Ubicacion punto1, Ubicacion punto2) {
        if (punto1 == null || punto2 == null) return Mono.just(0);

        var body = Map.of(
                "coordinates", new double[][]{
                        {punto1.getLongitud(), punto1.getLatitud()},
                        {punto2.getLongitud(), punto2.getLatitud()}
                },
                "radiuses", new int[]{3000, 3000}
        );

        return webClient.post()
                .uri("/directions/driving-car")
                .header("Authorization", apiKey.trim())
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            System.err.println("ERROR DESDE ORS: " + errorBody);
                            return Mono.error(new RuntimeException("Error ORS: " + errorBody));
                        })
                )
                .bodyToMono(JsonNode.class)
                .map(root -> {
                    JsonNode routes = root.path("routes");
                    if (!routes.isArray() || routes.isEmpty()) return 0;
                    double segundos = routes.get(0).path("summary").path("duration").asDouble(0.0);
                    // Redondeamos hacia arriba para no dar 0 min si faltan 30 seg
                    return (int) Math.ceil(segundos / 60.0);
                })
                .onErrorResume(e -> {
                    System.err.println("Error en llamada a API ORS: " + e.getMessage());
                    return Mono.just(0);
                });
    }

    public Mono<List<Integer>> calcularTiempoRestanteAVariasParadas(Long idLinea, Ubicacion ubicacionBus) {

        // 1. Preguntamos a Oracle cuál es el ID de la siguiente parada
        Long idSiguienteParada = lineaParadaRepository.encontrarSiguienteParadaId(
                idLinea, ubicacionBus.getLatitud(), ubicacionBus.getLongitud());

        if (idSiguienteParada != null) {
            System.out.println("📍 ORACLE DICE: La siguiente parada es ID " + idSiguienteParada);
        } else {
            System.out.println("⚠️ ORACLE DICE: No se encontró parada (posible fin de trayecto).");
            return Mono.just(new ArrayList<>());
        }

        // 2. Localizamos el registro en la tabla intermedia para saber el ORDEN
        LineaParadaId idCompuesto = new LineaParadaId(idLinea, idSiguienteParada);
        List<LineaParada> paradasEncontradas = lineaParadaRepository.findLineaParadaById(idCompuesto);

        if (paradasEncontradas.isEmpty()) return Mono.just(new ArrayList<>());

        int ordenSiguiente = paradasEncontradas.get(0).getOrden();

        // 3. Obtenemos todas las paradas desde la actual hasta el final
        List<LineaParada> tramosRestantes = lineaParadaRepository.findById_IdLineaOrderByOrdenAsc(idLinea)
                .stream()
                .filter(lp -> lp.getOrden() >= ordenSiguiente)
                .toList();

        if (tramosRestantes.isEmpty()) return Mono.just(new ArrayList<>());

        // 4. Cálculo Reactivo
        return calcularTiempoRestanteEntrePuntos(ubicacionBus, tramosRestantes.get(0).getParada().getUbicacion())
                .map(tiempoPrimerTramo -> {
                    List<Integer> resultados = new ArrayList<>();
                    int acumulado = tiempoPrimerTramo;

                    System.out.println("⏱️ [API] Tiempo a la parada " + idSiguienteParada + ": " + acumulado + " min");
                    resultados.add(acumulado);

                    // Bucle acumulativo: tiempo API + sumas sucesivas de BD
                    for (int i = 0; i < tramosRestantes.size() - 1; i++) {
                        // CORRECCIÓN CRÍTICA: Evitar NullPointerException al hacer unboxing
                        Integer tiempoBDNullable = tramosRestantes.get(i).getTiempoSiguienteMin();
                        int tiempoDeBD = (tiempoBDNullable != null) ? tiempoBDNullable : 0;

                        acumulado += tiempoDeBD;

                        System.out.println("⏱️ [ACUMULADO] Parada Orden " + tramosRestantes.get(i+1).getOrden() + ": " + acumulado + " min");
                        resultados.add(acumulado);
                    }
                    return resultados;
                });
    }
}