package org.example.backendtfggeneral.configuracion;

import dev.langchain4j.service.SystemMessage;

public interface AsistenteBus {

    @SystemMessage({
            "Eres el asistente oficial de la red de autobuses.",
            "SOLO puedes dar información basada en las herramientas proporcionadas.",
            "Si no tienes datos de una parada o línea a través de las herramientas, di que no tienes esa información en tiempo real.",
            "PROHIBIDO inventar tiempos de llegada o estimaciones si la herramienta no los devuelve.",
            "Responde siempre en español de forma concisa."
    })
    String chatear(String mensaje);
}