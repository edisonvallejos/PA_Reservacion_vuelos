package Controlador;

import Modelo.VueloModelo;
import Vista.ModificarVuelo;
import javax.swing.*;
import java.awt.event.*;

public class ModificarVueloController {
    private ModificarVuelo vista;
    private VueloModelo modelo;
    
    public ModificarVueloController(ModificarVuelo vista) {
        this.vista = vista;
        this.modelo = new VueloModelo();
        this.inicializarEventos();
    }
    
    private void inicializarEventos() {
        vista.getBtnActualizar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarVuelo();
            }
        });
        
        vista.getBtnCancelar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vista.dispose();
            }
        });
    }
    
    private void actualizarVuelo() {
        // Validaciones
        if (vista.getOrigen().isEmpty() || vista.getDestino().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Origen y Destino son obligatorios");
            return;
        }
        
        if (!vista.getFecha().matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(vista, "Fecha debe ser YYYY-MM-DD");
            return;
        }
        
        if (!vista.getHora().matches("\\d{2}:\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(vista, "Hora debe ser HH:MM:SS");
            return;
        }
        
        double precio = vista.getPrecio();
        if (precio <= 0) {
            JOptionPane.showMessageDialog(vista, "Precio debe ser mayor a 0");
            return;
        }
        
        int idAvion = vista.getAvionSeleccionado();
        if (idAvion <= 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione un avión válido");
            return;
        }
        
        // Llamar al modelo
        boolean exito = modelo.modificarVuelo(
            vista.getIdVuelo(),
            vista.getOrigen(),
            vista.getDestino(),
            vista.getFecha(),
            vista.getHora(),
            precio,
            idAvion
        );
        
        if (exito) {
            JOptionPane.showMessageDialog(vista, "Vuelo actualizado exitosamente");
            vista.dispose();
        }
    }
}