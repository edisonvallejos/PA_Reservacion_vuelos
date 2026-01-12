
package Controlador;

import Modelo.Reserva;
import Modelo.Usuario;
import Vista.VMisReservas;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class MisReservasControlador {
    private VMisReservas vista;
    private DefaultTableModel modeloTabla;

    public MisReservasControlador(VMisReservas vista) {
        this.vista = vista;
        iniciarTabla();
        cargarReservas();
        this.vista.btnCerrar.addActionListener(e -> vista.dispose());
        
         this.vista.btnCancelar.addActionListener(e -> cancelarReservaCliente());
    }

    private void iniciarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Origen", "Destino", "Fecha", "Hora", "Asiento", "Estado"},
            0
        );
        vista.tablaMisReservas.setModel(modeloTabla);
        vista.tablaMisReservas.getColumnModel().getColumn(6).setPreferredWidth(350);
        vista.tablaMisReservas.getColumnModel().getColumn(0).setWidth(0);
        vista.tablaMisReservas.getColumnModel().getColumn(0).setMinWidth(0);
        vista.tablaMisReservas.getColumnModel().getColumn(0).setMaxWidth(0);
    }

    private void cargarReservas() {
        String cedula = Usuario.cedulaLogueada;
        if (cedula == null) return;

        ResultSet rs = Reserva.listarMisReservas(cedula);
        try {
            if (rs != null) {
                while (rs.next()) {
                    String estadoVuelo = rs.getString("estado_vuelo");
                    String estadoReserva = rs.getString("estado_reserva");
                    String motivo = rs.getString("motivo_cancelacion");

                    String estadoFinal = estadoReserva;
                    if ("Cancelado".equals(estadoVuelo)) {
                        estadoFinal = "Vuelo cancelado";
                        if (motivo != null && !motivo.trim().isEmpty()) {
                            estadoFinal += ": " + motivo;
                        }
                    }

                    modeloTabla.addRow(new Object[]{
                        rs.getInt("id_reserva"),
                        rs.getString("origen"),
                        rs.getString("destino"),
                        rs.getDate("fecha_salida"),
                        rs.getTime("hora_salida"),
                        rs.getString("numero_asiento"),
                        estadoFinal
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    private void cancelarReservaCliente() {
    int fila = vista.tablaMisReservas.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(vista, "Seleccione una reserva para cancelar.");
        return;
    }

    // Obtiene el ID de la reserva (columna 0, aunque esté oculta)
    int idReserva = (int) modeloTabla.getValueAt(fila, 0);

    int confirm = JOptionPane.showConfirmDialog(
        vista,
        "¿Está seguro de cancelar esta reserva?",
        "Confirmar cancelación",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        if (Reserva.cancelarReserva(idReserva, "cliente")) {
            // Actualiza solo la celda de estado
            modeloTabla.setValueAt("Cancelada", fila, 6); // columna "Estado"
            JOptionPane.showMessageDialog(vista, "Reserva cancelada correctamente.");
        } else {
            JOptionPane.showMessageDialog(vista, "Error al cancelar la reserva.");
        }
    }
}
    
    
}