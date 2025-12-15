package Controlador;

import Modelo.VueloModelo;
import Vista.ListaVuelos;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListaVuelosController {
    private ListaVuelos vista;
    private VueloModelo modelo;
    
    public ListaVuelosController(ListaVuelos vista) {
        this.vista = vista;
        this.modelo = new VueloModelo();
        this.inicializarEventos();
    }
    
    private void inicializarEventos() {
        // Botón Actualizar
        vista.getBtnActualizar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarVuelos();
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
        DefaultTableModel tablaModelo = (DefaultTableModel) vista.getTblVuelos().getModel();
        tablaModelo.setRowCount(0); // Limpiar tabla
        
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
                        rs.getString("matricula")
                    };
                    tablaModelo.addRow(fila);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(vista, "Error al cargar vuelos: " + e.getMessage());
            }
        }
    }
}