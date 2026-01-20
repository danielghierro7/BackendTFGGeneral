package org.example.backendtfggeneral.repositorios;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.entidades.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    public ArrayList<Integer> devolverTiempoAParadas(Ubicacion ubicacionBus, List<Parada> listaParadas);
}
