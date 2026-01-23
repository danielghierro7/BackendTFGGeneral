package org.example.backendtfggeneral.repositorios;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.entidades.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

}
