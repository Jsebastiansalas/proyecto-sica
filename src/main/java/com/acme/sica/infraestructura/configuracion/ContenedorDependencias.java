package com.acme.sica.infraestructura.configuracion;

import com.acme.sica.aplicacion.autenticacion.IniciarSesionServicio;
import com.acme.sica.aplicacion.bitacora.ConsultarBitacoraServicio;
import com.acme.sica.aplicacion.empresa.AuditoriaEmpresaDecorador;
import com.acme.sica.aplicacion.empresa.GestionarEmpresaServicio;
import com.acme.sica.aplicacion.funcionario.AuditoriaFuncionarioDecorador;
import com.acme.sica.aplicacion.funcionario.GestionarFuncionarioServicio;
import com.acme.sica.aplicacion.permiso.AuditoriaPermisoDecorador;
import com.acme.sica.aplicacion.permiso.GestionarPermisoServicio;
import com.acme.sica.aplicacion.persona.AuditoriaPersonaDecorador;
import com.acme.sica.aplicacion.persona.GestionarPersonaServicio;
import com.acme.sica.aplicacion.rol.AuditoriaRolDecorador;
import com.acme.sica.aplicacion.rol.GestionarRolServicio;
import com.acme.sica.aplicacion.visita.AuditoriaPreRegistrarInvitadoDecorador;
import com.acme.sica.aplicacion.visita.PreRegistrarInvitadoServicio;
import com.acme.sica.dominio.puerto.entrada.ConsultarBitacoraCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarEmpresaCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarFuncionarioCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarPersonaCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarPermisoCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarRolCasoUso;
import com.acme.sica.dominio.puerto.entrada.IniciarSesionCasoUso;
import com.acme.sica.dominio.puerto.entrada.PreRegistrarInvitadoCasoUso;
import com.acme.sica.dominio.puerto.salida.*;
import com.acme.sica.infraestructura.persistencia.jdbc.FabricaConexiones;
import com.acme.sica.infraestructura.persistencia.jdbc.InicializadorBaseDatos;
import com.acme.sica.infraestructura.persistencia.jdbc.RepositorioJdbcBitacora;
import com.acme.sica.infraestructura.persistencia.jdbc.RepositorioJdbcEmpresa;
import com.acme.sica.infraestructura.persistencia.jdbc.RepositorioJdbcFuncionario;
import com.acme.sica.infraestructura.persistencia.jdbc.RepositorioJdbcIncidente;
import com.acme.sica.infraestructura.persistencia.jdbc.RepositorioJdbcPermiso;
import com.acme.sica.infraestructura.persistencia.jdbc.RepositorioJdbcPersona;
import com.acme.sica.infraestructura.persistencia.jdbc.RepositorioJdbcRol;
import com.acme.sica.infraestructura.persistencia.jdbc.RepositorioJdbcUsuario;
import com.acme.sica.infraestructura.persistencia.jdbc.RepositorioJdbcVisita;
import com.acme.sica.infraestructura.seguridad.HasheadorContrasenas;
import com.acme.sica.infraestructura.seguridad.autorizacion.FabricaCadenaAutorizacion;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

/**
 * Contenedor manual de dependencias.
 * Se encarga de instanciar y conectar todos los adaptadores, servicios y casos de uso.
 * A medida que avancemos, este archivo irá creciendo con cada funcionalidad nueva.
 */
public class ContenedorDependencias {

    private final ConfiguracionBaseDatos configuracionBaseDatos;
    private final FabricaConexiones fabricaConexiones;
    private final InicializadorBaseDatos inicializadorBaseDatos;

    private final HasheadorContrasenas hasheadorContrasenas;
    private final ManejadorAutorizacion cadenaAutorizacion;

    private final IniciarSesionCasoUso iniciarSesionCasoUso;
    private final GestionarRolCasoUso gestionarRolCasoUso;
    private final GestionarPermisoCasoUso gestionarPermisoCasoUso;
    private final GestionarEmpresaCasoUso gestionarEmpresaCasoUso;
    private final GestionarFuncionarioCasoUso gestionarFuncionarioCasoUso;
    private final GestionarPersonaCasoUso gestionarPersonaCasoUso;
    private final PreRegistrarInvitadoCasoUso preRegistrarInvitadoCasoUso;
    private final ConsultarBitacoraCasoUso consultarBitacoraCasoUso;

    private final UsuarioRepositorioPuerto usuarioRepositorio;
    private final RolRepositorioPuerto rolRepositorio;
    private final PermisoRepositorioPuerto permisoRepositorio;
    private final EmpresaRepositorioPuerto empresaRepositorio;
    private final FuncionarioRepositorioPuerto funcionarioRepositorio;
    private final PersonaRepositorioPuerto personaRepositorio;
    private final VisitaRepositorioPuerto visitaRepositorio;
    private final IncidenteRepositorioPuerto incidenteRepositorio;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public ContenedorDependencias(ConfiguracionBaseDatos configuracionBaseDatos) {
        this.configuracionBaseDatos = configuracionBaseDatos;
        this.fabricaConexiones = new FabricaConexiones(configuracionBaseDatos);
        this.inicializadorBaseDatos = new InicializadorBaseDatos(fabricaConexiones);

        this.hasheadorContrasenas = new HasheadorContrasenas();

        this.usuarioRepositorio = new RepositorioJdbcUsuario(fabricaConexiones);
        this.rolRepositorio = new RepositorioJdbcRol(fabricaConexiones);
        this.permisoRepositorio = new RepositorioJdbcPermiso(fabricaConexiones);
        this.empresaRepositorio = new RepositorioJdbcEmpresa(fabricaConexiones);
        this.funcionarioRepositorio = new RepositorioJdbcFuncionario(fabricaConexiones);
        this.personaRepositorio = new RepositorioJdbcPersona(fabricaConexiones);
        this.visitaRepositorio = new RepositorioJdbcVisita(fabricaConexiones);
        this.incidenteRepositorio = new RepositorioJdbcIncidente(fabricaConexiones);
        this.bitacoraRepositorio = new RepositorioJdbcBitacora(fabricaConexiones);

        this.cadenaAutorizacion = FabricaCadenaAutorizacion.crear(bitacoraRepositorio);

        this.iniciarSesionCasoUso = new IniciarSesionServicio(
                usuarioRepositorio,
                hasheadorContrasenas,
                bitacoraRepositorio
        );

        this.gestionarRolCasoUso = new AuditoriaRolDecorador(
                new GestionarRolServicio(rolRepositorio, usuarioRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.gestionarPermisoCasoUso = new AuditoriaPermisoDecorador(
                new GestionarPermisoServicio(permisoRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.gestionarEmpresaCasoUso = new AuditoriaEmpresaDecorador(
                new GestionarEmpresaServicio(empresaRepositorio, funcionarioRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.gestionarFuncionarioCasoUso = new AuditoriaFuncionarioDecorador(
                new GestionarFuncionarioServicio(funcionarioRepositorio, empresaRepositorio,
                        usuarioRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.gestionarPersonaCasoUso = new AuditoriaPersonaDecorador(
                new GestionarPersonaServicio(personaRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.preRegistrarInvitadoCasoUso = new AuditoriaPreRegistrarInvitadoDecorador(
                new PreRegistrarInvitadoServicio(visitaRepositorio, personaRepositorio,
                        funcionarioRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.consultarBitacoraCasoUso = new ConsultarBitacoraServicio(
                bitacoraRepositorio,
                cadenaAutorizacion
        );
    }

    public ConfiguracionBaseDatos getConfiguracionBaseDatos() {
        return configuracionBaseDatos;
    }

    public FabricaConexiones getFabricaConexiones() {
        return fabricaConexiones;
    }

    public InicializadorBaseDatos getInicializadorBaseDatos() {
        return inicializadorBaseDatos;
    }

    public UsuarioRepositorioPuerto getUsuarioRepositorio() {
        return usuarioRepositorio;
    }

    public RolRepositorioPuerto getRolRepositorio() {
        return rolRepositorio;
    }

    public PermisoRepositorioPuerto getPermisoRepositorio() {
        return permisoRepositorio;
    }

    public EmpresaRepositorioPuerto getEmpresaRepositorio() {
        return empresaRepositorio;
    }

    public FuncionarioRepositorioPuerto getFuncionarioRepositorio() {
        return funcionarioRepositorio;
    }

    public PersonaRepositorioPuerto getPersonaRepositorio() {
        return personaRepositorio;
    }

    public VisitaRepositorioPuerto getVisitaRepositorio() {
        return visitaRepositorio;
    }

    public IncidenteRepositorioPuerto getIncidenteRepositorio() {
        return incidenteRepositorio;
    }

    public BitacoraRepositorioPuerto getBitacoraRepositorio() {
        return bitacoraRepositorio;
    }

    public IniciarSesionCasoUso getIniciarSesionCasoUso() {
        return iniciarSesionCasoUso;
    }

    public GestionarRolCasoUso getGestionarRolCasoUso() {
        return gestionarRolCasoUso;
    }

    public GestionarPermisoCasoUso getGestionarPermisoCasoUso() {
        return gestionarPermisoCasoUso;
    }

    public ConsultarBitacoraCasoUso getConsultarBitacoraCasoUso() {
        return consultarBitacoraCasoUso;
    }

    public GestionarEmpresaCasoUso getGestionarEmpresaCasoUso() {
        return gestionarEmpresaCasoUso;
    }

    public GestionarFuncionarioCasoUso getGestionarFuncionarioCasoUso() {
        return gestionarFuncionarioCasoUso;
    }

    public GestionarPersonaCasoUso getGestionarPersonaCasoUso() {
        return gestionarPersonaCasoUso;
    }

    public PreRegistrarInvitadoCasoUso getPreRegistrarInvitadoCasoUso() {
        return preRegistrarInvitadoCasoUso;
    }

    public HasheadorContrasenas getHasheadorContrasenas() {
        return hasheadorContrasenas;
    }

    public ManejadorAutorizacion getCadenaAutorizacion() {
        return cadenaAutorizacion;
    }

}
