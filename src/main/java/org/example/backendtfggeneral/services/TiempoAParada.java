package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.Ciudad;
import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.procesos.CalcularTiempoRestanteAParada;
import org.example.backendtfggeneral.repositorios.CiudadRepository;
import org.example.backendtfggeneral.repositorios.RouteRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class TiempoAParada {

    private CalcularTiempoRestanteAParada calcularTiempoRestanteAParada;

    public TiempoAParada(CalcularTiempoRestanteAParada calcularTiempoRestanteAParada) {
        this.calcularTiempoRestanteAParada=calcularTiempoRestanteAParada;

    }

    public Mono<List<Integer>> calcularTiempoRestanteAParadas(Ubicacion ubicacionBus, List<Parada> listaParadas) {
        return calcularTiempoRestanteAParada.calcularTiempoRestanteAVariasParadas(ubicacionBus,listaParadas);
    }
}
