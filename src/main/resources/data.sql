-- =====================================================
-- SICA - Datos iniciales y de prueba
-- =====================================================

USE campus;

-- =====================================================
-- ROLES
-- =====================================================
INSERT INTO roles (nombre, descripcion) VALUES
    ('ADMINISTRADOR', 'Gestiona usuarios, roles, permisos y configuración general'),
    ('FUNCIONARIO_EMPRESA', 'Pre-registra invitados y aprueba/rechaza accesos'),
    ('GUARDA_SEGURIDAD', 'Opera puntos de entrada/salida'),
    ('SISTEMA', 'Actor automático del sistema')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- =====================================================
-- PERMISOS (catálogo)
-- =====================================================
INSERT INTO permisos (codigo, descripcion) VALUES
    ('login', 'Iniciar sesión en el sistema'),
    ('gestionar_roles', 'Crear, editar y eliminar roles'),
    ('gestionar_permisos', 'Definir y asignar permisos'),
    ('asignar_roles', 'Asignar roles a usuarios'),
    ('consultar_bitacora', 'Consultar historial de auditoría'),
    ('gestionar_empresas', 'Registrar y editar empresas'),
    ('gestionar_funcionarios', 'Registrar y editar funcionarios'),
    ('registrar_persona', 'Registrar invitados y trabajadores'),
    ('pre_registrar_invitado', 'Pre-registrar invitados con fecha de visita'),
    ('check_in_invitado', 'Confirmar ingreso de invitados'),
    ('registrar_no_anunciado', 'Registrar invitados no anunciados'),
    ('aprobar_rechazar', 'Aprobar o rechazar accesos pendientes'),
    ('registrar_trabajador', 'Registrar trabajadores sin carnet'),
    ('regularizar_salida', 'Regularizar salidas olvidadas'),
    ('check_out', 'Registrar salida de personas'),
    ('registrar_incidente', 'Registrar incidentes de seguridad'),
    ('bloquear_persona', 'Bloquear o desbloquear personas'),
    ('generar_reporte_accesos', 'Generar reportes de accesos'),
    ('generar_reporte_incidentes', 'Generar reportes de incidentes'),
    ('gestionar_usuarios', 'Crear, editar y eliminar usuarios'),
    ('ver_personal_presente', 'Ver el personal presente en el complejo de su empresa')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- =====================================================
-- CATÁLOGO DE ESTADOS DE ACCESO DE PERSONAS
-- =====================================================
INSERT IGNORE INTO persona_estados_acceso (nombre_estado) VALUES
    ('Activo'),
    ('Con Prohibicion de Ingreso');

-- =====================================================
-- CATÁLOGO DE ESTADOS DE VISITA
-- =====================================================
INSERT IGNORE INTO visita_estados (nombre_estado) VALUES
    ('Dentro'),
    ('Fuera'),
    ('Pendiente de Aprobacion'),
    ('Pendiente de Aprobacion por Olvido'),
    ('Aprobado'),
    ('Rechazado'),
    ('Cerrada'),
    ('Cerrada por Sistema'),
    ('Expirado');

-- =====================================================
-- ASIGNACIÓN DE PERMISOS A ROLES
-- =====================================================
-- ADMINISTRADOR: todos los permisos
INSERT IGNORE INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM roles r, permisos p
WHERE r.nombre = 'ADMINISTRADOR';

-- FUNCIONARIO_EMPRESA
INSERT IGNORE INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM roles r, permisos p
WHERE r.nombre = 'FUNCIONARIO_EMPRESA'
  AND p.codigo IN ('login', 'pre_registrar_invitado', 'aprobar_rechazar', 'registrar_persona', 'registrar_incidente', 'ver_personal_presente');

-- GUARDA_SEGURIDAD
INSERT IGNORE INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM roles r, permisos p
WHERE r.nombre = 'GUARDA_SEGURIDAD'
  AND p.codigo IN ('login', 'check_in_invitado', 'registrar_no_anunciado', 'registrar_trabajador',
                   'check_out', 'registrar_persona', 'registrar_incidente');

-- SISTEMA
INSERT IGNORE INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM roles r, permisos p
WHERE r.nombre = 'SISTEMA'
  AND p.codigo IN ('regularizar_salida');

-- =====================================================
-- USUARIOS DE PRUEBA
-- Contraseñas hasheadas con PBKDF2 (PasswordHasher)
-- =====================================================
INSERT INTO usuarios (username, password_hash, nombre_completo, correo_electronico, activo) VALUES
    ('admin', 'xoWeOEDC0i+oC89VUV9K5Q==$aUCB33OP4Rv6U2L5a99k0xxz0fPB1YYxy8ZKj8LV5fs=', 'Administrador Principal', 'admin@acme.com', TRUE),
    ('guarda1', 'wEEezEgLqd+t5OIKhFHjvg==$lIdFlb1VOmKhIJG4Dvg4l11bJjV39xGkEkPCaT2iE5w=', 'Guarda de Seguridad 1', 'guarda1@acme.com', TRUE),
    ('funcionario1', 'OKaauufh8swmU+YinrrhOA==$Rknj3C3t/MtqXtJzCa4T+sBWVavHPH8nB7l6axJUlug=', 'Funcionario Empresa A', 'funcionario1@acme.com', TRUE)
ON DUPLICATE KEY UPDATE
    password_hash = VALUES(password_hash),
    nombre_completo = VALUES(nombre_completo),
    correo_electronico = VALUES(correo_electronico),
    activo = VALUES(activo);

-- =====================================================
-- ASIGNACIÓN DE ROLES A USUARIOS
-- =====================================================
INSERT IGNORE INTO usuario_roles (usuario_id, rol_id) VALUES
    ((SELECT id FROM usuarios WHERE username = 'admin'), (SELECT id FROM roles WHERE nombre = 'ADMINISTRADOR')),
    ((SELECT id FROM usuarios WHERE username = 'guarda1'), (SELECT id FROM roles WHERE nombre = 'GUARDA_SEGURIDAD')),
    ((SELECT id FROM usuarios WHERE username = 'funcionario1'), (SELECT id FROM roles WHERE nombre = 'FUNCIONARIO_EMPRESA'));

-- =====================================================
-- EMPRESAS DE PRUEBA
-- =====================================================
INSERT INTO empresas (nombre, ubicacion, contacto_principal, activa) VALUES
    ('Empresa A', 'Edificio Norte, Piso 3', 'contacto.a@empresaa.com', TRUE),
    ('Empresa B', 'Edificio Sur, Piso 2', 'contacto.b@empresab.com', TRUE),
    ('Empresa C', 'Edificio Central, Piso 1', 'contacto.c@empresac.com', TRUE)
ON DUPLICATE KEY UPDATE ubicacion = VALUES(ubicacion), contacto_principal = VALUES(contacto_principal);

-- =====================================================
-- FUNCIONARIOS DE PRUEBA
-- =====================================================
INSERT INTO funcionarios (usuario_id, empresa_id, nombre, cargo, activo)
SELECT u.id, e.id, 'Juan Pérez', 'Recepcionista', TRUE
FROM usuarios u
JOIN empresas e ON e.nombre = 'Empresa A'
WHERE u.username = 'funcionario1'
  AND NOT EXISTS (
      SELECT 1 FROM funcionarios f WHERE f.usuario_id = u.id
  );

-- =====================================================
-- PERSONAS DE PRUEBA
-- =====================================================
INSERT INTO personas (documento, nombre, foto_url, tipo, bloqueada) VALUES
    ('1234567890', 'Carlos Invitado', 'https://example.com/fotos/carlos.jpg', 'INVITADO', FALSE),
    ('0987654321', 'Ana Trabajadora', 'https://example.com/fotos/ana.jpg', 'TRABAJADOR', FALSE),
    ('1122334455', 'Pedro Sospechoso', 'https://example.com/fotos/pedro.jpg', 'INVITADO', TRUE)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- =====================================================
-- VISITAS DE PRUEBA
-- =====================================================
INSERT INTO visitas (persona_id, funcionario_id, empresa_id, fecha_hora_programada, fecha_hora_checkin, estado, motivo)
SELECT p.id, f.id, e.id, DATE_ADD(NOW(), INTERVAL 1 HOUR), NOW(), 'APROBADO', 'Visita pre-registrada de prueba'
FROM personas p
JOIN funcionarios f ON f.nombre = 'Juan Pérez'
JOIN empresas e ON e.nombre = 'Empresa A'
WHERE p.documento = '1234567890'
  AND NOT EXISTS (
      SELECT 1 FROM visitas v WHERE v.motivo = 'Visita pre-registrada de prueba'
  );

INSERT INTO visitas (persona_id, funcionario_id, empresa_id, fecha_hora_programada, fecha_hora_checkin, estado, motivo)
SELECT p.id, f.id, e.id, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), 'DENTRO', 'Ana en el complejo'
FROM personas p
JOIN funcionarios f ON f.nombre = 'Juan Pérez'
JOIN empresas e ON e.nombre = 'Empresa A'
WHERE p.documento = '0987654321'
  AND NOT EXISTS (
      SELECT 1 FROM visitas v WHERE v.motivo = 'Ana en el complejo'
  );

INSERT INTO visitas (persona_id, funcionario_id, empresa_id, fecha_hora_programada, fecha_hora_checkin, estado, motivo)
SELECT p.id, f.id, e.id, DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR), 'DENTRO', 'Carlos en el complejo'
FROM personas p
JOIN funcionarios f ON f.nombre = 'Juan Pérez'
JOIN empresas e ON e.nombre = 'Empresa A'
WHERE p.documento = '1234567890'
  AND NOT EXISTS (
      SELECT 1 FROM visitas v WHERE v.motivo = 'Carlos en el complejo'
  );

-- =====================================================
-- VEHICULOS Y ACTIVOS DE PRUEBA
-- =====================================================
INSERT INTO vehiculos (placa, marca, tipo) VALUES
    ('ABC-123', 'Toyota', 'SUV'),
    ('XYZ-987', 'Nissan', 'Camioneta'),
    ('LKM-456', 'Hyundai', 'Sedán')
ON DUPLICATE KEY UPDATE marca = VALUES(marca);

INSERT INTO activos (descripcion, numero_serie)
SELECT 'Laptop corporativa', 'SN-LAP-001'
WHERE NOT EXISTS (SELECT 1 FROM activos WHERE numero_serie = 'SN-LAP-001');

INSERT INTO activos (descripcion, numero_serie)
SELECT 'Herramienta de mantenimiento', 'SN-HERR-002'
WHERE NOT EXISTS (SELECT 1 FROM activos WHERE numero_serie = 'SN-HERR-002');

INSERT INTO activos (descripcion, numero_serie)
SELECT 'Equipo de medición', 'SN-MED-003'
WHERE NOT EXISTS (SELECT 1 FROM activos WHERE numero_serie = 'SN-MED-003');

-- =====================================================
-- BIT�?CORA INICIAL
-- =====================================================
INSERT IGNORE INTO bitacora_auditoria (usuario_id, usuario_username, accion, entidad, entidad_id, detalle, punto_acceso) VALUES
    (NULL, 'sistema', 'INICIALIZACION', 'BASE_DATOS', NULL, 'Base de datos poblada con datos iniciales de prueba', 'SISTEMA');
