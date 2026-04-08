package org.example.backendtfggeneral.controladores;

import org.springframework.web.bind.annotation.*;
import org.example.backendtfggeneral.configuracion.AsistenteBus;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AsistenteBus asistente;

    public ChatController(AsistenteBus asistente) {
        this.asistente = asistente;
    }

    @GetMapping("/preguntar")
    public String preguntar(@RequestParam String mensaje) {
        // Aquí es donde ocurre la magia:
        // 1. El controlador recibe el texto.
        // 2. LangChain4j se lo manda a Ollama.
        // 3. Ollama decide si necesita usar una Tool de LineaParadaService.
        // 4. Te devuelve la respuesta final.
        return asistente.chatear(mensaje);
    }
}