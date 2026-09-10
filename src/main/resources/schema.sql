-- =====================================================
-- SICA - Sistema Integrado de Control de Acceso
-- Esquema de base de datos MySQL
-- =====================================================

CREATE DATABASE IF NOT EXISTS campus
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus;

-- =====================================================
-- TABLAS DE SEGURIDAD (RBAC)
-- =====================================================

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS permisos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS rol_permisos (
    rol_id BIGINT NOT NULL,
    permiso_id BIGINT NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    CONSTRAINT fk_rolperm_rol FOREIGN KEY (rol_id) REFERENCES roles(id),
    CONSTRAINT fk_rolperm_permiso FOREIGN KEY (permiso_id) REFERENCES permisos(id)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(200) NOT NULL,
    correo_electronico VARCHAR(100) UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usurol_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_usurol_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- =====================================================
-- TABLAS DE CONSULTA PARA ESTADOS (CATÁLOGOS)
-- =====================================================

-- Posibles estados de acceso de una persona
CREATE TABLE IF NOT EXISTS persona_estados_acceso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(50) NOT NULL UNIQUE -- 'Activo', 'Con Prohibicion de Ingreso'
);

-- Posibles estados de una visita
CREATE TABLE IF NOT EXISTS visita_estados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(50) NOT NULL UNIQUE -- 'Dentro', 'Fuera', 'Pendiente de Aprobacion', etc.
);

-- =====================================================
-- TABLAS DE NEGOCIO
-- =====================================================

CREATE TABLE IF NOT EXISTS empresas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL UNIQUE,
    ubicacion VARCHAR(255),
    contacto_principal VARCHAR(100),
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS funcionarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    empresa_id BIGINT NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    cargo VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_func_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_func_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

CREATE TABLE IF NOT EXISTS personas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    documento VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    foto_url VARCHAR(500),
    empresa_id BIGINT,
    tipo ENUM('INVITADO', 'TRABAJADOR') NOT NULL,
    bloqueada BOOLEAN NOT NULL DEFAULT FALSE,
    motivo_bloqueo VARCHAR(500),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_persona_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

CREATE TABLE IF NOT EXISTS vehiculos (
    placa VARCHAR(20) PRIMARY KEY,
    marca VARCHAR(100),
    tipo VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS activos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL,
    numero_serie VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS visitas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_id BIGINT NOT NULL,
    funcionario_id BIGINT,
    empresa_id BIGINT,
    registrado_por_id BIGINT,
    fecha_hora_programada TIMESTAMP,
    fecha_hora_checkin TIMESTAMP,
    fecha_hora_checkout TIMESTAMP,
    estado ENUM(
        'PENDIENTE_APROBACION',
        'PENDIENTE_APROBACION_OLVIDO',
        'APROBADO',
        'DENTRO',
        'CERRADA',
        'CERRADA_POR_SISTEMA',
        'RECHAZADO'
    ) NOT NULL DEFAULT 'PENDIENTE_APROBACION',
    placa_vehicular VARCHAR(20),
    motivo TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_visita_persona FOREIGN KEY (persona_id) REFERENCES personas(id),
    CONSTRAINT fk_visita_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id),
    CONSTRAINT fk_visita_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
    CONSTRAINT fk_visita_registrado_por FOREIGN KEY (registrado_por_id) REFERENCES usuarios(id),
    CONSTRAINT fk_visita_vehiculo FOREIGN KEY (placa_vehicular) REFERENCES vehiculos(placa)
);

CREATE TABLE IF NOT EXISTS visita_activos (
    visita_id BIGINT NOT NULL,
    activo_id BIGINT NOT NULL,
    PRIMARY KEY (visita_id, activo_id),
    CONSTRAINT fk_va_visita FOREIGN KEY (visita_id) REFERENCES visitas(id) ON DELETE CASCADE,
    CONSTRAINT fk_va_activo FOREIGN KEY (activo_id) REFERENCES activos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS incidentes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    visita_id BIGINT,
    persona_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    descripcion TEXT NOT NULL,
    gravedad ENUM('BAJA', 'MEDIA', 'ALTA', 'CRITICA') NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inc_visita FOREIGN KEY (visita_id) REFERENCES visitas(id),
    CONSTRAINT fk_inc_persona FOREIGN KEY (persona_id) REFERENCES personas(id),
    CONSTRAINT fk_inc_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS bitacora_auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    usuario_username VARCHAR(50),
    accion VARCHAR(100) NOT NULL,
    entidad VARCHAR(100),
    entidad_id BIGINT,
    detalle TEXT,
    ip_address VARCHAR(45),
    punto_acceso VARCHAR(255),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_bitacora_usuario (usuario_id),
    INDEX idx_bitacora_entidad (entidad),
    INDEX idx_bitacora_fecha (fecha)
) ENGINE=InnoDB;

-- Índices adicionales para consultas frecuentes
CREATE INDEX idx_visita_persona_estado ON visitas(persona_id, estado);
CREATE INDEX idx_visita_estado ON visitas(estado);
CREATE INDEX idx_visita_fechas ON visitas(fecha_hora_checkin, fecha_hora_checkout);
CREATE INDEX idx_visita_empresa ON visitas(empresa_id);
CREATE INDEX idx_visita_registrado_por ON visitas(registrado_por_id);
CREATE INDEX idx_persona_documento ON personas(documento);
CREATE INDEX idx_persona_bloqueada ON personas(bloqueada);
CREATE INDEX idx_funcionario_empresa ON funcionarios(empresa_id);
CREATE INDEX idx_incidente_fecha ON incidentes(fecha);
CREATE INDEX idx_roles_activo ON roles(activo);
CREATE INDEX idx_bitacora_ip ON bitacora_auditoria(ip_address);
