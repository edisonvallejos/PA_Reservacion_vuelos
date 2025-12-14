package Controlador;

import Modelo.usuarioModelo;
import Vista.inicio_Sesion;
import Vista.Registrar_U;
import Vista.VistaAdmin;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private inicio_Sesion vista;
    private usuarioModelo modelo;
    
    public LoginController(inicio_Sesion vista) {
        this.vista = vista;
        this.modelo = new usuarioModelo();
        this.inicializarEventos();
    }
    
    private void inicializarEventos() {
        // Botón ENTRAR
        vista.getBtnEntrar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSesion();
            }
        });
        
        // Botón REGISTRARSE
        vista.getBtnRegistrarse().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirRegistro();
            }
        });
    }
    
    private void iniciarSesion() {
        String cedulaEmail = vista.getCedulaEmail();
        String contrasena = vista.getContrasena();
        
        if (cedulaEmail.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Complete todos los campos");
            return;
        }
        
        String rol = modelo.login(cedulaEmail, contrasena);
        
        if (rol != null) {
            if (rol.equals("admin")) {
                JOptionPane.showMessageDialog(vista, "Bienvenido Administrador");
                // new VistaAdmin().setVisible(true);
                VistaAdmin vistaAdmin = new VistaAdmin();
                AdminController adminController = new AdminController(vistaAdmin);
                vistaAdmin.setVisible(true);
                vista.dispose();
            } else {
                JOptionPane.showMessageDialog(vista, "Bienvenido Cliente");
                // new VistaCliente().setVisible(true);
            }
            vista.dispose(); // Cierra login
        } else {
            JOptionPane.showMessageDialog(vista, "Credenciales incorrectas");
        }
    }
    
    private void abrirRegistro() {
        Registrar_U vistaRegistro = new Registrar_U();
        usuarioControlador controladorRegistro = new usuarioControlador(vistaRegistro);
        vistaRegistro.setVisible(true);
    }
}
