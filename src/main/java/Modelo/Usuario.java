
package Modelo;

import java.sql.*;
import javax.swing.JOptionPane;

public class Usuario {

    public static String cedulaLogueada = null;
    
    // Método para validar login
    public static String validarLogin(String cedula, String contrasena) {
        String rol = null;
        String sql = "SELECT rol FROM usuario WHERE cedula = ? AND contrasena = ?";
        
        try (Connection conn = ConexionBDD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            stmt.setString(2, contrasena);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                rol = rs.getString("rol");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos.");
            e.printStackTrace();
        }
        return rol;
    }

    // Validación de cédula ecuatoriana
    public static boolean validarCedulaEcuatoriana(String cedula) {
        if (cedula == null || cedula.length() != 10) return false;
        try {
            int provincia = Integer.parseInt(cedula.substring(0, 2));
            if (provincia < 1 || provincia > 24) return false;

            int[] coef = {2, 1, 2, 1, 2, 1, 2, 1, 2};
            int suma = 0;
            for (int i = 0; i < 9; i++) {
                int dig = Character.getNumericValue(cedula.charAt(i));
                int prod = dig * coef[i];
                if (prod >= 10) prod -= 9;
                suma += prod;
            }
            int verificador = (10 - (suma % 10)) % 10;
            return verificador == Character.getNumericValue(cedula.charAt(9));
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    
public static boolean registrar(String cedula, String nombres, String apellidos,
                                String contrasena, String telefono) {
    // Validar que no exista ya un usuario con esa cédula
    if (existeUsuario(cedula)) {
        JOptionPane.showMessageDialog(null, "Ya existe un usuario con esta cédula.");
        return false;
    }

    String sql = "INSERT INTO usuario (cedula, nombres, apellidos, contrasena, telefono, rol) VALUES (?, ?, ?, ?, ?, 'cliente')";
    try (Connection conn = ConexionBDD.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, cedula);
        stmt.setString(2, nombres);
        stmt.setString(3, apellidos);
        stmt.setString(4, contrasena);
        stmt.setString(5, telefono);

        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al registrar usuario.");
        e.printStackTrace();
        return false;
    }
}


private static boolean existeUsuario(String cedula) {
    String sql = "SELECT id_usuario FROM usuario WHERE cedula = ?";
    try (Connection conn = ConexionBDD.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, cedula);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
}