package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.entidades.Ciudad;
import org.example.backendtfggeneral.repositorios.CiudadRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CiudadService {

    private final CiudadRepository ciudadRepository;
        //genera una clase en tiempo de ejecucion
    public CiudadService(CiudadRepository ciudadRepository) {
        this.ciudadRepository = ciudadRepository; //esto crea una cosa llamada proxy interno que hace spring
        //Lo mismo que por ejemplo List... = new Arraylist () pero lo hace por detras
    }

    public List<Ciudad> obtenerTodas() {
        return ciudadRepository.findAll();

    }

    public Ciudad obtenerPorId(Long id) {
        return ciudadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada"));
    }

    public Ciudad obtenerPorNombre(String nombre) {
        return ciudadRepository.getCiudadByNombre((nombre));
    }

    public Ciudad crearCiudad(Ciudad ciudad) {
        return ciudadRepository.save(ciudad);
    }

    public Ciudad actualizarCiudad(Long id, Ciudad datos) {
        Ciudad ciudad = obtenerPorId(id);
        ciudad.setNombre(datos.getNombre());
        ciudad.setProvincia(datos.getProvincia());
        ciudad.setPais(datos.getPais());
        return ciudadRepository.save(ciudad);
    }

    public void eliminarCiudad(Long id) {
        ciudadRepository.deleteById(id);
    }
}
