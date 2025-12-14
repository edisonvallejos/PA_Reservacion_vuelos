package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class VueloModelo {
    private Connection conexion;
    
    public VueloModelo() {
        this.conexion = ConexionBDD.getConnection();
    }
    
    // ==================== OBTENER AVIONES PARA COMBOBOX ====================
    public List<Object[]> obtenerAviones() {
        List<Object[]> aviones = new ArrayList<>();
        String sql = "SELECT id_avion, modelo, matricula FROM avion ORDER BY modelo";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] avion = new Object[2];
                avion[0] = rs.getInt("id_avion"); // ID
                avion[1] = rs.getString("modelo") + " (" + rs.getString("matricula") + ")"; // Texto mostrar
                aviones.add(avion);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar aviones: " + e.getMessage());
        }
        return aviones;
    }
    
    // ==================== AGREGAR VUELO CON SP ====================
    public boolean agregarVuelo(String origen, String destino, String fecha, 
                                 String hora, double precio, int idAvion) {
        String sql = "{CALL sp_agregar_vuelo(?, ?, ?, ?, ?, ?, ?)}";
        
        try (CallableStatement cst = conexion.prepareCall(sql)) {
            cst.setString(1, origen);
            cst.setString(2, destino);
            cst.setString(3, fecha); // Formato: YYYY-MM-DD
            cst.setString(4, hora);  // Formato: HH:MM:SS
            cst.setDouble(5, precio);
            cst.setInt(6, idAvion);
            cst.registerOutParameter(7, Types.INTEGER); // p_resultado OUT
            
            cst.execute();
            int resultado = cst.getInt(7);
            
            if (resultado == 1) {
                JOptionPane.showMessageDialog(null, "Vuelo agregado exitosamente");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Error: No se pudo agregar el vuelo");
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en BD al agregar vuelo: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== VERIFICAR FECHA VÁLIDA ====================
    public boolean validarFecha(String fecha) {
        return fecha.matches("\\d{4}-\\d{2}-\\d{2}");
    }
    
    // ==================== VERIFICAR HORA VÁLIDA ====================
    public boolean validarHora(String hora) {
        return hora.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]");
    }
    
    // ==================== OBTENER TODOS LOS VUELOS (para listar) ====================
    public ResultSet obtenerVuelos() {
        String sql = "SELECT v.*, a.modelo, a.matricula FROM vuelo v " +
                     "JOIN avion a ON v.id_avion = a.id_avion " +
                     "ORDER BY v.fecha_salida, v.hora_salida";
        
        try {
            Statement stmt = conexion.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener vuelos: " + e.getMessage());
            return null;
        }
    }
    
    // ==================== CANCELAR VUELO (SP) ====================
    public boolean cancelarVuelo(int idVuelo) {
        String sql = "{CALL sp_cancelar_vuelo(?, ?)}";
        
        try (CallableStatement cst = conexion.prepareCall(sql)) {
            cst.setInt(1, idVuelo);
            cst.registerOutParameter(2, Types.INTEGER);
            
            cst.execute();
            int resultado = cst.getInt(2);
            return resultado == 1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cancelar vuelo: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== MODIFICAR VUELO (SP) ====================
    public boolean modificarVuelo(int idVuelo, String origen, String destino, String fecha, 
                                   String hora, double precio, int idAvion) {
        String sql = "{CALL sp_modificar_vuelo(?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try (CallableStatement cst = conexion.prepareCall(sql)) {
            cst.setInt(1, idVuelo);
            cst.setString(2, origen);
            cst.setString(3, destino);
            cst.setString(4, fecha);
            cst.setString(5, hora);
            cst.setDouble(6, precio);
            cst.setInt(7, idAvion);
            cst.registerOutParameter(8, Types.INTEGER);
            
            cst.execute();
            int resultado = cst.getInt(8);
            return resultado == 1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar vuelo: " + e.getMessage());
            return false;
        }
    }
}
