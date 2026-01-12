
package Controlador;

import Modelo.Avion;
import Vista.VCatalogoAviones;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CatalogoAvionesControlador {
    private VCatalogoAviones vista;
    private DefaultTableModel modeloTabla;

    public CatalogoAvionesControlador(VCatalogoAviones vista) {
        this.vista = vista;
        iniciarTabla();
        cargarAviones();
        this.vista.btnCerrar.addActionListener(e -> vista.dispose());
    }

    private void iniciarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Modelo", "Matrícula", "Capacidad"},
            0
        );
        vista.tablaAviones.setModel(modeloTabla);
    }

    private void cargarAviones() {
        ResultSet rs = Avion.listarAviones();
        try {
            if (rs != null) {
                while (rs.next()) {
                    modeloTabla.addRow(new Object[]{
                        rs.getInt("id_avion"),
                        rs.getString("modelo"),
                        rs.getString("matricula"),
                        rs.getInt("capacidad")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}