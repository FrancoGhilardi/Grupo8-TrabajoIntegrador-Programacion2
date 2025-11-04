package tpi_p2;

/**
 * Clase principal del sistema TPI - Trabajo Práctico Integrador.
 * 
 * Esta clase contiene el método main, que actúa como punto de entrada
 * de la aplicación. Actualmente ejecuta pruebas de conexión a la base de datos
 * para validar la correcta configuración del sistema.
 * 
 * Nota: El nombre de la clase está en minúsculas para coincidir con el
 * nombre del archivo main.java (convención de NetBeans).
 * 
 * @author Grupo 8
 * @version 1.0
 */
public class tpi_p2 {

    /**
     * Método principal de ejecución del programa.
     * 
     * Ejecuta la prueba de conexión a la base de datos para verificar
     * que la configuración de DatabaseConnection sea correcta y que
     * el sistema pueda comunicarse exitosamente con MySQL.
     * 
     * @param args argumentos de línea de comandos (no utilizados actualmente)
     */
    public static void main(String[] args) {
                
        TestConexion.probarConexion(); // Ejecuta la prueba de conexión a la base de datos es opcional.

    }
    
}
