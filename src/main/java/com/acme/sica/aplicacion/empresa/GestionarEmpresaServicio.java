package com.acme.sica.aplicacion.empresa;

import com.acme.sica.dominio.excepciones.EmpresaEnUsoExcepcion;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.puerto.entrada.GestionarEmpresaCasoUso;
import com.acme.sica.dominio.puerto.salida.EmpresaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.List;

/**
 * Servicio de aplicación para la gestión de empresas (HU-08).
 *
 * Se encarga de la lógica de negocio y de la autorización (Chain of
 * Responsibility). La auditoría la realiza un decorador (HU-06).
 */
public class GestionarEmpresaServicio implements GestionarEmpresaCasoUso {

    private static final String PERMISO_REQUERIDO = "gestionar_empresas";

    private final EmpresaRepositorioPuerto empresaRepositorio;
    private final FuncionarioRepositorioPuerto funcionarioRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public GestionarEmpresaServicio(EmpresaRepositorioPuerto empresaRepositorio,
                                    FuncionarioRepositorioPuerto funcionarioRepositorio,
                                    ManejadorAutorizacion cadenaAutorizacion) {
        this.empresaRepositorio = empresaRepositorio;
        this.funcionarioRepositorio = funcionarioRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    /**
     * Crea una nueva entidad a partir del comando recibido.
     */
    @Override
    public Empresa crear(CrearEmpresaComando comando) {
        autorizar("crear empresas");
        verificarNombreDuplicado(comando.getNombre());

        Empresa empresa = new Empresa(comando.getNombre(), comando.getUbicacion());
        return empresaRepositorio.guardar(empresa);
    }

    /**
     * Actualiza una entidad existente con los datos del comando.
     */
    @Override
    public Empresa editar(EditarEmpresaComando comando) {
        autorizar("editar empresas");

        Empresa empresa = empresaRepositorio.buscarPorId(comando.getId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Empresa no encontrada con id " + comando.getId()));

        if (!empresa.getNombre().equals(comando.getNombre())) {
            verificarNombreDuplicado(comando.getNombre());
        }

        empresa.setNombre(comando.getNombre());
        empresa.setUbicacion(comando.getUbicacion());
        empresa.setActiva(comando.isActiva());
        return empresaRepositorio.guardar(empresa);
    }

    /**
     * Elimina la entidad identificada por el id proporcionado.
     */
    @Override
    public void eliminar(Long empresaId) {
        autorizar("eliminar empresas");

        Empresa empresa = empresaRepositorio.buscarPorId(empresaId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Empresa no encontrada con id " + empresaId));

        List<Funcionario> funcionarios = funcionarioRepositorio.buscarPorEmpresa(empresaId);
        if (!funcionarios.isEmpty()) {
            throw new EmpresaEnUsoExcepcion("No se puede eliminar la empresa '" + empresa.getNombre()
                    + "' porque tiene " + funcionarios.size() + " funcionario(s) asociado(s)");
        }

        empresaRepositorio.eliminarPorId(empresaId);
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Empresa> listarTodos() {
        autorizar("listar empresas");
        return empresaRepositorio.listarTodos();
    }

    /**
     * Recupera una entidad a partir de su identificador.
     */
    @Override
    public Empresa obtenerPorId(Long empresaId) {
        autorizar("obtener empresas");
        return empresaRepositorio.buscarPorId(empresaId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Empresa no encontrada con id " + empresaId));
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }

    private void verificarNombreDuplicado(String nombre) {
        if (empresaRepositorio.existePorNombre(nombre)) {
            throw new IllegalArgumentException("Ya existe una empresa con el nombre '" + nombre + "'");
        }
    }
}
