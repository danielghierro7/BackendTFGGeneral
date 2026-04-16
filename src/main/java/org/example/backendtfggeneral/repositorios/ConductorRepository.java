package org.example.backendtfggeneral.repositorios;

import org.example.backendtfggeneral.entidades.ConductorAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConductorRepository extends JpaRepository<ConductorAuth, Long> {

    Optional<ConductorAuth> findByBusId(String busId);
}
