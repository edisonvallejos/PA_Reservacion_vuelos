
import Controlador.LoginController;
import Vista.inicio_Sesion;

public class Reservacion_tickets_avion {
    public static void main(String[] args) {
        // Ejecuta el login directamente
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                inicio_Sesion vistaLogin = new inicio_Sesion();
                LoginController controlador = new LoginController(vistaLogin);
                vistaLogin.setVisible(true);
            }
        });
    }
}
