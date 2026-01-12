package Controlador;
import Modelo.Usuario;
import Vista.VMenuCliente;

// Controlador/MenuClienteControlador.java
public class MenuClienteControlador {
    private VMenuCliente vista;

    public MenuClienteControlador(VMenuCliente vista) {
        this.vista = vista;
        vista.btnBuscarVuelos.addActionListener(e -> {
            new Vista.VBuscarVuelos().setVisible(true);
        });
        vista.btnCerrarSesion.addActionListener(e -> {
            Usuario.cedulaLogueada = null; // limpia al salir
            vista.dispose();
            new Vista.VLogin().setVisible(true);
        });
        
        vista.btnMisReservas.addActionListener(e -> {
            new Vista.VMisReservas().setVisible(true);
        });
    }
}