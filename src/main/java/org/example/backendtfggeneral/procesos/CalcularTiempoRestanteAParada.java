package org.example.backendtfggeneral.procesos;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.Parada;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

//Crea una instancia automáticamente y puedes inyectarla en otras clases con @Autowired o a través del constructor
@Component
public class CalcularTiempoRestanteAParada {

    @Value("${ors.api.key}") String apiKey;

    private final WebClient webClient;



    public CalcularTiempoRestanteAParada(WebClient.Builder builder,@Value("${ors.api.url}")  String url) {

        //WebClient.Builder es el configurador donde defino como va a se el objeto WebClient que construiré con el .build
        //Builder Pattern es un patrón de diseño de creación Su objetivo es solucionar el problema de tener constructores con demasiados parámetros (el "constructor pesadilla")
        //Fluent interface es para escribirlo todo de corrido como seria leido en la vida real en vez de estar separando cada metodo por ;

        this.webClient = builder.baseUrl(url).build();
    }

    public Mono<Integer> calcularTiempoRestanteEntrePuntos(Ubicacion punto1, Ubicacion punto2) {

        var body = java.util.Map.of(
                "coordinates", new double[][] {
                        { punto1.getLongitud(), punto1.getLatitud() },
                        { punto2.getLongitud(), punto2.getLatitud() }
                }
        );

            return webClient.post()
                      .header("Authorization", apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                      .bodyToMono(JsonNode.class)  //usa por detras jackson por eso no lo instancio el ObjectMapper

                    //.block(); no puede ir el block
                    .map(root->{

                        try {



                                double segundos = root.path("features").path(0)
                                        .path("properties")
                                        .path("summary").path(0)
                                        .path("duration").asDouble();

                                return (int) Math.ceil(segundos / 60.0);

                             }catch(Exception e){
                                throw new RuntimeException("Error al calcular tiempo parada", e);

                            }


                    });

    }



    public Mono<List<Integer>> calcularTiempoRestanteAVariasParadas(Ubicacion ubicacionBus, List<Parada> listaParadas) {
        return Flux.fromIterable(listaParadas) // crea un flujo de paradas
                .flatMap(parada -> calcularTiempoRestanteEntrePuntos(ubicacionBus, parada.getUbicacion())).collectList();
        //No hace falta poner el subscribe, lo delega el framework
}
}
