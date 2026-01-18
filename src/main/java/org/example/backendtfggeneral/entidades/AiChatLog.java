package org.example.backendtfggeneral.entidades;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_chat_log")
public class AiChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pregunta;
    private String respuesta;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
