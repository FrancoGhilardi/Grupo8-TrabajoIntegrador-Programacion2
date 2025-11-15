package main;

import entities.Empleado;
import entities.Legajo;
import entities.EstadoLegajo;

import service.EmpleadoService;
import service.LegajoService;
import service.EmpleadoLegajoService;
import service.ServiceModule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Objects;


/**
 * Menú principal de la aplicación.
 *
 * Esta clase controla toda la interacción por consola con el usuario.
 * Permite gestionar empleados, legajos y realizar operaciones conjuntas.
 *
 * Responsabilidades principales:
 * 
 *   Mostrar menús y submenús.
 *   Leer entradas del usuario de forma segura.
 *   Invocar los servicios correspondientes.
 *   Manejar errores y mostrar mensajes claros.
 * 
 */
public class AppMenu {

    /** Lector de consola general para todo el menú. */
    private final Scanner scanner;

    /** Servicio para operaciones de empleados. */
    private final EmpleadoService empleadoService;

    /** Servicio para operaciones de legajos. */
    private final LegajoService legajoService;

    /** Servicio para operaciones combinadas Empleado + Legajo. */
    private final EmpleadoLegajoService empleadoLegajoService;
    
        // Constructor por defecto
    public AppMenu() {
        this(
                ServiceModule.empleadoService(),
                ServiceModule.legajoService(),
                ServiceModule.empleadoLegajoService(),
                new Scanner(System.in)
        );
    }
        // Constructor inyectable
    AppMenu(
            EmpleadoService empleadoService,
            LegajoService legajoService,
            EmpleadoLegajoService empleadoLegajoService,
            Scanner scanner
    ) 
    {
        this.empleadoService = Objects.requireNonNull(empleadoService);
        this.legajoService = Objects.requireNonNull(legajoService);
        this.empleadoLegajoService = Objects.requireNonNull(empleadoLegajoService);
        this.scanner = Objects.requireNonNull(scanner);
        }


    /**
     * Muestra el menú principal de la aplicación.
     *
     * Permite elegir entre gestionar empleados, legajos o realizar alta conjunta.
     */
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

    // 
    // SUBMENU EMPLEADOS
    // 

    /**
     * Muestra el submenU para operaciones sobre empleados.
     */
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

    /**
     * Crea un nuevo empleado solicitando los datos por consola.
     */
    private void crearEmpleado() {
    try {
        System.out.println("\n--- CREAR EMPLEADO ---");
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
        System.out.println("\nSeleccione un legajo disponible");
        List<Legajo> legajos = legajoService.getAll();

        if (legajos.isEmpty()) {
            System.out.println("No existen legajos disponibles. Debe crear uno antes.");
            return;
        }

        legajos.forEach(l -> 
            System.out.println("ID: " + l.getId() + " | Nro: " + l.getNroLegajo() + " | Estado: " + l.getEstado())
        );

        System.out.print("Ingrese el ID del legajo a asociar: ");
        long idLegajo = leerLong();

        Optional<Legajo> legOpt = legajoService.getById(idLegajo);
        if (legOpt.isEmpty()) {
            System.out.println("Legajo inexistente.");
            return;
        }

        Legajo leg = legOpt.get();

        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setApellido(apellido);
        e.setDni(dni);
        e.setArea(area);
        e.setEmail(email);
        e.setFechaIngreso(LocalDate.now());
        e.setLegajo(leg); 

        empleadoService.insertar(e);
        System.out.println("Empleado creado con éxito.");

        } catch (Exception ex) {
            System.out.println("Error al crear empleado: " + ex.getMessage());
        }
    }

    /**
     * Busca un empleado por su ID.
     */
    private void buscarEmpleadoPorId() {
        System.out.print("Ingrese ID de empleado: ");
        long id = leerLong();
        Optional<Empleado> emp = empleadoService.getById(id);
        emp.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No se encontro un empleado con ID " + id)
        );
    }

    /**
     * Busca un empleado por su DNI.
     */
    private void buscarEmpleadoPorDni() {
        System.out.print("Ingrese DNI: ");
        String dni = scanner.nextLine().trim();
        Optional<Empleado> emp = empleadoService.getByDni(dni);
        emp.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No se encontró un empleado con DNI " + dni)
        );
    }

    /**
     * Lista todos los empleados registrados.
     */
    private void listarEmpleados() {
        List<Empleado> empleados = empleadoService.getAll();
        if (empleados.isEmpty()) System.out.println("No hay empleados registrados.");
        else empleados.forEach(System.out::println);
    }

    /**
     * Actualiza los datos de un empleado existente.
     */
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
            System.out.println("\n--- Actualizando empleado ID " + id + " ---");

            System.out.print("Nuevo nombre (" + e.getNombre() + "): ");
            String nombre = scanner.nextLine().trim();
            if (!nombre.isEmpty()) e.setNombre(nombre);

            System.out.print("Nuevo apellido (" + e.getApellido() + "): ");
            String apellido = scanner.nextLine().trim();
            if (!apellido.isEmpty()) e.setApellido(apellido);

            System.out.print("Nueva area (" + e.getArea() + "): ");
            String area = scanner.nextLine().trim();
            if (!area.isEmpty()) e.setArea(area.toUpperCase());

            System.out.print("Nuevo email (" + e.getEmail() + "): ");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) e.setEmail(email);

            empleadoService.actualizar(e);
            System.out.println("Empleado actualizado correctamente.");

        } catch (Exception ex) {
            System.out.println("Error al actualizar empleado: " + ex.getMessage());
        }
    }
    /**
     * Elimina un empleado por ID .
     */
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

    
    // SUBMENU LEGAJOS
   

    /**
     * Muestra el submenú de gestión de legajos.
     */
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

    /**
     * Crea un nuevo legajo solicitando los datos por consola.
     */
    private void crearLegajo() {
        try {
            System.out.print("Numero de legajo: ");
            String nro = scanner.nextLine().trim().toUpperCase();
            
            System.out.print("Categoria: ");
            String categoria = scanner.nextLine().trim().toUpperCase();
            System.out.print("Estado (ACTIVO/INACTIVO): ");
            
            String estadoStr = scanner.nextLine().trim().toUpperCase();
            EstadoLegajo estado = EstadoLegajo.valueOf(estadoStr);

            System.out.print("Observaciones (opcional): ");
            String obs = scanner.nextLine().trim();

            Legajo l = new Legajo(null, false, nro, categoria, estado, LocalDate.now(), obs);
            legajoService.insertar(l);
            System.out.println("Legajo creado con exito.");

        } catch (Exception ex) {
            System.out.println("Error al crear legajo: " + ex.getMessage());
        }
    }

    /**
     * Busca un legajo por su ID.
     */
    private void buscarLegajoPorId() {
        System.out.print("Ingrese ID de legajo: ");
        long id = leerLong();
        Optional<Legajo> leg = legajoService.getById(id);
        leg.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No se encontro legajo con ID " + id)
        );
    }

    /**
     * Busca un legajo por su número único.
     */
    private void buscarLegajoPorNumero() {
        System.out.print("Ingrese numero de legajo: ");
        String nro = scanner.nextLine().trim();
        Optional<Legajo> leg = legajoService.getByNumero(nro);
        leg.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No se encontro legajo con numero " + nro)
        );
    }

    /**
     * Lista todos los legajos almacenados.
     */
    private void listarLegajos() {
        List<Legajo> legajos = legajoService.getAll();
        if (legajos.isEmpty()) System.out.println("No hay legajos registrados.");
        else legajos.forEach(System.out::println);
    }

    /**
     * Actualiza información de un legajo existente.
     */
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
            System.out.println("\n--- Actualizando legajo ID " + id + " ---");

            System.out.print("Nueva categoria (" + l.getCategoria() + "): ");
            String categoria = scanner.nextLine().trim();
            if (!categoria.isEmpty()) l.setCategoria(categoria.toUpperCase());

            System.out.print("Nuevo estado (" + l.getEstado().name() + ") [ACTIVO/INACTIVO]: ");
            String estado = scanner.nextLine().trim().toUpperCase();
            if (!estado.isEmpty())
                l.setEstado(EstadoLegajo.valueOf(estado));

            System.out.print("Nuevas observaciones (" + l.getObservaciones() + "): ");
            String obs = scanner.nextLine().trim();
            if (!obs.isEmpty()) l.setObservaciones(obs);

            legajoService.actualizar(l);
            System.out.println("Legajo actualizado correctamente.");

        } catch (Exception ex) {
            System.out.println("Error al actualizar legajo: " + ex.getMessage());
        }
    }

    /**
     * Elimina un legajo por ID.
     */
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

    
    // ALTA CONJUNTA
    
    /**
     * Realiza la creación simultanea de un empleado y su legajo,
     * en una unica transacción.
     */
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
            String nro = scanner.nextLine().trim().toUpperCase();
            System.out.print("Categoria: ");
            String categoria = scanner.nextLine().trim().toUpperCase();
            System.out.print("Observaciones (opcional): ");
            String obs = scanner.nextLine().trim();
            
            Empleado e = new Empleado();
            e.setNombre(nombre);
            e.setApellido(apellido);
            e.setDni(dni);
            e.setArea(area);
            e.setEmail(email);
            e.setFechaIngreso(LocalDate.now());

            Legajo l = new Legajo(null, false, nro, categoria, EstadoLegajo.ACTIVO, LocalDate.now(), obs);

            empleadoLegajoService.altaEmpleadoConLegajo(e, l);
            System.out.println("Alta conjunta realizada con exito.");

        } catch (Exception ex) {
            System.out.println("Error en alta conjunta: " + ex.getMessage());
        }
    }

    // UTILITARIOS

    /**
     * lee un valor entero desde consola.
     *
     * si la entrada no es valida, retorna -1 para evitar que el programa falle.
     *
     * @return numero entero ingresado o -1 si la entrada es invalida
     */
    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    

    /**
     * lee un valor long desde consola.
     *
     * muestra un mensaje si la entrada es invalida y retorna -1.
     *
     * @return numero long ingresado o -1 si ocurrió un error
     */
    private long leerLong() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida, se esperaba un numero.");
            return -1;
        }
    }
}