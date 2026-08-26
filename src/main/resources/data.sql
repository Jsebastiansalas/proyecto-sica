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
    ('SISTEMA', 'Actor automático del sistema');

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
    ('generar_reporte_incidentes', 'Generar reportes de incidentes');

-- =====================================================
-- ASIGNACIÓN DE PERMISOS A ROLES
-- =====================================================
-- ADMINISTRADOR: todos los permisos
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM roles r, permisos p
WHERE r.nombre = 'ADMINISTRADOR';

-- FUNCIONARIO_EMPRESA
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM roles r, permisos p
WHERE r.nombre = 'FUNCIONARIO_EMPRESA'
  AND p.codigo IN ('login', 'pre_registrar_invitado', 'aprobar_rechazar', 'registrar_persona', 'registrar_incidente');

-- GUARDA_SEGURIDAD
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM roles r, permisos p
WHERE r.nombre = 'GUARDA_SEGURIDAD'
  AND p.codigo IN ('login', 'check_in_invitado', 'registrar_no_anunciado', 'registrar_trabajador',
                   'check_out', 'registrar_persona', 'registrar_incidente');

-- SISTEMA
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM roles r, permisos p
WHERE r.nombre = 'SISTEMA'
  AND p.codigo IN ('regularizar_salida');

-- =====================================================
-- USUARIOS DE PRUEBA
-- Contraseñas hasheadas con PBKDF2 (PasswordHasher)
-- =====================================================
INSERT INTO usuarios (username, password_hash, nombre_completo, activo) VALUES
    ('admin', '1JS4alcj20uRyNyfQhT4zA==$pCu7pPuZUp/WKAkrzPLwxgXbcj/gdKElvQoE3uctUVo=', 'Administrador Principal', TRUE),
    ('guarda1', 'ik6F6MJU4H9qJ1JgCMgHGQ==$IIp03J0QobCQ1DGX8SmPXGsWd9Tc2G7vtUzssSrWpt8=', 'Guarda de Seguridad 1', TRUE),
    ('funcionario1', 'wS5dMPgr8VSqNiVyo+x2eQ==$eNkGhgLjQ+Va2MqYA0W0w1YvuE/eG1cENKd0JmSpH3Q=', 'Funcionario Empresa A', TRUE);

-- =====================================================
-- ASIGNACIÓN DE ROLES A USUARIOS
-- =====================================================
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES
    ((SELECT id FROM usuarios WHERE username = 'admin'), (SELECT id FROM roles WHERE nombre = 'ADMINISTRADOR')),
    ((SELECT id FROM usuarios WHERE username = 'guarda1'), (SELECT id FROM roles WHERE nombre = 'GUARDA_SEGURIDAD')),
    ((SELECT id FROM usuarios WHERE username = 'funcionario1'), (SELECT id FROM roles WHERE nombre = 'FUNCIONARIO_EMPRESA'));

-- =====================================================
-- EMPRESAS DE PRUEBA
-- =====================================================
INSERT INTO empresas (nombre, ubicacion, activa) VALUES
    ('Empresa A', 'Edificio Norte, Piso 3', TRUE),
    ('Empresa B', 'Edificio Sur, Piso 2', TRUE),
    ('Empresa C', 'Edificio Central, Piso 1', TRUE);

-- =====================================================
-- FUNCIONARIOS DE PRUEBA
-- =====================================================
INSERT INTO funcionarios (usuario_id, empresa_id, nombre, cargo, activo) VALUES
    ((SELECT id FROM usuarios WHERE username = 'funcionario1'),
     (SELECT id FROM empresas WHERE nombre = 'Empresa A'),
     'Juan Pérez', 'Recepcionista', TRUE);

-- =====================================================
-- PERSONAS DE PRUEBA
-- =====================================================
INSERT INTO personas (documento, nombre, foto_url, tipo, bloqueada) VALUES
    ('1234567890', 'Carlos Invitado', 'https://example.com/fotos/carlos.jpg', 'INVITADO', FALSE),
    ('0987654321', 'Ana Trabajadora', 'https://example.com/fotos/ana.jpg', 'TRABAJADOR', FALSE),
    ('1122334455', 'Pedro Sospechoso', 'https://example.com/fotos/pedro.jpg', 'INVITADO', TRUE);

-- =====================================================
-- VISITAS DE PRUEBA
-- =====================================================
INSERT INTO visitas (persona_id, funcionario_id, fecha_hora_programada, estado, observaciones) VALUES
    ((SELECT id FROM personas WHERE documento = '1234567890'),
     (SELECT id FROM funcionarios WHERE nombre = 'Juan Pérez'),
     DATE_ADD(NOW(), INTERVAL 1 HOUR),
     'APROBADO',
     'Visita pre-registrada de prueba');

-- =====================================================
-- BITÁCORA INICIAL
-- =====================================================
INSERT INTO bitacora_auditoria (usuario_id, usuario_username, accion, entidad, entidad_id, detalle) VALUES
    (NULL, 'sistema', 'INICIALIZACION', 'BASE_DATOS', NULL, 'Base de datos poblada con datos iniciales de prueba');