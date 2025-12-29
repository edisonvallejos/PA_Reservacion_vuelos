// Controlador/CancelarVueloControlador.java
package Controlador;

import Modelo.Vuelo;
import Vista.VCancelarVuelo;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CancelarVueloControlador {
    private VCancelarVuelo vista;
    private DefaultTableModel modeloTabla;

    public CancelarVueloControlador(VCancelarVuelo vista) {
        this.vista = vista;
        iniciarTabla();
        cargarVuelos();
        this.vista.btnCancelar.addActionListener(e -> cancelarVueloSeleccionado());
        this.vista.btnCerrar.addActionListener(e -> vista.dispose());
    }

    private void iniciarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Origen", "Destino", "Fecha", "Hora", "Precio", "Avión", "Estado"},
            0
        );
        vista.tablaVuelos.setModel(modeloTabla);
    }

    private void cargarVuelos() {
        ResultSet rs = Vuelo.listarVuelos(); // reutiliza el método del paso anterior
        try {
            while (rs != null && rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_vuelo"),
                    rs.getString("origen"),
                    rs.getString("destino"),
                    rs.getDate("fecha_salida"),
                    rs.getTime("hora_salida"),
                    rs.getBigDecimal("precio"),
                    rs.getString("avion"),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cancelarVueloSeleccionado() {
        int fila = vista.tablaVuelos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un vuelo de la lista.");
            return;
        }

        int idVuelo = (int) modeloTabla.getValueAt(fila, 0);
        String estado = (String) modeloTabla.getValueAt(fila, 7);

        if ("Cancelado".equals(estado)) {
            JOptionPane.showMessageDialog(vista, "Este vuelo ya está cancelado.");
            return;
        }

        if (Vuelo.cancelarVuelo(idVuelo)) {
            modeloTabla.setValueAt("Cancelado", fila, 7);
            JOptionPane.showMessageDialog(vista, "Vuelo cancelado correctamente.");
        } else {
            JOptionPane.showMessageDialog(vista, "Error al cancelar el vuelo.");
        }
    }
}