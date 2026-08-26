package com.acme.sica.infraestructura.seguridad;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Utilidad para hashing seguro de contraseñas usando PBKDF2.
 * No depende de frameworks ni librerías externas.
 */
public class HasheadorContrasenas {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 65536;
    private static final int LONGITUD_CLAVE = 256;
    private static final int LONGITUD_SALT = 16;

    public String hashear(String contrasena) {
        try {
            byte[] salt = generarSalt();
            byte[] hash = derivar(contrasena, salt);
            return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    public boolean verificar(String contrasena, String hashAlmacenado) {
        try {
            String[] partes = hashAlmacenado.split("\\$");
            if (partes.length != 2) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);
            byte[] hashActual = derivar(contrasena, salt);
            return igualdadTiempoConstante(hashEsperado, hashActual);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] generarSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[LONGITUD_SALT];
        random.nextBytes(salt);
        return salt;
    }

    private byte[] derivar(String contrasena, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), salt, ITERACIONES, LONGITUD_CLAVE);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO);
        return factory.generateSecret(spec).getEncoded();
    }

    private boolean igualdadTiempoConstante(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int resultado = 0;
        for (int i = 0; i < a.length; i++) {
            resultado |= a[i] ^ b[i];
        }
        return resultado == 0;
    }

}
