package org.example.backendtfggeneral.procesos;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.Parada;
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




        //WebClient.Builder es el configurador donde defino como va a se el objeto WebClient que construiré con el .build
        //Builder Pattern es un patrón de diseño de creación Su objetivo es solucionar el problema de tener constructores con demasiados parámetros (el "constructor pesadilla")
        //Fluent interface es para escribirlo todo de corrido como seria leido en la vida real en vez de estar separando cada metodo por ;
public CalcularTiempoRestanteAParada(WebClient.Builder builder) {
            // Es mejor dejar la base hasta el v2
            this.webClient = builder.baseUrl("https://api.openrouteservice.org/v2").build();
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
                    // Imprimimos para confirmar que estamos leyendo este bloque
                    System.out.println("Procesando respuesta de ORS...");

                    // 1. En tu JSON, la clave principal es "routes", no "features"
                    JsonNode route = root.path("routes").get(0);

                    if (route == null || route.isMissingNode()) {
                        System.err.println("No se encontró el nodo 'routes'. JSON recibido: " + root.toString());
                        return 0;
                    }

                    // 2. Accedemos a summary que está directo dentro de la ruta
                    JsonNode summary = route.path("summary");

                    if (summary.isMissingNode()) {
                        System.err.println("No se encontró 'summary' dentro de la ruta");
                        return 0;
                    }

                    double segundos = summary.path("duration").asDouble();
                    int minutos = (int) Math.ceil(segundos / 60.0);

                    System.out.println("✅ ¡CONSEGUIDO! Tiempo: " + minutos + " min");
                    return minutos;
                });
    }





    public Mono<List<Integer>> calcularTiempoRestanteAVariasParadas(Ubicacion ubicacionBus, List<Parada> todasLasParadas) {

        List<Parada> paradasRestantes = todasLasParadas.subList(2, todasLasParadas.size());
        //el 2 cambiarlo por el numero siguiente


        return calcularTiempoRestanteEntrePuntos(ubicacionBus,paradasRestantes.get(0).getUbicacion()).map(
                tiempoPrimerTramo-> {

                    List<Integer> resultados = new ArrayList<>();
                    int acumulado = tiempoPrimerTramo;
                    resultados.add(acumulado);

// 3. Sumamos los tiempos precalculados de las siguientes
                    for (int i = 0; i < paradasRestantes.size() - 1; i++) {
                        acumulado += paradasRestantes.get(i).getTiempoSiguienteParada();
                        resultados.add(acumulado);
                    }
                    return resultados;
                });




        //No hace falta poner el subscribe, lo delega el framework
}
}
