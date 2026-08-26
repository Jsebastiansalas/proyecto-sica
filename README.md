# SICA - Sistema Integrado de Control de Acceso

Sistema de control de acceso para el complejo empresarial **Zona Acme** (30+ empresas), que reemplaza el registro manual en papel por una solución de software en Java puro con JavaFX y MySQL.

## Tecnologías

- Java 17 (sin frameworks — JDBC y inyección de dependencias manual)
- JavaFX 21 (interfaz gráfica)
- Maven (gestión de dependencias y ejecución)
- JDBC + MySQL 8 (persistencia)
- Arquitectura Hexagonal + Vertical Slice+
- Patrones: Chain of Responsibility, Decorator, Strategy

## Roles del sistema

- **Administrador**: gestiona usuarios, roles, permisos, empresas y funcionarios.
- **Funcionario de Empresa**: pre-registra invitados y aprueba/rechaza accesos.
- **Guarda de Seguridad**: opera puntos de entrada/salida.
- **Sistema**: regulariza salidas olvidadas automáticamente.

## Credenciales de prueba

| Usuario       | Contraseña      | Rol                  |
|---------------|-----------------|----------------------|
| admin         | admin123        | Administrador        |
| guarda1       | guarda123       | Guarda de Seguridad  |
| funcionario1  | funcionario123  | Funcionario Empresa  |

## Ejecución

```bash
mvn javafx:run
```

## Estructura del proyecto

```
src/main/java/com/acme/sica/
├── Principal.java             # Punto de entrada
├── dominio/                   # Núcleo del negocio (sin dependencias externas)
│   ├── modelo/                # Usuario, Rol, Permiso, Empresa, Funcionario,
│   │                          # Persona, Visita, Incidente, BitacoraAuditoria
│   ├── modelo/enumerados/     # EstadoVisita, TipoPersona, GravedadIncidente, ...
│   ├── excepciones/
│   └── puerto/salida/         # Interfaces de repositorios (puertos hexagonales)
└── infraestructura/
    ├── configuracion/         # ConfiguracionBaseDatos, ContenedorDependencias
    ├── persistencia/jdbc/     # RepositorioJdbc*, FabricaConexiones, InicializadorBaseDatos
    ├── seguridad/             # HasheadorContrasenas (PBKDF2)
    └── ui/javafx/             # AplicacionJavaFx (adaptador de entrada)
```

## Estado

Proyecto en construcción.
