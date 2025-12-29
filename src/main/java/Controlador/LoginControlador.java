// Controlador/LoginControlador.java
package Controlador;

import Modelo.Usuario;
import Vista.VLogin;
import javax.swing.JOptionPane;

public class LoginControlador {

    private VLogin vista;

    public LoginControlador(VLogin vista) {
        this.vista = vista;
        this.vista.btnEntrar.addActionListener(e -> iniciarSesion());
        this.vista.btnRegistrarse.addActionListener(e -> abrirRegistro());
    }

    private void iniciarSesion() {
        String cedula = vista.txtCedula.getText().trim();
        String contrasena = new String(vista.txtContrasena.getPassword());

        if (cedula.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese cédula y contraseña.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!Usuario.validarCedulaEcuatoriana(cedula)) {
            JOptionPane.showMessageDialog(vista, "La cédula ingresada no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String rol = Usuario.validarLogin(cedula, contrasena);
        if (rol != null) {
            Usuario.cedulaLogueada = cedula;
            if ("admin".equals(rol)) {
                new Vista.VMenuAdmin().setVisible(true); // ← abre menú admin
          } else {
                new Vista.VMenuCliente().setVisible(true); // ← abre menú cliente
          }
          vista.dispose(); // cierra login
       } else {
            JOptionPane.showMessageDialog(vista, "Cédula o contraseña incorrectos.");
}
    }

    private void abrirRegistro() {
        vista.dispose();
        new Vista.VRegistro().setVisible(true);
    }
}
