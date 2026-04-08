package org.example.backendtfggeneral.configuracion;

import dev.langchain4j.service.SystemMessage;

public interface AsistenteBus {

    @SystemMessage({
            "Eres el asistente inteligente de la red de autobuses.",
            "Tu objetivo es ayudar a los usuarios con tiempos de llegada y rutas.",
            "Usa las herramientas proporcionadas para dar datos reales del sistema.",
            "Sé amable, conciso y responde siempre en español."
    })
    String chatear(String mensaje);
}