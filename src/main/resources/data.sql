-- =====================================================
-- SICA - Datos iniciales y de prueba
-- =====================================================

USE sica_db;

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
    ('generar_reporte_incidentes', 'Generar reportes de incidentes')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

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
  AND p.codigo IN ('login', 'pre_registrar_invitado', 'aprobar_rechazar', 'registrar_persona', 'registrar_incidente');

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
INSERT INTO usuarios (username, password_hash, nombre_completo, activo) VALUES
    ('admin', 'xoWeOEDC0i+oC89VUV9K5Q==$aUCB33OP4Rv6U2L5a99k0xxz0fPB1YYxy8ZKj8LV5fs=', 'Administrador Principal', TRUE),
    ('guarda1', 'wEEezEgLqd+t5OIKhFHjvg==$lIdFlb1VOmKhIJG4Dvg4l11bJjV39xGkEkPCaT2iE5w=', 'Guarda de Seguridad 1', TRUE),
    ('funcionario1', 'OKaauufh8swmU+YinrrhOA==$Rknj3C3t/MtqXtJzCa4T+sBWVavHPH8nB7l6axJUlug=', 'Funcionario Empresa A', TRUE)
ON DUPLICATE KEY UPDATE
    password_hash = VALUES(password_hash),
    nombre_completo = VALUES(nombre_completo),
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
INSERT INTO empresas (nombre, ubicacion, activa) VALUES
    ('Empresa A', 'Edificio Norte, Piso 3', TRUE),
    ('Empresa B', 'Edificio Sur, Piso 2', TRUE),
    ('Empresa C', 'Edificio Central, Piso 1', TRUE)
ON DUPLICATE KEY UPDATE ubicacion = VALUES(ubicacion);

-- =====================================================
-- FUNCIONARIOS DE PRUEBA
-- =====================================================
INSERT IGNORE INTO funcionarios (usuario_id, empresa_id, nombre, cargo, activo) VALUES
    ((SELECT id FROM usuarios WHERE username = 'funcionario1'),
     (SELECT id FROM empresas WHERE nombre = 'Empresa A'),
     'Juan Pérez', 'Recepcionista', TRUE);

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
INSERT IGNORE INTO visitas (persona_id, funcionario_id, fecha_hora_programada, estado, observaciones) VALUES
    ((SELECT id FROM personas WHERE documento = '1234567890'),
     (SELECT id FROM funcionarios WHERE nombre = 'Juan Pérez'),
     DATE_ADD(NOW(), INTERVAL 1 HOUR),
     'APROBADO',
     'Visita pre-registrada de prueba');

-- =====================================================
-- BITÁCORA INICIAL
-- =====================================================
INSERT IGNORE INTO bitacora_auditoria (usuario_id, usuario_username, accion, entidad, entidad_id, detalle) VALUES
    (NULL, 'sistema', 'INICIALIZACION', 'BASE_DATOS', NULL, 'Base de datos poblada con datos iniciales de prueba');
