/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class AsientoInfo {
    public int id;
    public String numero;
    public boolean disponible;

    public AsientoInfo(int id, String numero, boolean disponible) {
        this.id = id;
        this.numero = numero;
        this.disponible = disponible;
    }
    
    
    public static List<AsientoInfo> obtenerAsientosConEstado(int idVuelo) {
    List<AsientoInfo> asientos = new ArrayList<>();
    try (Connection conn = ConexionBDD.getConnection()) {
        CallableStatement stmt = conn.prepareCall("{CALL sp_ObtenerAsientosConEstado(?)}");
        stmt.setInt(1, idVuelo);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            asientos.add(new AsientoInfo(
                rs.getInt("id_asiento"),
                rs.getString("numero_asiento"),
                "Disponible".equals(rs.getString("estado"))
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return asientos;
}
    
    
    
}
