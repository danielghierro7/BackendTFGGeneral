package org.example.backendtfggeneral.services;

import org.example.backendtfggeneral.beans.Ubicacion;

// Crea un pequeño Bean o Record para esto
public record EstadoBus(Ubicacion ubicacion, Long idLinea) {}
