package Controlador;


import Modelo.AsientoInfo;
import Modelo.Reserva;
import Modelo.Vuelo;
import Modelo.Usuario;
import Vista.VBuscarVuelos;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;

public class BuscarVuelosControlador {
    private VBuscarVuelos vista;
    private DefaultTableModel modeloTabla;
    private Integer vueloIdaSeleccionado = null;

    public BuscarVuelosControlador(VBuscarVuelos vista) {
        this.vista = vista;
        iniciarTabla();
        cargarRutas();
        
        // Botones
        this.vista.btnBuscar.addActionListener(e -> buscarVuelos());
        this.vista.btnReservar.addActionListener(e -> reservarVuelo());
        this.vista.btnCancelar.addActionListener(e -> vista.dispose());
        
        // Selección de vuelo y muestra asientos
        vista.tablaVuelos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = vista.tablaVuelos.getSelectedRow();
                if (fila >= 0) {
                    int idVuelo = (int) modeloTabla.getValueAt(fila, 0);
                    mostrarAsientosDelVuelo(idVuelo);
                }
            }
        });
    }

    private void iniciarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Origen", "Destino", "Fecha", "Hora", "Precio", "Avión"},
            0
        );
        vista.tablaVuelos.setModel(modeloTabla);
        vista.tablaVuelos.getColumnModel().getColumn(0).setMinWidth(0);
        vista.tablaVuelos.getColumnModel().getColumn(0).setMaxWidth(0);
        vista.tablaVuelos.getColumnModel().getColumn(0).setWidth(0);
    }

    private void cargarRutas() {
        String[] ciudades = {"Quito", "Guayaquil", "Cuenca", "Manta", "Esmeraldas", "Salinas", "Nueva Loja"};
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
        JOptionPane.showMessageDialog(vista, "Seleccione un vuelo.");
        return;
    }

    String cedula = Usuario.cedulaLogueada;
    if (cedula == null || cedula.isEmpty()) {
        JOptionPane.showMessageDialog(vista, "Usuario no identificado.");
        return;
    }

    int idVuelo = (int) modeloTabla.getValueAt(fila, 0);

    // Si ya hay un vuelo de ida guardado es el regreso
    if (vueloIdaSeleccionado != null) {
        reservarIdaYVuelta(cedula, vueloIdaSeleccionado, idVuelo);
        return;
    }

    
    Object[] opciones = {"Solo ida", "Ida y vuelta"};
    Object seleccion = JOptionPane.showInputDialog(
        vista,
        "Tipo de viaje:",
        "Reserva",
        JOptionPane.QUESTION_MESSAGE,
        null,
        opciones,
        opciones[0]
    );

    if (seleccion == null) return;

    if ("Solo ida".equals(seleccion)) {
        reservarUnVuelo(cedula, idVuelo);
    } else {
        // Guardar el vuelo de ida y buscar el de vuelta
        vueloIdaSeleccionado = idVuelo;
        String origen = vista.cboOrigen.getSelectedItem().toString();
        String destino = vista.cboDestino.getSelectedItem().toString();

        // Cambiar combos a vuelta
        vista.cboOrigen.setSelectedItem(destino);
        vista.cboDestino.setSelectedItem(origen);

        // Buscar vuelos de vuelta automáticamente
        buscarVuelos();
        JOptionPane.showMessageDialog(vista, "Ahora seleccione el vuelo de regreso.");
    }
}

    private void mostrarAsientosDelVuelo(int idVuelo) {
        vista.panelAsientos.removeAll();
        vista.panelAsientos.setLayout(new GridLayout(0, 6));

        List<AsientoInfo> asientos = AsientoInfo.obtenerAsientosConEstado(idVuelo); // ← CORREGIDO: es Asiento., no AsientoInfo.
        for (AsientoInfo info : asientos) {
            JButton btn = new JButton(info.numero);
            btn.setPreferredSize(new Dimension(45, 30));
            btn.setFocusPainted(false);

            if (info.disponible) {
                btn.setBackground(Color.GREEN);
                btn.setForeground(Color.BLACK);
            } else {
                btn.setBackground(Color.RED);
                btn.setForeground(Color.WHITE);
            }
            vista.panelAsientos.add(btn);
        }

        long disponibles = asientos.stream().filter(a -> a.disponible).count();
        vista.lblDisponibles.setText("Asientos disponibles: " + disponibles);

        vista.panelAsientos.revalidate();
        vista.panelAsientos.repaint();
    }
    
    
    private void reservarUnVuelo(String cedula, int idVuelo) {
    String input = JOptionPane.showInputDialog(vista, "¿Cuántos asientos desea?");
    if (input == null || input.trim().isEmpty()) return;

    try {
        int cantidad = Integer.parseInt(input.trim());
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(vista, "Cantidad inválida.");
            return;
        }

        List<Integer> asientos = Reserva.reservarVueloConAsientos(cedula, idVuelo, cantidad);
        if (!asientos.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "¡Reserva de ida exitosa!");
            mostrarAsientosDelVuelo(idVuelo);
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(vista, "Ingrese un número válido.");
    }
}
    
    
    
    
    private void reservarIdaYVuelta(String cedula, int idVueloIda, int idVueloVuelta) {
    String input = JOptionPane.showInputDialog(vista, "¿Cuántos asientos desea para ida y vuelta?");
    if (input == null || input.trim().isEmpty()) {
        vueloIdaSeleccionado = null;
        return;
    }

    try {
        int cantidad = Integer.parseInt(input.trim());
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(vista, "Cantidad inválida.");
            vueloIdaSeleccionado = null;
            return;
        }

        // Reservar ida
        List<Integer> asientosIda = Reserva.reservarVueloConAsientos(cedula, idVueloIda, cantidad);
        if (asientosIda.isEmpty()) {
            vueloIdaSeleccionado = null;
            return;
        }

        // Reservar vuelta
        List<Integer> asientosVuelta = Reserva.reservarVueloConAsientos(cedula, idVueloVuelta, cantidad);
        if (asientosVuelta.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Error al reservar el vuelo de regreso.");
            vueloIdaSeleccionado = null;
            return;
        }

        JOptionPane.showMessageDialog(vista, "¡Reserva de ida y vuelta exitosa!");
        vueloIdaSeleccionado = null; // reset

        
        mostrarAsientosDelVuelo(idVueloVuelta);

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(vista, "Ingrese un número válido.");
        vueloIdaSeleccionado = null;
    }
}
    
    
    
}