package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.Ciudad;
import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.procesos.CalcularTiempoRestanteAParada;
import org.example.backendtfggeneral.repositorios.CiudadRepository;
import org.example.backendtfggeneral.repositorios.RouteRepository;

import java.util.ArrayList;
import java.util.List;

public class TiempoAParada {

    private CalcularTiempoRestanteAParada calcularTiempoRestanteAParada;

    public TiempoAParada(CalcularTiempoRestanteAParada calcularTiempoRestanteAParada) {
        this.calcularTiempoRestanteAParada=calcularTiempoRestanteAParada;

    }

    public ArrayList<Integer> calcularTiempoRestanteAParadas(Ubicacion ubicacionBus, List<Parada> listaParadas) {
        ArrayList<Integer> tiempoRestantesAParadas = new ArrayList<>();

        for (Parada parada : listaParadas) {
            tiempoRestantesAParadas.add(calcularTiempoRestanteAParada.calcularTiempoRestanteEntrePuntos(ubicacionBus, parada.getUbicacion()))
        }
    }
}
