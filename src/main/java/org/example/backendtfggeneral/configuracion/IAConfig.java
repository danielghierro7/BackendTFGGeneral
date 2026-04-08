package org.example.backendtfggeneral.configuracion;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.example.backendtfggeneral.services.LineaParadaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IAConfig {
    @Bean
    public AsistenteBus asistenteBus(LineaParadaService service) {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:3b") // Asegúrate de haber hecho 'ollama run llama3' antes
                .build();

        return AiServices.builder(AsistenteBus.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(service) // Aquí inyectas tu Service que tendrá los métodos @Tool
                .build();
    }
}



