package Controlador;

import Modelo.VueloModelo;
import Vista.AgregarVuelo;
import javax.swing.*;
import java.awt.event.*;

public class VueloController {
    private AgregarVuelo vista;
    private VueloModelo modelo;
    
    public VueloController(AgregarVuelo vista) {
        this.vista = vista;
        this.modelo = new VueloModelo();
        this.inicializarEventos();
    }
    
    private void inicializarEventos() {
        // Botón Guardar
        vista.getBtnGuardar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarVuelo();
            }
        });
        
        // Botón Cancelar
        vista.getBtnCancelar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vista.dispose(); // Cierra ventana
            }
        });
    }
    
    private void guardarVuelo() {
        // Validar campos
        if (vista.getOrigen().isEmpty() || vista.getDestino().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Origen y Destino son obligatorios");
            return;
        }
        
        double precio = vista.getPrecio();
        if (precio <= 0) {
            JOptionPane.showMessageDialog(vista, "Precio debe ser mayor a 0");
            return;
        }
        
        // Validar fecha (formato simple)
        if (!vista.getFecha().matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(vista, "Fecha debe tener formato YYYY-MM-DD");
            return;
        }
        
        // Validar hora (formato simple)
        if (!vista.getHora().matches("\\d{2}:\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(vista, "Hora debe tener formato HH:MM:SS");
            return;
        }
        
        // Aquí obtendrás el ID del avión seleccionado (por ahora 1)
        int idAvion = vista.getAvionSeleccionado();
        
        // Llamar al modelo
        boolean exito = modelo.agregarVuelo(
            vista.getOrigen(),
            vista.getDestino(),
            vista.getFecha(),
            vista.getHora(),
            precio,
            idAvion
        );
        
        if (exito) {
            JOptionPane.showMessageDialog(vista, "Vuelo agregado exitosamente");
            vista.dispose();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al agregar vuelo");
        }
    }
}

