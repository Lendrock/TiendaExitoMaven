package org.tiendaexito.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 *
 * @author Lendrock
 */
public class Conexion {
    private static Conexion instancia;
    private Connection conexion;
    private static final String URL = "jdbc:mysql://localhost:3306/tiendaexito?useSSL=false";
    private static final String USER = "quintom";
    private static final String PASSWORD = "admin";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    
    public Conexion(){
        conectar();
    }
    public void conectar(){
        try {
            Class.forName(DRIVER).newInstance();
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexion realizada con exito");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println("Error al conectar");
            ex.printStackTrace();
        }
    }
    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar();//reconectar
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conexion;
    }
}
