package org.example.backendtfggeneral.repositorios;

import org.example.backendtfggeneral.entidades.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
    Ciudad getCiudadByNombre(String nombre);


}
