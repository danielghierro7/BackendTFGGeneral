CREATE TABLE ciudad (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(150) NOT NULL,
                        provincia VARCHAR(150),
                        pais VARCHAR(150) DEFAULT 'España'
);

CREATE TABLE linea_bus (
                           id BIGINT PRIMARY KEY,
                           nombre_linea VARCHAR(200),
                           ciudad_origen BIGINT,
                           ciudad_destino BIGINT,
                           FOREIGN KEY (ciudad_origen) REFERENCES ciudad(id),
                           FOREIGN KEY (ciudad_destino) REFERENCES ciudad(id)
);



CREATE TABLE parada (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(255) NOT NULL,
                        latitud DOUBLE,
                        longitud DOUBLE,
                        id_ciudad BIGINT NOT NULL,
                        FOREIGN KEY (id_ciudad) REFERENCES ciudad(id)
);


CREATE TABLE linea_parada (
                              id_linea BIGINT,
                              id_parada BIGINT,
                              orden INT, -- Para saber cuál es la 1ª, 2ª, 3ª...
                              PRIMARY KEY (id_linea, id_parada),
                              FOREIGN KEY (id_linea) REFERENCES linea_bus(id),
                              FOREIGN KEY (id_parada) REFERENCES parada(id)
);


CREATE TABLE route (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       nombre VARCHAR(200) NOT NULL,
                       linea_bus_id BIGINT NOT NULL,
                       FOREIGN KEY (linea_bus_id) REFERENCES linea_bus(id)
);

CREATE TABLE ai_chat_log (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             pregunta TEXT NOT NULL,
                             respuesta TEXT NOT NULL,
                             timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- DATOS
-- =========================

-- CIUDADES (PUEBLOS)
INSERT INTO ciudad (nombre, provincia, pais) VALUES
                                                 ('Marchena', 'Sevilla', 'España'),
                                                 ('La Puebla de Cazalla', 'Sevilla', 'España'),
                                                 ('Arahal', 'Sevilla', 'España'),
                                                 ('Osuna', 'Sevilla', 'España');

-- LINEAS DE BUS
INSERT INTO linea_bus (id, nombre_linea, ciudad_origen, ciudad_destino) VALUES
                                                                            (140, 'M-140', 1, 2), -- Marchena -> La Puebla
                                                                            (141, 'M-141', 1, 3), -- Marchena -> Arahal
                                                                            (142, 'M-142', 1, 4); -- Marchena -> Osuna

-- PARADAS
INSERT INTO parada (nombre, latitud, longitud, id_ciudad) VALUES
-- Marchena
('Estación de Autobuses de Marchena', 37.3309, -5.4172, 1),
('Avenida Maestro Santos Ruano', 37.3275, -5.4150, 1),
('Plaza Ducal', 37.3292, -5.4163, 1),

-- La Puebla de Cazalla
('Estación de Autobuses La Puebla de Cazalla', 37.2226, -5.3115, 2),
('Avenida Antonio Fuentes', 37.2241, -5.3138, 2),
('Plaza Vieja', 37.2233, -5.3120, 2),

-- Arahal
('Estación de Autobuses de Arahal', 37.2624, -5.5454, 3),
('Avenida Verdeo', 37.2611, -5.5430, 3),
('Parque San Antonio', 37.2605, -5.5442, 3),

-- Osuna
('Estación de Autobuses de Osuna', 37.2365, -5.1027, 4),
('Hospital de la Merced', 37.2380, -5.1002, 4),
('Universidad de Osuna', 37.2372, -5.1015, 4);

-- RUTAS
INSERT INTO route (nombre, linea_bus_id) VALUES
                                             ('Ruta Marchena - La Puebla de Cazalla', 140),
                                             ('Ruta Marchena - Arahal', 141),
                                             ('Ruta Marchena - Osuna', 142);

-- LOGS DE IA (EJEMPLO)
INSERT INTO ai_chat_log (pregunta, respuesta) VALUES
                                                  ('¿Qué autobuses salen desde Marchena?', 'Desde Marchena salen las líneas M-140, M-141 y M-142'),
                                                  ('¿Hay autobús a Osuna?', 'Sí, la línea M-142 conecta Marchena con Osuna'),
                                                  ('¿Cuántas paradas tiene Arahal?', 'Arahal cuenta con varias paradas urbanas y una estación principal');
✅ Esto encaja PERFECTO con JPA