package org.example.backendtfggeneral.repositorios;

import org.example.backendtfggeneral.entidades.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
    Ciudad getCiudadByNombre(String nombre);
}
