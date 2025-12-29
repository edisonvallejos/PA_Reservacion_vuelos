/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import java.sql.*;
/**
 *
 * @author DELL
 */
public class Avion {
    
    public static ResultSet listarAviones(){
        try{
            Connection conn = ConexionBDD.getConnection();
            CallableStatement stmt = conn.prepareCall("{CALL sp_ListarAviones()}");
            return stmt.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    
    
}
