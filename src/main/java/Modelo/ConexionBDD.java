// ConexionBDD.java
package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionBDD {
    private static Connection conexion = null;
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_reservacion_vuelos";
    private static final String USER = "root";
    private static final String PASSWORD = "10045300189"; // Cambia si es diferente
    
    // Constructor privado para evitar instancias
    private ConexionBDD() {}
    
    // Método estático para obtener la conexión (Singleton)
    public static Connection getConnection() {
        if (conexion == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Driver actualizado
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión exitosa a la BD");
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Error: Driver no encontrado\n" + e.getMessage());
                System.out.println("Error driver: " + e.getMessage());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al conectar a la BD\n" + e.getMessage());
                System.out.println("Error SQL: " + e.getMessage());
            }
        }
        return conexion;
    }
    
    // Método para cerrar la conexión (opcional)
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion = null;
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}