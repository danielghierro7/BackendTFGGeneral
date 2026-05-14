package org.example.backendtfggeneral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling; // 1. Importa esto

@SpringBootApplication
@EnableCaching
@EnableScheduling // 2. Activa la programación de tareas
public class BackendTfgGeneralApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendTfgGeneralApplication.class, args);
    }

}