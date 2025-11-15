package main;

import entities.*;
import service.impl.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


//menu principal de la aplicacion.

public class AppMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final EmpleadoServiceImpl empleadoService = new EmpleadoServiceImpl();
    private final LegajoServiceImpl legajoService = new LegajoServiceImpl();
    private final EmpleadoLegajoServiceImpl empleadoLegajoService = new EmpleadoLegajoServiceImpl();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1. Gestionar Empleados");
            System.out.println("2. Gestionar Legajos");
            System.out.println("3. Alta conjunta (Empleado + Legajo)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> menuEmpleados();
                case 2 -> menuLegajos();
                case 3 -> altaConjunta();
                case 0 -> System.out.println("Saliendo de la aplicacion...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    
    // submenu empleados
    
    private void menuEmpleados() {
        int opcion;
        do {
            System.out.println("\n--- GESTION DE EMPLEADOS ---");
            System.out.println("1. Crear empleado");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Buscar por DNI");
            System.out.println("4. Listar todos");
            System.out.println("5. Actualizar empleado");
            System.out.println("6. Eliminar empleado");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> crearEmpleado();
                case 2 -> buscarEmpleadoPorId();
                case 3 -> buscarEmpleadoPorDni();
                case 4 -> listarEmpleados();
                case 5 -> actualizarEmpleado();
                case 6 -> eliminarEmpleado();
                case 0 -> System.out.println("Volviendo al menu principal...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void crearEmpleado() {
        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();
            System.out.print("Apellido: ");
            String apellido = scanner.nextLine().trim();
            System.out.print("DNI: ");
            String dni = scanner.nextLine().trim();
            System.out.print("Area: ");
            String area = scanner.nextLine().trim().toUpperCase();
            System.out.print("Email (opcional): ");
            String email = scanner.nextLine().trim();

            Empleado e = new Empleado();
            e.setNombre(nombre);
            e.setApellido(apellido);
            e.setDni(dni);
            e.setArea(area);
            e.setEmail(email);
            e.setFechaIngreso(LocalDate.now());

            empleadoService.insertar(e);
            System.out.println("Empleado creado con éxito.");

        } catch (Exception ex) {
            System.out.println("Error al crear empleado: " + ex.getMessage());
        }
    }

    private void buscarEmpleadoPorId() {
        System.out.print("Ingrese ID de empleado: ");
        long id = leerLong();
        Optional<Empleado> emp = empleadoService.getById(id);
        emp.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No se encontro un empleado con ID " + id)
        );
    }

    private void buscarEmpleadoPorDni() {
        System.out.print("Ingrese DNI: ");
        String dni = scanner.nextLine().trim();
        Optional<Empleado> emp = empleadoService.getByDni(dni);
        emp.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No se encontró un empleado con DNI " + dni)
        );
    }

    private void listarEmpleados() {
        List<Empleado> empleados = empleadoService.getAll();
        if (empleados.isEmpty()) System.out.println("No hay empleados registrados.");
        else empleados.forEach(System.out::println);
    }

    private void actualizarEmpleado() {
        try {
            System.out.print("Ingrese ID del empleado a actualizar: ");
            long id = leerLong();
            Optional<Empleado> empOpt = empleadoService.getById(id);
            if (empOpt.isEmpty()) {
                System.out.println("Empleado no encontrado.");
                return;
            }

            Empleado e = empOpt.get();
            System.out.print("Nuevo nombre (" + e.getNombre() + "): ");
            String nombre = scanner.nextLine().trim();
            if (!nombre.isEmpty()) e.setNombre(nombre);

            System.out.print("Nuevo apellido (" + e.getApellido() + "): ");
            String apellido = scanner.nextLine().trim();
            if (!apellido.isEmpty()) e.setApellido(apellido);

            empleadoService.actualizar(e);
            System.out.println("Empleado actualizado correctamente.");

        } catch (Exception ex) {
            System.out.println("Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminarEmpleado() {
        System.out.print("Ingrese ID de empleado a eliminar: ");
        long id = leerLong();
        try {
            empleadoService.eliminar(id);
            System.out.println("Empleado eliminado.");
        } catch (Exception ex) {
            System.out.println("Error al eliminar: " + ex.getMessage());
        }
    }

    
    // submenu legajos
    
    private void menuLegajos() {
        int opcion;
        do {
            System.out.println("\n--- GESTION DE LEGAJOS ---");
            System.out.println("1. Crear legajo");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Buscar por numero");
            System.out.println("4. Listar todos");
            System.out.println("5. Actualizar legajo");
            System.out.println("6. Eliminar legajo");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> crearLegajo();
                case 2 -> buscarLegajoPorId();
                case 3 -> buscarLegajoPorNumero();
                case 4 -> listarLegajos();
                case 5 -> actualizarLegajo();
                case 6 -> eliminarLegajo();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void crearLegajo() {
        try {
            System.out.print("Numero de legajo: ");
            String nro = scanner.nextLine().trim();
            System.out.print("Categoria: ");
            String categoria = scanner.nextLine().trim().toUpperCase();
            System.out.print("Estado (ACTIVO/INACTIVO): ");
            String estadoStr = scanner.nextLine().trim().toUpperCase();

            EstadoLegajo estado = EstadoLegajo.valueOf(estadoStr);
            Legajo l = new Legajo(null, false, nro, categoria, estado, LocalDate.now(), "");

            legajoService.insertar(l);
            System.out.println("Legajo creado con exito.");

        } catch (Exception ex) {
            System.out.println("Error al crear legajo: " + ex.getMessage());
        }
    }

    private void buscarLegajoPorId() {
        System.out.print("Ingrese ID de legajo: ");
        long id = leerLong();
        Optional<Legajo> leg = legajoService.getById(id);
        leg.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No se encontro legajo con ID " + id)
        );
    }

    private void buscarLegajoPorNumero() {
        System.out.print("Ingrese numero de legajo: ");
        String nro = scanner.nextLine().trim();
        Optional<Legajo> leg = legajoService.getByNumero(nro);
        leg.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No se encontro legajo con numero " + nro)
        );
    }

    private void listarLegajos() {
        List<Legajo> legajos = legajoService.getAll();
        if (legajos.isEmpty()) System.out.println("No hay legajos registrados.");
        else legajos.forEach(System.out::println);
    }

    private void actualizarLegajo() {
        try {
            System.out.print("Ingrese ID del legajo: ");
            long id = leerLong();
            Optional<Legajo> legOpt = legajoService.getById(id);
            if (legOpt.isEmpty()) {
                System.out.println("Legajo no encontrado.");
                return;
            }

            Legajo l = legOpt.get();
            System.out.print("Nueva categoria (" + l.getCategoria() + "): ");
            String categoria = scanner.nextLine().trim();
            if (!categoria.isEmpty()) l.setCategoria(categoria.toUpperCase());

            legajoService.actualizar(l);
            System.out.println("Legajo actualizado correctamente.");

        } catch (Exception ex) {
            System.out.println("Error al actualizar legajo: " + ex.getMessage());
        }
    }

    private void eliminarLegajo() {
        System.out.print("Ingrese ID de legajo a eliminar: ");
        long id = leerLong();
        try {
            legajoService.eliminar(id);
            System.out.println("Legajo eliminado.");
        } catch (Exception ex) {
            System.out.println("Error al eliminar: " + ex.getMessage());
        }
    }

    // alta conjunta
    
    private void altaConjunta() {
        try {
            System.out.println("\n--- ALTA CONJUNTA EMPLEADO + LEGAJO ---");

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();
            System.out.print("Apellido: ");
            String apellido = scanner.nextLine().trim();
            System.out.print("DNI: ");
            String dni = scanner.nextLine().trim();
            System.out.print("Area: ");
            String area = scanner.nextLine().trim().toUpperCase();
            System.out.print("Email (opcional): ");
            String email = scanner.nextLine().trim();

            System.out.print("Numero de legajo: ");
            String nro = scanner.nextLine().trim();
            System.out.print("Categoria: ");
            String categoria = scanner.nextLine().trim().toUpperCase();

            Empleado e = new Empleado();
            e.setNombre(nombre);
            e.setApellido(apellido);
            e.setDni(dni);
            e.setArea(area);
            e.setEmail(email);
            e.setFechaIngreso(LocalDate.now());


            Legajo l = new Legajo(null, false, nro, categoria, EstadoLegajo.ACTIVO, LocalDate.now(), "");

            empleadoLegajoService.altaEmpleadoConLegajo(e, l);
            System.out.println("Alta conjunta realizada con exito.");

        } catch (Exception ex) {
            System.out.println("Error en alta conjunta: " + ex.getMessage());
        }
    }

    
    // utilitarios para que las entradas del usuario sean validas y que no crashee el menu
    
    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private long leerLong() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida, se esperaba un numero.");
            return -1;
        }
    }
}