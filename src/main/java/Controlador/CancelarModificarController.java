package Controlador;

import Modelo.VueloModelo;
import Vista.CancelarModificarVuelo;
import Vista.ModificarVuelo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CancelarModificarController {
    private CancelarModificarVuelo vista;
    private VueloModelo modelo;
    
    public CancelarModificarController(CancelarModificarVuelo vista) {
        this.vista = vista;
        this.modelo = new VueloModelo();
        this.inicializarEventos();
        this.cargarVuelos();
    }
    
    private void inicializarEventos() {
        // Botón Cancelar Vuelo
        vista.getBtnCancelarVuelo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarVuelo();
            }
        });
        
        // Botón Modificar Vuelo
        vista.getBtnModificarVuelo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarVuelo();
            }
        });
        
        // Botón Atrás
        vista.getBtnAtras().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vista.dispose();
            }
        });
    }
    
    private void cargarVuelos() {
    // 1. CREAR NUEVO MODELO CON NOMBRES
    DefaultTableModel tablaModelo = new DefaultTableModel();
    tablaModelo.addColumn("ID");
    tablaModelo.addColumn("ORIGEN");
    tablaModelo.addColumn("DESTINO");
    tablaModelo.addColumn("FECHA");
    tablaModelo.addColumn("HORA");
    tablaModelo.addColumn("PRECIO");
    tablaModelo.addColumn("AVIÓN");
    tablaModelo.addColumn("MATRÍCULA");
    tablaModelo.addColumn("ESTADO");
    
    // 2. ASIGNAR MODELO A LA TABLA
    vista.getTblVuelos().setModel(tablaModelo);
    
    // 3. CARGAR DATOS
    ResultSet rs = modelo.obtenerVuelos();
    if (rs != null) {
        try {
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_vuelo"),
                    rs.getString("origen"),
                    rs.getString("destino"),
                    rs.getDate("fecha_salida"),
                    rs.getTime("hora_salida"),
                    rs.getDouble("precio"),
                    rs.getString("modelo"),
                    rs.getString("matricula"),
                    rs.getString("estado")
                };
                tablaModelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar vuelos: " + e.getMessage());
        }
    }
}
    
    private void cancelarVuelo() {
        int idVuelo = vista.getVueloSeleccionado();
        if (idVuelo == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un vuelo de la tabla");
            return;
        }
        
        int confirmar = JOptionPane.showConfirmDialog(vista, 
            "¿Está seguro de cancelar este vuelo?\nLos clientes con reservas serán notificados.",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmar == JOptionPane.YES_OPTION) {
            boolean exito = modelo.cancelarVuelo(idVuelo);
            if (exito) {
                JOptionPane.showMessageDialog(vista, "Vuelo cancelado exitosamente");
                cargarVuelos(); // Refrescar tabla (el vuelo desaparecerá)
            }
        }
    }
    
    private void modificarVuelo() {
    int idVuelo = vista.getVueloSeleccionado();
    if (idVuelo == -1) {
        JOptionPane.showMessageDialog(vista, "Seleccione un vuelo para modificar");
        return;
    }
    
    ModificarVuelo vistaModificar = new ModificarVuelo(idVuelo);
    ModificarVueloController controlador = new ModificarVueloController(vistaModificar);
    vistaModificar.setVisible(true);
}
}

