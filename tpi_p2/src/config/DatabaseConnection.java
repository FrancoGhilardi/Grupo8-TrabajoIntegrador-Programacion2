package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para gestionar la conexión a la base de datos MySQL.
 * 
 * Implementa el patrón Singleton implícito mediante métodos estáticos,
 * proporcionando un punto centralizado de acceso a la conexión de base de datos.
 * 
 * Características:
 * - Carga del driver JDBC al inicializar la clase (bloque static)
 * - Validación de credenciales antes de establecer conexión
 * - Manejo de excepciones para errores de driver y conexión
 * 
 * Configuración:
 * - Base de datos: MySQL
 * - Puerto: 3306 (puerto estándar)
 * - Schema: tpi_p2
 * 
 * @author Grupo 8
 * @version 1.0
 */
public class DatabaseConnection {

    /** URL de conexión JDBC a la base de datos MySQL */
    private static final String URL = "jdbc:mysql://localhost:3306/tpi_p2";
    
    /** Usuario de la base de datos */
    private static final String USER = "root";
    
    /** Contraseña del usuario de la base de datos */
    private static final String PASSWORD = "admin123";

    /**
     * Bloque de inicialización estático.
     * Se ejecuta una única vez al cargar la clase, registrando el driver JDBC de MySQL.
     * 
     * @throws RuntimeException si el driver JDBC no está disponible en el classpath
     */
    static {
        try {
            // Carga del driver JDBC de MySQL una sola vez
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Se lanza una excepción en caso de que el driver no esté disponible
            throw new RuntimeException("Error: No se encontró el driver JDBC.", e);
        }
    }

    /**
     * Obtiene una nueva conexión a la base de datos.
     * 
     * Este método realiza las siguientes validaciones:
     * 1. Verifica que las credenciales no estén vacías o nulas
     * 2. Establece la conexión usando DriverManager
     * 
     * Importante: El código que invoque este método es responsable de cerrar
     * la conexión cuando termine de usarla (try-with-resources recomendado).
     * 
     * @return Connection objeto de conexión activa a la base de datos
     * @throws SQLException si hay un problema al conectarse o las credenciales son inválidas
     */
    public static Connection getConnection() throws SQLException {
        // Validación adicional para asegurarse de que las credenciales no estén vacías
        if (URL == null || URL.isEmpty() || USER == null || USER.isEmpty() || PASSWORD == null || PASSWORD.isEmpty()) {
            throw new SQLException("Configuración de la base de datos incompleta o inválida.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}