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

@Component
public class CalcularTiempoRestanteAParada {

    @Value("${ors.api.key}") String apiKey;

    private final WebClient webClient;
    private final LineaParadaRepository lineaParadaRepository;

    public CalcularTiempoRestanteAParada(WebClient.Builder builder, LineaParadaRepository lineaParadaRepository) {
        this.webClient = builder.baseUrl("https://api.openrouteservice.org/v2").build();
        this.lineaParadaRepository = lineaParadaRepository;
    }

    // Tu método de ORS se queda igual (está muy bien gestionado)
    public Mono<Integer> calcularTiempoRestanteEntrePuntos(Ubicacion punto1, Ubicacion punto2) {
        if (punto1 == null || punto2 == null) return Mono.just(0);
        var body = java.util.Map.of(
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
                    return (int) Math.ceil(segundos / 60.0);
                })
                .onErrorResume(e -> Mono.just(0));
    }

    /**
     * MÉTODO ACTUALIZADO: Lógica TUSSAM con Oracle Spatial
     */
    public Mono<List<Integer>> calcularTiempoRestanteAVariasParadas(Long idLinea, Ubicacion ubicacionBus) {

        // 1. Preguntamos a Oracle cuál es el ID de la siguiente parada basándose en la ruta LRS
        Long idSiguienteParada = lineaParadaRepository.encontrarSiguienteParadaId(
                idLinea, ubicacionBus.getLatitud(), ubicacionBus.getLongitud());

        if (idSiguienteParada == null) {
            System.out.println("Bus fuera de ruta o línea terminada.");
            return Mono.just(new ArrayList<>());
        }

        // 2. Buscamos el objeto LineaParada completo para saber su 'orden'
        // Usamos la clave compuesta (idLinea, idSiguienteParada)
        LineaParadaId idCompuesto = new LineaParadaId(idLinea, idSiguienteParada);
        List<LineaParada> paradasEncontradas = lineaParadaRepository.findLineaParadaById(idCompuesto);

        if (paradasEncontradas.isEmpty()) return Mono.just(new ArrayList<>());

        int ordenSiguiente = paradasEncontradas.get(0).getOrden();

        // 3. Obtenemos todas las paradas desde la actual hasta el final ordenadas
        // He adaptado el nombre a lo que Spring espera según tu Repository
        List<LineaParada> tramosRestantes = lineaParadaRepository.findById_IdLineaOrderByOrdenAsc(idLinea)
                .stream()
                .filter(lp -> lp.getOrden() >= ordenSiguiente)
                .toList();

        if (tramosRestantes.isEmpty()) return Mono.just(new ArrayList<>());

        // 4. Calculamos tiempo real a la PRIMERA parada (ORS) y sumamos el resto (BD)
        return calcularTiempoRestanteEntrePuntos(ubicacionBus, tramosRestantes.get(0).getParada().getUbicacion())
                .map(tiempoPrimerTramo -> {
                    List<Integer> resultados = new ArrayList<>();
                    int acumulado = tiempoPrimerTramo;
                    resultados.add(acumulado);

                    // Bucle acumulativo: tiempo API + sumas sucesivas de BD
                    for (int i = 0; i < tramosRestantes.size() - 1; i++) {
                        int tiempoDeBD = tramosRestantes.get(i).getTiempoSiguienteMin();
                        acumulado += tiempoDeBD;
                        resultados.add(acumulado);
                    }
                    return resultados;
                });
    }
}