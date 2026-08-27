package com.acme.sica.aplicacion.funcionario;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.entrada.GestionarFuncionarioCasoUso;
import com.acme.sica.dominio.puerto.salida.EmpresaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.List;

/**
 * Servicio de aplicación para la gestión de funcionarios (HU-09).
 *
 * Se encarga de la lógica de negocio y de la autorización (Chain of
 * Responsibility). La auditoría la realiza un decorador (HU-06).
 */
public class GestionarFuncionarioServicio implements GestionarFuncionarioCasoUso {

    private static final String PERMISO_REQUERIDO = "gestionar_funcionarios";

    private final FuncionarioRepositorioPuerto funcionarioRepositorio;
    private final EmpresaRepositorioPuerto empresaRepositorio;
    private final UsuarioRepositorioPuerto usuarioRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public GestionarFuncionarioServicio(FuncionarioRepositorioPuerto funcionarioRepositorio,
                                        EmpresaRepositorioPuerto empresaRepositorio,
                                        UsuarioRepositorioPuerto usuarioRepositorio,
                                        ManejadorAutorizacion cadenaAutorizacion) {
        this.funcionarioRepositorio = funcionarioRepositorio;
        this.empresaRepositorio = empresaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    @Override
    public Funcionario crear(CrearFuncionarioComando comando) {
        autorizar("crear funcionarios");

        if (comando.getNombreCompleto() == null || comando.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException("El nombre del funcionario es obligatorio");
        }

        Empresa empresa = empresaRepositorio.buscarPorId(comando.getEmpresaId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Empresa no encontrada con id " + comando.getEmpresaId()));
        if (!empresa.isActiva()) {
            throw new IllegalArgumentException("La empresa '" + empresa.getNombre() + "' no está activa");
        }

        Usuario usuario = null;
        if (comando.getUsuarioId() != null) {
            usuario = usuarioRepositorio.buscarPorId(comando.getUsuarioId())
                    .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                            "Usuario no encontrado con id " + comando.getUsuarioId()));
            if (funcionarioRepositorio.buscarPorUsuario(comando.getUsuarioId()).isPresent()) {
                throw new IllegalArgumentException(
                        "El usuario '" + usuario.getUsername() + "' ya es un funcionario registrado");
            }
        }

        Funcionario funcionario = new Funcionario(usuario, empresa,
                comando.getNombreCompleto(), comando.getCargo());
        return funcionarioRepositorio.guardar(funcionario);
    }

    @Override
    public Funcionario editar(EditarFuncionarioComando comando) {
        autorizar("editar funcionarios");

        Funcionario funcionario = funcionarioRepositorio.buscarPorId(comando.getId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Funcionario no encontrado con id " + comando.getId()));

        Empresa empresa = empresaRepositorio.buscarPorId(comando.getEmpresaId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Empresa no encontrada con id " + comando.getEmpresaId()));
        if (!empresa.isActiva()) {
            throw new IllegalArgumentException("La empresa '" + empresa.getNombre() + "' no está activa");
        }

        Usuario usuario = null;
        if (comando.getUsuarioId() != null) {
            usuario = usuarioRepositorio.buscarPorId(comando.getUsuarioId())
                    .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                            "Usuario no encontrado con id " + comando.getUsuarioId()));
            final String usernameDuplicado = usuario.getUsername();
            funcionarioRepositorio.buscarPorUsuario(comando.getUsuarioId()).ifPresent(existing -> {
                if (!existing.getId().equals(comando.getId())) {
                    throw new IllegalArgumentException(
                            "El usuario '" + usernameDuplicado + "' ya es otro funcionario");
                }
            });
        }

        funcionario.setUsuario(usuario);
        funcionario.setEmpresa(empresa);
        funcionario.setNombreCompleto(comando.getNombreCompleto());
        funcionario.setCargo(comando.getCargo());
        funcionario.setActivo(comando.isActivo());
        return funcionarioRepositorio.guardar(funcionario);
    }

    @Override
    public void eliminar(Long funcionarioId) {
        autorizar("eliminar funcionarios");

        Funcionario funcionario = funcionarioRepositorio.buscarPorId(funcionarioId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Funcionario no encontrado con id " + funcionarioId));

        funcionarioRepositorio.eliminarPorId(funcionarioId);
    }

    @Override
    public List<Funcionario> listarTodos() {
        autorizar("listar funcionarios");
        return funcionarioRepositorio.listarTodos();
    }

    @Override
    public Funcionario obtenerPorId(Long funcionarioId) {
        autorizar("obtener funcionarios");
        return funcionarioRepositorio.buscarPorId(funcionarioId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Funcionario no encontrado con id " + funcionarioId));
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
