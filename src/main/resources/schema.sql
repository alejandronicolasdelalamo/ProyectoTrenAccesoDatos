DROP TABLE IF EXISTS trenes;

CREATE TABLE trenes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fabricante VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    matricula VARCHAR(50) NOT NULL UNIQUE,
    anio INT NOT NULL,
    precio DECIMAL(12,2) NOT NULL,
    velocidad_maxima INT NOT NULL,
    propulsion VARCHAR(50) NOT NULL,
    tipo_servicio VARCHAR(50) NOT NULL,
    color VARCHAR(50)
);

-- Log de transacciones: sin DROP para que el historial se conserve entre arranques
CREATE TABLE IF NOT EXISTS log_transacciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operacion VARCHAR(20) NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    entidad_id BIGINT,
    detalle VARCHAR(500),
    fecha DATETIME(6) NOT NULL
);