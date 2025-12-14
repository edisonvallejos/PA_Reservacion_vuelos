package Controlador;

import Vista.VistaAdmin;
import Vista.GestionVuelos;
import javax.swing.*;
import java.awt.event.*;

public class AdminController {
    private VistaAdmin vista;
    
    public AdminController(VistaAdmin vista) {
        this.vista = vista;
        this.inicializarEventos();
    }
    
    private void inicializarEventos() {
        // Solo el botón de Gestionar Vuelos por ahora
        if (vista.getBtnGestionVuelos() != null) {
            vista.getBtnGestionVuelos().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GestionVuelos vistaGestion = new GestionVuelos();
                    vistaGestion.setVisible(true);
                }
            });
        }
        
        // Agregarás los otros botones después
    }
}