package com.acme.sica.infraestructura.seguridad.autorizacion;

/**
 * Manejador abstracto de la cadena de responsabilidad para autorización.
 * Cada eslabón verifica una condición y, si pasa, delega al siguiente.
 */
public abstract class ManejadorAutorizacion {

    protected ManejadorAutorizacion siguiente;

    public ManejadorAutorizacion setSiguiente(ManejadorAutorizacion siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    /**
     * Verifica la autorización para una operación dada.
     *
     * @param permiso código del permiso requerido
     * @param accion  descripción de la acción para mensajes de auditoría
     */
    public abstract void verificar(String permiso, String accion);

    protected void verificarSiguiente(String permiso, String accion) {
        if (siguiente != null) {
            siguiente.verificar(permiso, accion);
        }
    }

}
