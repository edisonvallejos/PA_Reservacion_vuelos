// Modelo/Vuelo.java
package Modelo;

import java.sql.*;
import javax.swing.JOptionPane;

public class Vuelo {

    public static boolean registrarVuelo(String origen, String destino, 
            String fechaSalida, String horaSalida, double precio, int idAvion) {
        
        String sql = "{CALL sp_RegistrarVuelo(?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = ConexionBDD.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, origen);
            stmt.setString(2, destino);
            stmt.setString(3, fechaSalida); // formato 'yyyy-MM-dd'
            stmt.setString(4, horaSalida);  // formato 'HH:mm:ss'
            stmt.setDouble(5, precio);
            stmt.setInt(6, idAvion);
            
            stmt.execute();
            return true;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar el vuelo.");
            e.printStackTrace();
            return false;
        }
    }
    
    //metodo para listar los aviones
    public static ResultSet listarVuelos() {
    try {
        Connection conn = ConexionBDD.getConnection();
        CallableStatement stmt = conn.prepareCall("{CALL sp_ListarVuelos()}");
        return stmt.executeQuery();
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
    
}
    
public static boolean cancelarVuelo(int idVuelo) {
    String sql = "{CALL sp_CancelarVuelo(?)}";
    try (Connection conn = ConexionBDD.getConnection();
         CallableStatement stmt = conn.prepareCall(sql)) {
        stmt.setInt(1, idVuelo);
        stmt.execute();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
} 


public static ResultSet buscarVuelosDisponibles(String origen, String destino) {
    try {
        Connection conn = ConexionBDD.getConnection();
        CallableStatement stmt = conn.prepareCall("{CALL sp_BuscarVuelosRuta(?, ?)}");
        stmt.setString(1, origen);
        stmt.setString(2, destino);
        return stmt.executeQuery();
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
    
    
    
    
    
}
