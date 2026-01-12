
package Controlador;

import Modelo.Usuario;
import Vista.VRegistro;
import javax.swing.JOptionPane;

public class RegistroControlador {

    private VRegistro vista;

    public RegistroControlador(VRegistro vista) {
        this.vista = vista;
        this.vista.btnRegistrar.addActionListener(e -> registrarUsuario());
        this.vista.btnCancelar.addActionListener(e -> cancelar());
    }

    private void registrarUsuario() {
        String cedula = vista.txtCedula.getText().trim();
        String nombres = vista.txtNombres.getText().trim();
        String apellidos = vista.txtApellidos.getText().trim();
        String contrasena = new String(vista.txtContrasena.getPassword()).trim();
        String telefono = vista.txtTelefono.getText().trim();

        // Validar campos vacíos
        if (cedula.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() ||
            contrasena.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar cédula ecuatoriana
        if (!Usuario.validarCedulaEcuatoriana(cedula)) {
            JOptionPane.showMessageDialog(vista, "La cédula no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Registrar en la base de datos
        if (Usuario.registrar(cedula, nombres, apellidos, contrasena, telefono)) {
            JOptionPane.showMessageDialog(vista, "Usuario registrado correctamente.");
            //vista.dispose(); // Cierra la ventana de registro
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo registrar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        vista.dispose();
    }
}