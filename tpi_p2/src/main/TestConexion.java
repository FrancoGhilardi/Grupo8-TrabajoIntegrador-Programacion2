package main;

import java.sql.Connection;
import java.sql.SQLException;
import config.DatabaseConnection;

/**
 * Clase de utilidad para probar la conexión a la base de datos.
 * 
 * Proporciona métodos estáticos que verifican que:
 * - El driver JDBC esté correctamente configurado
 * - Las credenciales de acceso sean válidas
 * - La base de datos esté accesible en el host y puerto especificados
 * 
 * Esta clase es especialmente útil durante el desarrollo y deployment
 * para validar la configuración del entorno antes de ejecutar operaciones
 * críticas en la base de datos.
 * 
 * @author Grupo 8
 * @version 1.0
 */
public class TestConexion {

    /**
     * Prueba la conexión a la base de datos MySQL.
     * 
     * Este método intenta establecer una conexión usando la clase
     * DatabaseConnection y reporta el resultado en la consola.
     * 
     * Utiliza try-with-resources para asegurar que la conexión se cierre
     * automáticamente, evitando fugas de recursos (resource leaks).
     * 
     * Salida en consola:
     * - ✅ Mensaje de éxito si la conexión se establece correctamente
     * - ❌ Mensaje de error si falla la conexión
     * - ⚠️ Detalles del error y stack trace para facilitar la depuración
     */
    public static void probarConexion() {

        System.out.println("🔧 Iniciando prueba de conexion...");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Conexion establecida con exito.");
          
            } else {
                System.out.println("❌ No se pudo establecer la conexion.");
            }
        } catch (SQLException e) {
            // Manejo de errores en la conexión a la base de datos
            System.err.println("⚠️ Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace completo para depuración
        }
    }   
}

