
package Controlador;

import Modelo.Vuelo;
import Vista.VListaVuelos;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListaVuelosControlador {
    private VListaVuelos vista;
    private DefaultTableModel modeloTabla;

    public ListaVuelosControlador(VListaVuelos vista) {
        this.vista = vista;
        iniciarTabla();
        cargarDatos();
        this.vista.btnCerrar.addActionListener(e -> vista.dispose());
    }

    private void iniciarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Origen", "Destino", "Fecha", "Hora", "Precio", "Avión", "Estado"},
            0
        );
        vista.tablaVuelos.setModel(modeloTabla);
    }

    private void cargarDatos() {
        ResultSet rs = Vuelo.listarVuelos();
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
}