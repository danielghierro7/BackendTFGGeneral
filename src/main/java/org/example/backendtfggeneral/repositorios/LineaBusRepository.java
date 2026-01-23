package org.example.backendtfggeneral.repositorios;

import org.example.backendtfggeneral.entidades.LineaBus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaBusRepository extends JpaRepository<LineaBus, Long> {
    Long id(Long id);
}
