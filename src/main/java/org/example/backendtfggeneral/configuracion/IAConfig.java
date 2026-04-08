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
                // "host.docker.internal" apunta a tu Windows desde dentro de Docker
                .baseUrl("http://host.docker.internal:11434")
                .modelName("mistral")
                .build();

        return AiServices.builder(AsistenteBus.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(service)
                .build();
    }
}



