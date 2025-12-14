// usuarioModelo.java
package Modelo;
import Modelo.ConexionBDD;
import java.sql.*;
import javax.swing.JOptionPane;

public class usuarioModelo {
    
    private Connection conexion;
    
    public usuarioModelo() {
        this.conexion = ConexionBDD.getConnection();
    }
    
    // ==================== VALIDACIÓN CÉDULA ECUATORIANA ====================
    public static boolean validarCedulaEcuatoriana(String cedula) {
        // Verificar longitud
        if (cedula == null || cedula.length() != 10) {
            return false;
        }
        
        // Verificar que sean solo dígitos
        if (!cedula.matches("\\d{10}")) {
            return false;
        }
        
        // Los primeros dos dígitos deben ser válidos para provincia (01-24)
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }
        
        // Algoritmo de validación (módulo 10)
        int total = 0;
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int verificador = Integer.parseInt(cedula.substring(9, 10));
        
        for (int i = 0; i < 9; i++) {
            int valor = Integer.parseInt(cedula.substring(i, i + 1)) * coeficientes[i];
            if (valor >= 10) {
                valor -= 9;
            }
            total += valor;
        }
        
        int resultado = 10 - (total % 10);
        if (resultado == 10) {
            resultado = 0;
        }
        
        return resultado == verificador;
    }
    
    // ==================== LOGIN CON STORE PROCEDURE ====================
    public String login(String cedulaEmail, String contrasena) {
        String rol = null;
        String sql = "{CALL sp_login(?, ?, ?)}"; // Llamada al SP
        
        try (CallableStatement cst = conexion.prepareCall(sql)) {
            cst.setString(1, cedulaEmail);
            cst.setString(2, contrasena);
            cst.registerOutParameter(3, Types.VARCHAR); // p_rol es OUT
            
            cst.execute();
            rol = cst.getString(3); // Obtener valor de OUT parameter
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en login: " + e.getMessage());
        }
        return rol; // "admin", "cliente" o null
    }
    
    // ==================== REGISTRO CON STORE PROCEDURE ====================
    public boolean registrarCliente(String cedula, String nombres, String apellidos, 
                                     String email, String contrasena, String telefono) {
        
        // 1. Validar cédula ecuatoriana
        if (!validarCedulaEcuatoriana(cedula)) {
            JOptionPane.showMessageDialog(null, "Cédula ecuatoriana inválida");
            return false;
        }
        
        // 2. Validar que email tenga formato básico
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(null, "Email inválido");
            return false;
        }
        
        // 3. Llamar al SP de registro
        String sql = "{CALL sp_registrar_cliente(?, ?, ?, ?, ?, ?, ?)}";
        
        try (CallableStatement cst = conexion.prepareCall(sql)) {
            cst.setString(1, cedula);
            cst.setString(2, nombres);
            cst.setString(3, apellidos);
            cst.setString(4, email);
            cst.setString(5, contrasena);
            cst.setString(6, telefono);
            cst.registerOutParameter(7, Types.INTEGER); // p_resultado OUT
            
            cst.execute();
            int resultado = cst.getInt(7); // 1 = éxito, 0 = error
            
            if (resultado == 1) {
                JOptionPane.showMessageDialog(null, "Registro exitoso. Ahora puede iniciar sesión.");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Error: Cédula o Email ya registrado.");
                return false;
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en registro: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== VERIFICAR SI USUARIO EXISTE ====================
    public boolean existeUsuario(String cedula, String email) {
        String sql = "{CALL sp_existe_usuario(?, ?, ?)}"; // Asume que creas este SP
        
        try (CallableStatement cst = conexion.prepareCall(sql)) {
            cst.setString(1, cedula);
            cst.setString(2, email);
            cst.registerOutParameter(3, Types.INTEGER);
            
            cst.execute();
            int existe = cst.getInt(3);
            return existe == 1;
            
        } catch (SQLException e) {
            // Si no hay SP, hacer consulta normal
            String sqlBackup = "SELECT id_usuario FROM usuario WHERE cedula = ? OR email = ?";
            try (PreparedStatement pst = conexion.prepareStatement(sqlBackup)) {
                pst.setString(1, cedula);
                pst.setString(2, email);
                ResultSet rs = pst.executeQuery();
                return rs.next();
            } catch (SQLException ex) {
                return false;
            }
        }
    }
    
    // ==================== OBTENER DATOS DE USUARIO ====================
    public ResultadoUsuario obtenerDatosUsuario(String cedulaEmail) {
        ResultadoUsuario usuario = null;
        String sql = "SELECT id_usuario, cedula, nombres, apellidos, email, telefono, rol " +
                     "FROM usuario WHERE cedula = ? OR email = ?";
        
        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, cedulaEmail);
            pst.setString(2, cedulaEmail);
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                usuario = new ResultadoUsuario(
                    rs.getInt("id_usuario"),
                    rs.getString("cedula"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos: " + e.getMessage());
        }
        return usuario;
    }
    
    // ==================== CLASE INTERNA PARA DATOS DE USUARIO ====================
    public static class ResultadoUsuario {
        private int id;
        private String cedula;
        private String nombres;
        private String apellidos;
        private String email;
        private String telefono;
        private String rol;
        
        public ResultadoUsuario(int id, String cedula, String nombres, String apellidos, 
                                String email, String telefono, String rol) {
            this.id = id;
            this.cedula = cedula;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.email = email;
            this.telefono = telefono;
            this.rol = rol;
        }
        
        // Getters
        public int getId() { return id; }
        public String getCedula() { return cedula; }
        public String getNombres() { return nombres; }
        public String getApellidos() { return apellidos; }
        public String getEmail() { return email; }
        public String getTelefono() { return telefono; }
        public String getRol() { return rol; }
    }
}
 
