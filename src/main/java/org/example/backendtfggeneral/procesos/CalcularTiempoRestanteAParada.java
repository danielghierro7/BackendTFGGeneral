package org.example.backendtfggeneral.procesos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.LineaParada;
import org.example.backendtfggeneral.entidades.LineaParadaId;
import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.repositorios.LineaParadaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

//Crea una instancia automáticamente y puedes inyectarla en otras clases con @Autowired o a través del constructor
@Component
public class CalcularTiempoRestanteAParada {

    @Value("${ors.api.key}") String apiKey;

    private final WebClient webClient;
    private final LineaParadaRepository lineaParadaRepository;



        //WebClient.Builder es el configurador donde defino como va a se el objeto WebClient que construiré con el .build
        //Builder Pattern es un patrón de diseño de creación Su objetivo es solucionar el problema de tener constructores con demasiados parámetros (el "constructor pesadilla")
        //Fluent interface es para escribirlo todo de corrido como seria leido en la vida real en vez de estar separando cada metodo por ;
public CalcularTiempoRestanteAParada(WebClient.Builder builder,LineaParadaRepository lineaParadaRepository) {
            // Es mejor dejar la base hasta el v2
            this.webClient = builder.baseUrl("https://api.openrouteservice.org/v2").build();
            this.lineaParadaRepository = lineaParadaRepository;
        }

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
                    System.out.println("Procesando respuesta de ORS...");

                    // Validamos que el JSON no sea nulo y tenga contenido
                    if (root == null || root.isMissingNode()) {
                        System.err.println("La respuesta de ORS está vacía.");
                        return 0;
                    }

                    // 1. Validamos que el nodo "routes" exista y sea un array con elementos
                    JsonNode routes = root.path("routes");
                    if (!routes.isArray() || routes.isEmpty()) {
                        System.err.println("No se encontraron rutas en el JSON: " + root.toString());
                        return 0;
                    }

                    JsonNode route = routes.get(0);

                    // 2. Accedemos a summary con seguridad
                    JsonNode summary = route.path("summary");
                    if (summary.isMissingNode()) {
                        System.err.println("No se encontró 'summary' en la primera ruta");
                        return 0;
                    }

                    double segundos = summary.path("duration").asDouble(0.0);
                    int minutos = (int) Math.ceil(segundos / 60.0);

                    System.out.println("✅ ¡CONSEGUIDO! Tiempo: " + minutos + " min");
                    return minutos;
                })
                .onErrorResume(e -> {
                    // Si algo falla (red, 429, etc), devolvemos 0 para que la app no muera
                    System.err.println("Fallo crítico en el cálculo: " + e.getMessage());
                    return Mono.just(0);
                });
    }





    public Mono<List<Integer>> calcularTiempoRestanteAVariasParadas(Ubicacion ubicacionBus, List<LineaParada> todasLasParadasRuta) {

        List<LineaParada> tramosRestantes = todasLasParadasRuta.subList(2, todasLasParadasRuta.size());
        //el 2 cambiarlo por el numero siguiente Hay que calcularlo

        return calcularTiempoRestanteEntrePuntos(ubicacionBus, tramosRestantes.get(0).getParada().getUbicacion())
                .map(tiempoPrimerTramo -> {

                    List<Integer> resultados = new ArrayList<>();
                    int acumulado = tiempoPrimerTramo;
                    resultados.add(acumulado);


                    // Los tiempos ya vienen en los objetos de la lista
                    for (int i = 0; i < tramosRestantes.size() - 1; i++) {
                        // Sacamos el tiempo de la BD
                        int tiempoDeBD = tramosRestantes.get(i).getTiempoSiguienteMin();

                        acumulado += tiempoDeBD;
                        resultados.add(acumulado);
                    }

                    return resultados;
                });




        //No hace falta poner el subscribe, lo hace el framework
}
}
