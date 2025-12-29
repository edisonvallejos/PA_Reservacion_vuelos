// Modelo/Reserva.java
package Modelo;

import java.sql.*;
import javax.swing.JOptionPane;

public class Reserva {

    // Método para listar todas las reservas (usado en JTable)
    public static ResultSet listarReservas() {
        try {
            Connection conn = ConexionBDD.getConnection();
            CallableStatement stmt = conn.prepareCall("{CALL sp_ListarReservas()}");
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para cancelar una reserva por ID
    public static boolean cancelarReserva(int idReserva) {
        String sql = "UPDATE reserva SET estado = 'Cancelada' WHERE id_reserva = ?";
        try (Connection conn = ConexionBDD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean reservarVuelo(String cedulaCliente, int idVuelo) {
    String sql = "{CALL sp_ReservarVuelos(?, ?)}";
    try (Connection conn = ConexionBDD.getConnection();
         CallableStatement stmt = conn.prepareCall(sql)) {
        stmt.setString(1, cedulaCliente);
        stmt.setInt(2, idVuelo);
        stmt.execute();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "No se pudo reservar: " + 
            (e.getMessage().contains("No hay asientos") ? "no hay asientos disponibles." : "error desconocido."));
        return false;
    }
}
    
    public static ResultSet listarMisReservas(String cedula) {
    try {
        Connection conn = ConexionBDD.getConnection();
        CallableStatement stmt = conn.prepareCall("{CALL sp_ListarMisReservas(?)}");
        stmt.setString(1, cedula);
        return stmt.executeQuery();
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
    
    
    
    
    
    // Método para liberar el asiento (opcional, si quieres marcarlo como disponible)
    // En tu caso, como no usas estado en asiento, no es necesario.
    // Solo cancelas la reserva y ya.
}
