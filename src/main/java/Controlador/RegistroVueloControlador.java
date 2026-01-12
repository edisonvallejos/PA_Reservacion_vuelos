
package Controlador;

import Modelo.Vuelo;
import Vista.VRegistroVuelo;
import javax.swing.JOptionPane;

public class RegistroVueloControlador {
    private VRegistroVuelo vista;

    public RegistroVueloControlador(VRegistroVuelo vista) {
        this.vista = vista;
        this.vista.btnRegistrar.addActionListener(e -> registrar());
        this.vista.btnCancelar.addActionListener(e -> vista.dispose());
    }

    private void registrar() {
        try {
            String origen = vista.cboOrigen.getSelectedItem().toString().trim();
            String destino = vista.cboDestino.getSelectedItem().toString().trim();
            String fecha = vista.txtFecha.getText().trim();
            String hora = vista.txtHora.getText().trim() + ":00";
            double precio = Double.parseDouble(vista.txtPrecio.getText().trim());
            String item = vista.cboAvion.getSelectedItem().toString();
            int idAvion = Integer.parseInt(item.split(" - ")[0]);

            if (origen.isEmpty() || destino.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.");
                return;
            }
            
            if (origen.equals(destino)){
                JOptionPane.showMessageDialog(vista,"Origen y destino deben ser diferentes");
                return;
            }

            if (Vuelo.registrarVuelo(origen, destino, fecha, hora, precio, idAvion)) {
                JOptionPane.showMessageDialog(vista, "Vuelo registrado correctamente.");
                return;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Precio o ID de avión inválido.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}