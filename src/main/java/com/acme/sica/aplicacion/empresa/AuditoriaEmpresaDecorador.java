package com.acme.sica.aplicacion.empresa;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarEmpresaCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría para la gestión de empresas (HU-06).
 * Registra automáticamente CREAR/EDITAR/ELIMINAR empresa en la bitácora.
 */
public class AuditoriaEmpresaDecorador implements GestionarEmpresaCasoUso {

    private static final String ENTIDAD = "EMPRESA";

    private final GestionarEmpresaCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaEmpresaDecorador(GestionarEmpresaCasoUso decorado,
                                     BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Empresa crear(CrearEmpresaComando comando) {
        Empresa empresa = decorado.crear(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_EMPRESA,
                ENTIDAD, empresa.getId(), "Empresa creada: " + empresa.getNombre());
        return empresa;
    }

    @Override
    public Empresa editar(EditarEmpresaComando comando) {
        Empresa empresa = decorado.editar(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.EDITAR_EMPRESA,
                ENTIDAD, empresa.getId(), "Empresa editada: " + empresa.getNombre());
        return empresa;
    }

    @Override
    public void eliminar(Long empresaId) {
        decorado.eliminar(empresaId);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.ELIMINAR_EMPRESA,
                ENTIDAD, empresaId, "Empresa eliminada: id=" + empresaId);
    }

    @Override
    public List<Empresa> listarTodos() {
        return decorado.listarTodos();
    }

    @Override
    public Empresa obtenerPorId(Long empresaId) {
        return decorado.obtenerPorId(empresaId);
    }
}
