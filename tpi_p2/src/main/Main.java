package main;

/**
 * Clase principal de arranque de la aplicación.
 * 
 * Su única responsabilidad es invocar al menú principal de la aplicación.
 * Cumple con el requisito del TP de iniciar la ejecución desde Main.
 */
public class Main {
    public static void main(String[] args) {
        AppMenu menu = new AppMenu();
        menu.mostrar();
    }
}