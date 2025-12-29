// Controlador/BuscarVuelosControlador.java
package Controlador;

import Modelo.Reserva;
import Modelo.Vuelo;
import Modelo.Usuario; // ← necesario para cedulaLogueada
import Vista.VBuscarVuelos;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BuscarVuelosControlador {
    private VBuscarVuelos vista;
    private DefaultTableModel modeloTabla;

    public BuscarVuelosControlador(VBuscarVuelos vista) {
        this.vista = vista;
        iniciarTabla();
        cargarRutas();
        this.vista.btnBuscar.addActionListener(e -> buscarVuelos());
        this.vista.btnReservar.addActionListener(e -> reservarVuelo());
        this.vista.btnCancelar.addActionListener(e -> vista.dispose());
    }

    private void iniciarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Origen", "Destino", "Fecha", "Hora", "Precio", "Avión"},
            0
        );
        vista.tablaVuelos.setModel(modeloTabla);
    }

    private void cargarRutas() {
        String[] ciudades = {"Quito", "Guayaquil", "Cuenca"};
        for (String ciudad : ciudades) {
            vista.cboOrigen.addItem(ciudad);
            vista.cboDestino.addItem(ciudad);
        }
    }

    private void buscarVuelos() {
        String origen = vista.cboOrigen.getSelectedItem().toString();
        String destino = vista.cboDestino.getSelectedItem().toString();
       

        if (origen.equals(destino)) {
            JOptionPane.showMessageDialog(vista, "Origen y destino deben ser diferentes.");
            return;
        }
        

        modeloTabla.setRowCount(0);
        ResultSet rs = Vuelo.buscarVuelosDisponibles(origen, destino);
        try {
            if (rs != null) {
                boolean encontrado = false;
                while (rs.next()) {
                    encontrado = true;
                    modeloTabla.addRow(new Object[]{
                        rs.getInt("id_vuelo"),
                        rs.getString("origen"),
                        rs.getString("destino"),
                        rs.getDate("fecha_salida"),
                        rs.getTime("hora_salida"),
                        rs.getBigDecimal("precio"),
                        rs.getString("avion")
                    });
                }
                if (!encontrado) {
                    JOptionPane.showMessageDialog(vista, "No hay vuelos disponibles.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al buscar vuelos.");
        }
    }

    private void reservarVuelo() {
    int fila = vista.tablaVuelos.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(vista, "Seleccione un vuelo de la lista.");
        return;
    }

    // Verifica que el usuario esté logueado
    String cedula = Usuario.cedulaLogueada;
    if (cedula == null || cedula.isEmpty()) {
        JOptionPane.showMessageDialog(vista, "Error: usuario no identificado.");
        return;
    }

    int idVuelo = (int) modeloTabla.getValueAt(fila, 0);

    // Confirmar reserva
    int confirm = JOptionPane.showConfirmDialog(
        vista,
        "¿Desea reservar este vuelo?",
        "Confirmar reserva",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        if (Reserva.reservarVuelo(cedula, idVuelo)) {
            JOptionPane.showMessageDialog(vista, "¡Reserva realizada con éxito!");
            vista.dispose(); // cierra la ventana de búsqueda
        }
    }
}
}
