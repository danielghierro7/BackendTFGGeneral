-- Creación de tablas con soporte UTF-8 completo
SET NAMES 'utf8mb4';
CREATE TABLE ciudad (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(150) NOT NULL,
                        provincia VARCHAR(150),
                        pais VARCHAR(150) DEFAULT 'España'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE linea_bus (
                           id BIGINT PRIMARY KEY,
                           nombre_linea VARCHAR(200),
                           ciudad_origen BIGINT,
                           ciudad_destino BIGINT,
                           FOREIGN KEY (ciudad_origen) REFERENCES ciudad(id),
                           FOREIGN KEY (ciudad_destino) REFERENCES ciudad(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE parada (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(255) NOT NULL,
                        latitud DOUBLE,
                        longitud DOUBLE,
                        id_ciudad BIGINT NOT NULL,
                        FOREIGN KEY (id_ciudad) REFERENCES ciudad(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE linea_parada (
                              id_linea BIGINT,
                              id_parada BIGINT,
                              orden INT,
                              tiempo_siguiente_min INT,
                              distancia_siguiente_km DOUBLE,
                              PRIMARY KEY (id_linea, id_parada),
                              FOREIGN KEY (id_linea) REFERENCES linea_bus(id),
                              FOREIGN KEY (id_parada) REFERENCES parada(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE route (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       nombre VARCHAR(200) NOT NULL,
                       linea_bus_id BIGINT NOT NULL,
                       FOREIGN KEY (linea_bus_id) REFERENCES linea_bus(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ai_chat_log (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             pregunta TEXT NOT NULL,
                             respuesta TEXT NOT NULL,
                             timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




-- TABLA DE AUTENTICACIÓN PARA CONDUCTORES
CREATE TABLE conductor_auth (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                bus_id VARCHAR(50) NOT NULL UNIQUE, -- El identificador para el login (ej: 'bus-101')
                                password VARCHAR(255) NOT NULL,      -- La contraseña (antes 'password_hash')
                                nombre_conductor VARCHAR(150),
                                id_linea_asignada BIGINT,
                                FOREIGN KEY (id_linea_asignada) REFERENCES linea_bus(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =========================
-- DATOS
-- =========================

-- CIUDADES
INSERT INTO ciudad (nombre, provincia, pais) VALUES
                                                 ('Marchena', 'Sevilla', 'España'),
                                                 ('La Puebla de Cazalla', 'Sevilla', 'España'),
                                                 ('Arahal', 'Sevilla', 'España'),
                                                 ('Osuna', 'Sevilla', 'España');

-- LINEAS DE BUS
INSERT INTO linea_bus (id, nombre_linea, ciudad_origen, ciudad_destino) VALUES
                                                                            (140, 'M-140', 1, 2),
                                                                            (141, 'M-141', 1, 3),
                                                                            (142, 'M-142', 1, 4);

-- PARADAS
INSERT INTO parada (nombre, latitud, longitud, id_ciudad) VALUES
                                                              ('Estación de Autobuses de Marchena', 37.3309, -5.4172, 1),
                                                              ('Avenida Maestro Santos Ruano', 37.3275, -5.4150, 1),
                                                              ('Plaza Ducal', 37.3292, -5.4163, 1),
                                                              ('Estación de Autobuses La Puebla de Cazalla', 37.2226, -5.3115, 2),
                                                              ('Avenida Antonio Fuentes', 37.2241, -5.3138, 2),
                                                              ('Plaza Vieja', 37.2233, -5.3120, 2),
                                                              ('Estación de Autobuses de Arahal', 37.2624, -5.5454, 3),
                                                              ('Avenida Verdeo', 37.2611, -5.5430, 3),
                                                              ('Parque San Antonio', 37.2605, -5.5442, 3),
                                                              ('Estación de Autobuses de Osuna', 37.2365, -5.1027, 4),
                                                              ('Hospital de la Merced', 37.2380, -5.1002, 4),
                                                              ('Universidad de Osuna', 37.2372, -5.1015, 4);

-- RUTAS
INSERT INTO route (nombre, linea_bus_id) VALUES
                                             ('Ruta Marchena - La Puebla de Cazalla', 140),
                                             ('Ruta Marchena - Arahal', 141),
                                             ('Ruta Marchena - Osuna', 142);

-- LOGS DE IA
INSERT INTO ai_chat_log (pregunta, respuesta) VALUES
                                                  ('¿Qué autobuses salen desde Marchena?', 'Desde Marchena salen las líneas M-140, M-141 y M-142'),
                                                  ('¿Hay autobús a Osuna?', 'Sí, la línea M-142 conecta Marchena con Osuna'),
                                                  ('¿Cuántas paradas tiene Arahal?', 'Arahal cuenta con varias paradas urbanas y una estación principal');

-- ASIGNACIÓN DE PARADAS A LÍNEAS
INSERT INTO linea_parada (id_linea, id_parada, orden, tiempo_siguiente_min, distancia_siguiente_km) VALUES
                                                                                                        (140, 1, 1, 3, 1.2),
                                                                                                        (140, 2, 2, 2, 0.8),
                                                                                                        (140, 3, 3, 18, 14.5),
                                                                                                        (140, 4, 4, 4, 1.5),
                                                                                                        (140, 5, 5, 2, 0.7),
                                                                                                        (140, 6, 6, 0, 0.0),
                                                                                                        (141, 1, 1, 3, 1.2),
                                                                                                        (141, 3, 2, 15, 12.0),
                                                                                                        (141, 7, 3, 4, 1.1),
                                                                                                        (141, 8, 4, 2, 0.6),
                                                                                                        (141, 9, 5, 0, 0.0),
                                                                                                        (142, 1, 1, 25, 24.0),
                                                                                                        (142, 10, 2, 6, 2.2),
                                                                                                        (142, 11, 3, 3, 1.1),
                                                                                                        (142, 12, 4, 0, 0.0);

INSERT INTO conductor_auth (bus_id, password, nombre_conductor, id_linea_asignada) VALUES
                                                                                       ('bus-101', 'tfg2024', 'Juan Pérez', 140),
                                                                                       ('bus-102', 'admin123', 'Ana García', 141),
                                                                                       ('bus-103', 'osuna2026', 'Carlos Ruiz', 142);