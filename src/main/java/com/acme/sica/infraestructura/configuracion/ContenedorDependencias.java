package com.acme.sica.infraestructura.configuracion;

import com.acme.sica.aplicacion.autenticacion.IniciarSesionServicio;
import com.acme.sica.aplicacion.bitacora.ConsultarBitacoraServicio;
import com.acme.sica.aplicacion.empresa.AuditoriaEmpresaDecorador;
import com.acme.sica.aplicacion.empresa.GestionarEmpresaServicio;
import com.acme.sica.aplicacion.funcionario.AuditoriaFuncionarioDecorador;
import com.acme.sica.aplicacion.funcionario.GestionarFuncionarioServicio;
import com.acme.sica.aplicacion.incidente.AuditoriaIncidenteDecorador;
import com.acme.sica.aplicacion.incidente.GestionarIncidenteServicio;
import com.acme.sica.aplicacion.permiso.AuditoriaPermisoDecorador;
import com.acme.sica.aplicacion.permiso.GestionarPermisoServicio;
import com.acme.sica.aplicacion.persona.AuditoriaPersonaDecorador;
import com.acme.sica.aplicacion.persona.GestionarPersonaServicio;
import com.acme.sica.aplicacion.reporte.ConsultarReporteAccesosServicio;
import com.acme.sica.aplicacion.reporte.ConsultarReporteIncidentesServicio;
import com.acme.sica.aplicacion.persona.AuditoriaBloqueoPersonaDecorador;
import com.acme.sica.aplicacion.persona.GestionarBloqueoPersonaServicio;
import com.acme.sica.aplicacion.rol.AuditoriaRolDecorador;
import com.acme.sica.aplicacion.rol.GestionarRolServicio;
import com.acme.sica.aplicacion.usuario.AuditoriaUsuarioDecorador;
import com.acme.sica.aplicacion.usuario.GestionarUsuarioServicio;
import com.acme.sica.aplicacion.visita.AuditoriaPreRegistrarInvitadoDecorador;
import com.acme.sica.aplicacion.visita.PreRegistrarInvitadoServicio;
import com.acme.sica.aplicacion.visita.AuditoriaCheckInDecorador;
import com.acme.sica.aplicacion.visita.CheckInInvitadoServicio;
import com.acme.sica.aplicacion.visita.AuditoriaRegistrarNoAnunciadoDecorador;
import com.acme.sica.aplicacion.visita.RegistrarNoAnunciadoServicio;
import com.acme.sica.aplicacion.visita.AuditoriaAprobarRechazarDecorador;
import com.acme.sica.aplicacion.visita.AprobarRechazarVisitaServicio;
import com.acme.sica.aplicacion.visita.AuditoriaRegistrarTrabajadorDecorador;
import com.acme.sica.aplicacion.visita.RegistrarTrabajadorServicio;
import com.acme.sica.aplicacion.visita.AuditoriaRegularizarDecorador;
import com.acme.sica.aplicacion.visita.RegularizarSalidaServicio;
import com.acme.sica.aplicacion.visita.EstrategiaCierreSistema;
import com.acme.sica.aplicacion.visita.EstrategiaNuevoIngreso;
import com.acme.sica.aplicacion.visita.TipoRegularizacion;
import com.acme.sica.aplicacion.visita.AuditoriaCheckOutDecorador;
import com.acme.sica.aplicacion.visita.CheckOutServicio;
import com.acme.sica.dominio.puerto.entrada.ConsultarBitacoraCasoUso;
import com.acme.sica.dominio.puerto.entrada.CheckInInvitadoCasoUso;
import com.acme.sica.dominio.puerto.entrada.AprobarRechazarVisitaCasoUso;
import com.acme.sica.dominio.puerto.entrada.CheckOutCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarEmpresaCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarFuncionarioCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarIncidenteCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarBloqueoPersonaCasoUso;
import com.acme.sica.dominio.puerto.entrada.ConsultarReporteAccesosCasoUso;
import com.acme.sica.dominio.puerto.entrada.ConsultarReporteIncidentesCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarPersonaCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarPermisoCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarRolCasoUso;
import com.acme.sica.dominio.puerto.entrada.GestionarUsuarioCasoUso;
import com.acme.sica.dominio.puerto.entrada.IniciarSesionCasoUso;
import com.acme.sica.dominio.puerto.entrada.PreRegistrarInvitadoCasoUso;
import com.acme.sica.dominio.puerto.entrada.RegularizarSalidaCasoUso;
import com.acme.sica.dominio.puerto.entrada.RegistrarNoAnunciadoCasoUso;
import com.acme.sica.dominio.puerto.entrada.RegistrarTrabajadorCasoUso;
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

import java.util.HashMap;
import java.util.Map;

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
    private final GestionarUsuarioCasoUso gestionarUsuarioCasoUso;
    private final GestionarPermisoCasoUso gestionarPermisoCasoUso;
    private final GestionarEmpresaCasoUso gestionarEmpresaCasoUso;
    private final GestionarFuncionarioCasoUso gestionarFuncionarioCasoUso;
    private final GestionarIncidenteCasoUso gestionarIncidenteCasoUso;
    private final GestionarBloqueoPersonaCasoUso gestionarBloqueoPersonaCasoUso;
    private final ConsultarReporteAccesosCasoUso consultarReporteAccesosCasoUso;
    private final ConsultarReporteIncidentesCasoUso consultarReporteIncidentesCasoUso;
    private final GestionarPersonaCasoUso gestionarPersonaCasoUso;
    private final PreRegistrarInvitadoCasoUso preRegistrarInvitadoCasoUso;
    private final CheckInInvitadoCasoUso checkInInvitadoCasoUso;
    private final RegistrarNoAnunciadoCasoUso registrarNoAnunciadoCasoUso;
    private final AprobarRechazarVisitaCasoUso aprobarRechazarCasoUso;
    private final RegistrarTrabajadorCasoUso registrarTrabajadorCasoUso;
    private final RegularizarSalidaCasoUso regularizarSalidaCasoUso;
    private final CheckOutCasoUso checkOutCasoUso;
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

        this.gestionarUsuarioCasoUso = new AuditoriaUsuarioDecorador(
                new GestionarUsuarioServicio(usuarioRepositorio, rolRepositorio,
                        cadenaAutorizacion, hasheadorContrasenas),
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

        this.gestionarIncidenteCasoUso = new AuditoriaIncidenteDecorador(
                new GestionarIncidenteServicio(incidenteRepositorio, personaRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.gestionarBloqueoPersonaCasoUso = new AuditoriaBloqueoPersonaDecorador(
                new GestionarBloqueoPersonaServicio(personaRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.consultarReporteAccesosCasoUso = new ConsultarReporteAccesosServicio(
                visitaRepositorio, cadenaAutorizacion);
        this.consultarReporteIncidentesCasoUso = new ConsultarReporteIncidentesServicio(
                incidenteRepositorio, cadenaAutorizacion);

        this.gestionarPersonaCasoUso = new AuditoriaPersonaDecorador(
                new GestionarPersonaServicio(personaRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        EstrategiaCierreSistema estrategiaCierreSistema = new EstrategiaCierreSistema(visitaRepositorio);

        this.preRegistrarInvitadoCasoUso = new AuditoriaPreRegistrarInvitadoDecorador(
                new PreRegistrarInvitadoServicio(visitaRepositorio, personaRepositorio,
                        funcionarioRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.checkInInvitadoCasoUso = new AuditoriaCheckInDecorador(
                new CheckInInvitadoServicio(visitaRepositorio, personaRepositorio,
                        cadenaAutorizacion, estrategiaCierreSistema),
                bitacoraRepositorio
        );

        this.registrarNoAnunciadoCasoUso = new AuditoriaRegistrarNoAnunciadoDecorador(
                new RegistrarNoAnunciadoServicio(visitaRepositorio, personaRepositorio,
                        funcionarioRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.aprobarRechazarCasoUso = new AuditoriaAprobarRechazarDecorador(
                new AprobarRechazarVisitaServicio(visitaRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        this.registrarTrabajadorCasoUso = new AuditoriaRegistrarTrabajadorDecorador(
                new RegistrarTrabajadorServicio(visitaRepositorio, personaRepositorio,
                        funcionarioRepositorio, cadenaAutorizacion),
                bitacoraRepositorio
        );

        Map<TipoRegularizacion, com.acme.sica.aplicacion.visita.EstrategiaSalidaOlvidada> estrategias = new HashMap<>();
        estrategias.put(TipoRegularizacion.CIERRE_SISTEMA, estrategiaCierreSistema);
        estrategias.put(TipoRegularizacion.NUEVO_INGRESO, new EstrategiaNuevoIngreso(visitaRepositorio));

        this.regularizarSalidaCasoUso = new AuditoriaRegularizarDecorador(
                new RegularizarSalidaServicio(visitaRepositorio, personaRepositorio,
                        cadenaAutorizacion, estrategias),
                bitacoraRepositorio
        );

        this.checkOutCasoUso = new AuditoriaCheckOutDecorador(
                new CheckOutServicio(visitaRepositorio, personaRepositorio, cadenaAutorizacion),
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

    public GestionarUsuarioCasoUso getGestionarUsuarioCasoUso() {
        return gestionarUsuarioCasoUso;
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

    public GestionarIncidenteCasoUso getGestionarIncidenteCasoUso() {
        return gestionarIncidenteCasoUso;
    }

    public GestionarBloqueoPersonaCasoUso getGestionarBloqueoPersonaCasoUso() {
        return gestionarBloqueoPersonaCasoUso;
    }

    public ConsultarReporteAccesosCasoUso getConsultarReporteAccesosCasoUso() {
        return consultarReporteAccesosCasoUso;
    }

    public ConsultarReporteIncidentesCasoUso getConsultarReporteIncidentesCasoUso() {
        return consultarReporteIncidentesCasoUso;
    }

    public GestionarPersonaCasoUso getGestionarPersonaCasoUso() {
        return gestionarPersonaCasoUso;
    }

    public PreRegistrarInvitadoCasoUso getPreRegistrarInvitadoCasoUso() {
        return preRegistrarInvitadoCasoUso;
    }

    public CheckInInvitadoCasoUso getCheckInInvitadoCasoUso() {
        return checkInInvitadoCasoUso;
    }

    public RegistrarNoAnunciadoCasoUso getRegistrarNoAnunciadoCasoUso() {
        return registrarNoAnunciadoCasoUso;
    }

    public AprobarRechazarVisitaCasoUso getAprobarRechazarCasoUso() {
        return aprobarRechazarCasoUso;
    }

    public RegistrarTrabajadorCasoUso getRegistrarTrabajadorCasoUso() {
        return registrarTrabajadorCasoUso;
    }

    public RegularizarSalidaCasoUso getRegularizarSalidaCasoUso() {
        return regularizarSalidaCasoUso;
    }

    public CheckOutCasoUso getCheckOutCasoUso() {
        return checkOutCasoUso;
    }

    public HasheadorContrasenas getHasheadorContrasenas() {
        return hasheadorContrasenas;
    }

    public ManejadorAutorizacion getCadenaAutorizacion() {
        return cadenaAutorizacion;
    }

}
