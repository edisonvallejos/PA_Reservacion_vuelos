// Controlador/MisReservasControlador.java
package Controlador;

import Modelo.Reserva;
import Modelo.Usuario;
import Vista.VMisReservas;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MisReservasControlador {
    private VMisReservas vista;
    private DefaultTableModel modeloTabla;

    public MisReservasControlador(VMisReservas vista) {
        this.vista = vista;
        iniciarTabla();
        cargarReservas();
        this.vista.btnCerrar.addActionListener(e -> vista.dispose());
    }

    private void iniciarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{ "Origen", "Destino", "Fecha", "Hora", "Asiento", "Estado"},
            0
        );
        vista.tablaMisReservas.setModel(modeloTabla);
    }

    private void cargarReservas() {
        String cedula = Usuario.cedulaLogueada;
        if (cedula == null) return;

        ResultSet rs = Reserva.listarMisReservas(cedula);
        try {
            if (rs != null) {
                while (rs.next()) {
                    modeloTabla.addRow(new Object[]{
                        
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
        }
    }
}