
package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Reserva {

    // Método para listar todas las reservas
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

    // Cancelar reserva
    public static boolean cancelarReserva(int idReserva, String canceladaPor) {
        String sql = "UPDATE reserva SET estado = 'Cancelada', cancelada_por = ? WHERE id_reserva = ?";
        try (Connection conn = ConexionBDD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, canceladaPor);
            stmt.setInt(2, idReserva);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // reservar N asientos
    public static List<Integer> reservarVueloConAsientos(String cedulaCliente, int idVuelo, int cantidad) {
    int idUsuario = obtenerIdUsuarioPorCedula(cedulaCliente);
    if (idUsuario == -1) {
        JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
        return new ArrayList<>(); // ← devuelve lista vacía, no false
    }

    List<Integer> asientos = obtenerAsientosDisponibles(idVuelo, cantidad);
    if (asientos.isEmpty()) {
        JOptionPane.showMessageDialog(null, "No hay asientos disponibles.");
        return new ArrayList<>();
    }
    if (asientos.size() < cantidad) {
        JOptionPane.showMessageDialog(null, 
            "Solo hay " + asientos.size() + " asientos disponibles.");
        return new ArrayList<>();
    }

    for (int idAsiento : asientos) {
        if (!reservarAsiento(idUsuario, idVuelo, idAsiento)) {
            JOptionPane.showMessageDialog(null, "Error al reservar uno de los asientos.");
            return new ArrayList<>(); // ← error → lista vacía
        }
    }
    
    return asientos;
}

    // 🔹 NUEVO: método privado que llama al SP para reservar un asiento específico
    private static boolean reservarAsiento(int idUsuario, int idVuelo, int idAsiento) {
        try (Connection conn = ConexionBDD.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_ReservarAsiento(?, ?, ?)}");
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idVuelo);
            stmt.setInt(3, idAsiento);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // obtener asientos disponibles
    public static List<Integer> obtenerAsientosDisponibles(int idVuelo, int cantidad) {
        List<Integer> asientos = new ArrayList<>();
        try (Connection conn = ConexionBDD.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_ObtenerAsientosDisponibles(?, ?)}");
            stmt.setInt(1, idVuelo);
            stmt.setInt(2, cantidad);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                asientos.add(rs.getInt("id_asiento"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return asientos;
    }

    // MANTENEMOS este método por compatibilidad
    public static boolean reservarVuelo(String cedulaCliente, int idVuelo) {
        // Este método ya no se usa si implementas la nueva lógica
        JOptionPane.showMessageDialog(null, "Use reservarVueloConAsientos en su lugar.");
        return false;
    }

    // Método para listar mis reservas
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
    
    private static int obtenerIdUsuarioPorCedula(String cedula) {
    String sql = "SELECT id_usuario FROM usuario WHERE cedula = ?";
    try (Connection conn = ConexionBDD.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, cedula);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id_usuario");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1;
}
    
    
    
}
