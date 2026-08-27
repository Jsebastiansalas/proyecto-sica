package com.acme.sica.aplicacion.funcionario;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarFuncionarioCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría para la gestión de funcionarios (HU-06).
 * Registra automáticamente CREAR/EDITAR/ELIMINAR funcionario en la bitácora.
 */
public class AuditoriaFuncionarioDecorador implements GestionarFuncionarioCasoUso {

    private static final String ENTIDAD = "FUNCIONARIO";

    private final GestionarFuncionarioCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaFuncionarioDecorador(GestionarFuncionarioCasoUso decorado,
                                         BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Funcionario crear(CrearFuncionarioComando comando) {
        Funcionario funcionario = decorado.crear(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_FUNCIONARIO,
                ENTIDAD, funcionario.getId(), "Funcionario creado: " + funcionario.getNombreCompleto());
        return funcionario;
    }

    @Override
    public Funcionario editar(EditarFuncionarioComando comando) {
        Funcionario funcionario = decorado.editar(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.EDITAR_FUNCIONARIO,
                ENTIDAD, funcionario.getId(), "Funcionario editado: " + funcionario.getNombreCompleto());
        return funcionario;
    }

    @Override
    public void eliminar(Long funcionarioId) {
        decorado.eliminar(funcionarioId);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.ELIMINAR_FUNCIONARIO,
                ENTIDAD, funcionarioId, "Funcionario eliminado: id=" + funcionarioId);
    }

    @Override
    public List<Funcionario> listarTodos() {
        return decorado.listarTodos();
    }

    @Override
    public Funcionario obtenerPorId(Long funcionarioId) {
        return decorado.obtenerPorId(funcionarioId);
    }
}
