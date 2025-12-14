package Controlador;

import Modelo.usuarioModelo;
import Vista.Registrar_U;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class usuarioControlador {
    private Registrar_U vista;
    private usuarioModelo modelo;
    
    public usuarioControlador(Registrar_U vista) {
        this.vista = vista;
        this.modelo = new usuarioModelo();
        this.inicializarEventos();
    }
    
    private void inicializarEventos() {
        // Botón Registrar
        vista.getBtnRegistrar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
            }
        });
    }
    
    private void registrarUsuario() {
        // Obtener datos de la vista usando los getters
        String cedula = vista.getCedula();
        String nombres = vista.getNombres();
        String apellidos = vista.getApellidos();
        String email = vista.getEmail();
        String telefono = vista.getTelefono();
        String contrasena = vista.getContrasena();
        
        // Validar
        if (cedula.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || 
            email.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Complete todos los campos obligatorios");
            return;
        }
        
        // Llamar al modelo
        boolean exito = modelo.registrarCliente(cedula, nombres, apellidos, 
                                                email, contrasena, telefono);
        
        if (exito) {
            JOptionPane.showMessageDialog(vista, "Registro exitoso");
            vista.dispose(); // Cierra ventana
        }
    }
}