package com.acme.sica.infraestructura.seguridad;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HasheadorContrasenasTest {

    private final HasheadorContrasenas hasheador = new HasheadorContrasenas();

    @Test
    void verificarContrasenaValida() {
        // Given
        String contrasena = "admin123";
        String hashAlmacenado = hasheador.hashear(contrasena);

        // When
        boolean resultado = hasheador.verificar(contrasena, hashAlmacenado);

        // Then
        assertTrue(resultado);
    }

    @Test
    void verificarContrasenaInvalida() {
        // Given
        String contrasenaCorrecta = "admin123";
        String contrasenaIncorrecta = "wrongpassword";
        String hashAlmacenado = hasheador.hashear(contrasenaCorrecta);

        // When
        boolean resultado = hasheador.verificar(contrasenaIncorrecta, hashAlmacenado);

        // Then
        assertFalse(resultado);
    }

    @Test
    void verificarHashInvalido() {
        // Given
        String contrasena = "admin123";
        String hashInvalido = "invalid$hash";

        // When
        boolean resultado = hasheador.verificar(contrasena, hashInvalido);

        // Then
        assertFalse(resultado);
    }

    @Test
    void generarHashDiferenteCadaVez() {
        // Given
        String contrasena = "admin123";

        // When
        String hash1 = hasheador.hashear(contrasena);
        String hash2 = hasheador.hashear(contrasena);

        // Then
        assertNotEquals(hash1, hash2);
        assertTrue(hasheador.verificar(contrasena, hash1));
        assertTrue(hasheador.verificar(contrasena, hash2));
    }

}
