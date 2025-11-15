package main;

/**
 * Clase principal del sistema TPI - Trabajo Práctico Integrador.
 * 
 * Esta clase contiene el método main, que actúa como punto de entrada
 * de la aplicación. Ejecuta pruebas de conexión a la base de datos
 * para validar la correcta configuración del sistema y luego llama al AppMenu
 * 

 * @author Grupo 8
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        TestConexion.probarConexion(); // Ejecuta la prueba de conexión a la base de datos.
        AppMenu menu = new AppMenu();
        menu.mostrar();
    }
}