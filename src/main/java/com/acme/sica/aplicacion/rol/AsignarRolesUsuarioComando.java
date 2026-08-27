package com.acme.sica.aplicacion.rol;

import java.util.Set;

/**
 * Comando para asignar uno o más roles a un usuario.
 */
public class AsignarRolesUsuarioComando {

    private final Long usuarioId;
    private final Set<Long> rolIds;

    public AsignarRolesUsuarioComando(Long usuarioId, Set<Long> rolIds) {
        this.usuarioId = usuarioId;
        this.rolIds = rolIds;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Set<Long> getRolIds() {
        return rolIds;
    }

}
