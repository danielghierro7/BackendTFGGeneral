CREATE DATABASE IF NOT EXISTS empresa;
USE empresa;

CREATE TABLE DEPARTAMENTOS (
                               dept_no INT PRIMARY KEY,
                               dnombre VARCHAR(50),
                               loc     VARCHAR(50)
);

CREATE TABLE EMPLEADOS (
                           emp_no    INT PRIMARY KEY,
                           apellido  VARCHAR(50),
                           oficio    VARCHAR(50),
                           dir       INT,
                           fecha_alt DATE,
                           salario   FLOAT,
                           comision  FLOAT,
                           dept_no   INT,
                           FOREIGN KEY (dept_no) REFERENCES DEPARTAMENTOS(dept_no)
);

-- Datos de prueba
INSERT INTO DEPARTAMENTOS VALUES (10, 'CONTABILIDAD', 'SEVILLA');
INSERT INTO DEPARTAMENTOS VALUES (20, 'INVESTIGACIÓN', 'MADRID');
INSERT INTO DEPARTAMENTOS VALUES (30, 'VENTAS', 'BARCELONA');

INSERT INTO EMPLEADOS VALUES (7369, 'SÁNCHEZ', 'VENDEDOR', 7902, '2023-12-17', 1040, NULL, 20);
INSERT INTO EMPLEADOS VALUES (7499, 'ARROYO', 'VENDEDOR', 7698, '2023-02-20', 1500, 300, 30);
INSERT INTO EMPLEADOS VALUES (7566, 'JIMÉNEZ', 'DIRECTOR', 7839, '2023-04-02', 2975, NULL, 20);
INSERT INTO EMPLEADOS VALUES (7788, 'GIL', 'ANALISTA', 7566, '2024-11-09', 3000, NULL, 20);
INSERT INTO EMPLEADOS VALUES (7902, 'FERNÁNDEZ', 'ANALISTA', 7566, '2023-12-03', 3000, NULL, 20);