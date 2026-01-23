package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.Parada;
import org.example.backendtfggeneral.entidades.Route;
import org.example.backendtfggeneral.procesos.CalcularTiempoRestanteAParada;
import org.example.backendtfggeneral.repositorios.CiudadRepository;
import org.example.backendtfggeneral.repositorios.RouteRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class RouteService  {


    private RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository,CalcularTiempoRestanteAParada calcularTiempoRestanteAParada) {
        this.routeRepository = routeRepository;

    }








}
