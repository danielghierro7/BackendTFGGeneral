package org.example.backendtfggeneral.procesos;



import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.Parada;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

//Crea una instancia automáticamente y puedes inyectarla en otras clases con @Autowired o a través del constructor
@Component
public class CalcularTiempoRestanteAParada {

    private final WebClient webClient;

    @Value("${ors.api.key}") String apiKey;


    public CalcularTiempoRestanteAParada(WebClient.Builder builder) {

        //WebClient.Builder es el configurador donde defino como va a se el objeto WebClient que construiré con el .build
        //Builder Pattern es un patrón de diseño de creación Su objetivo es solucionar el problema de tener constructores con demasiados parámetros (el "constructor pesadilla")
        //Fluent interface es para escribirlo todo de corrido como seria leido en la vida real en vez de estar separando cada metodo por ;


        this.webClient = builder.baseUrl("https://api.openrouteservice.org/v2/directions/driving-car").build();
    }

    public Mono<Integer> calcularTiempoRestanteEntrePuntos(Ubicacion punto1, Ubicacion punto2) {

            String body = String.format("""
                {
                    "coordinates": [
                        [%f, %f],
                        [%f, %f]
                    ]
                }
                """, punto1.getLongitud(), punto1.getLatitud(),
                    punto2.getLongitud(), punto2.getLatitud());

            return webClient.post()
                      .header("Authorization", apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                      .bodyToMono(String.class)
                    //.block(); no puede ir el block
                    .map(json->{


            ObjectMapper mapper = new ObjectMapper();

                        try {
                            JsonNode root = mapper.readTree(json);

                        int segundos = root.path("features").get(0)
                    .path("properties")
                    .path("segments").get(0)
                    .path("duration").asInt();

            return segundos / 60.0;

                        }catch (Exception e) {
                            throw new RuntimeException("Error al calcular tiempo parada", e);

                        }


                    });

    }



    public Flux<Integer> calcularTiempoRestanteAVariasParadas(Ubicacion ubicacionBus, List<Parada> listaParadas) {
        return Flux.fromIterable(listaParadas) // crea un flujo de paradas
                .flatMap(parada -> calcularTiempoRestanteEntrePuntos(ubicacionBus, parada.getUbicacion()));
}
}
