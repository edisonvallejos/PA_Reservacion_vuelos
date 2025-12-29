// Controlador/ListaReservasControlador.java
package Controlador;

import Modelo.Reserva;
import Vista.VListaReservas;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListaReservasControlador {
    private VListaReservas vista;
    private DefaultTableModel modeloTabla;

    public ListaReservasControlador(VListaReservas vista) {
        this.vista = vista;
        iniciarTabla();
        cargarReservas();
        this.vista.btnCancelar.addActionListener(e -> cancelarReserva());
        this.vista.btnCerrar.addActionListener(e -> vista.dispose());
    }

    private void iniciarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Cédula", "Cliente", "Origen", "Destino", "Fecha", "Hora", "Asiento", "Estado"},
            0
        );
        vista.tablaReservas.setModel(modeloTabla);
    }

    private void cargarReservas() {
        ResultSet rs = Reserva.listarReservas();
        try {
            if (rs != null) {
                while (rs.next()) {
                    modeloTabla.addRow(new Object[]{
                        rs.getInt("id_reserva"),
                        rs.getString("cedula_cliente"),
                        rs.getString("cliente"),
                        rs.getString("origen"),
                        rs.getString("destino"),
                        rs.getDate("fecha_salida"),
                        rs.getTime("hora_salida"),
                        rs.getString("numero_asiento"),
                        rs.getString("estado")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al cargar reservas.");
        }
    }

    private void cancelarReserva() {
        int fila = vista.tablaReservas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione una reserva de la lista.");
            return;
        }

        int idReserva = (int) modeloTabla.getValueAt(fila, 0);
        String estado = (String) modeloTabla.getValueAt(fila, 8);

        if ("Cancelada".equals(estado)) {
            JOptionPane.showMessageDialog(vista, "Esta reserva ya está cancelada.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            vista,
            "¿Está seguro de cancelar esta reserva?",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (Reserva.cancelarReserva(idReserva)) {
                modeloTabla.setValueAt("Cancelada", fila, 8);
                JOptionPane.showMessageDialog(vista, "Reserva cancelada correctamente.");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al cancelar la reserva.");
            }
        }
    }
}
