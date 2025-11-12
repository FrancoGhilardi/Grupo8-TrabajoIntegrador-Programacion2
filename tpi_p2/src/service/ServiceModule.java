package service;

import service.impl.EmpleadoLegajoServiceImpl;

/**
 * Módulo de ensamblado de servicios.
 *
 * <p>Objetivo: brindar un punto único de acceso a los servicios de dominio
 * sin acoplar al resto de la app a implementaciones JDBC ni a detalles de wiring.</p>
 *
 * <h3>Resumen</h3>
 * <ul>
 *   <li>Reutiliza {@link ServiceFactory} para {@link EmpleadoService} y {@link LegajoService}.</li>
 *   <li>Expone también {@link EmpleadoLegajoService} (alta atómica Empleado+Legajo).</li>
 *   <li>Permite overrides inyectables (tests, dobles, etc.).</li>
 * </ul>
 */
public final class ServiceModule {

    private ServiceModule() { /* no instancias */ }

    // ----------------------------
    // Singletons por defecto
    // ----------------------------
    private static final class Defaults {
        private static final EmpleadoLegajoService EMPLEADO_LEGAJO = new EmpleadoLegajoServiceImpl();
    }

    // Overrides inyectables (útiles para testing)
    private static volatile EmpleadoLegajoService empleadoLegajoOverride;

    /**
     * Obtiene el servicio de Empleado.
     * <p>Delegado de {@link ServiceFactory}.</p>
     */
    public static EmpleadoService empleadoService() {
        return ServiceFactory.empleadoService();
    }

    /**
     * Obtiene el servicio de Legajo.
     * <p>Delegado de {@link ServiceFactory}.</p>
     */
    public static LegajoService legajoService() {
        return ServiceFactory.legajoService();
    }

    /**
     * Obtiene el servicio compuesto de alta atómica Empleado + Legajo.
     *
     * @return instancia por defecto o el override inyectado
     */
    public static EmpleadoLegajoService empleadoLegajoService() {
        EmpleadoLegajoService s = empleadoLegajoOverride;
        return (s != null) ? s : Defaults.EMPLEADO_LEGAJO;
    }

    /**
     * Define un override para el servicio compuesto Empleado+Legajo.
     *
     * @param service implementación a registrar
     */
    public static void setEmpleadoLegajoService(EmpleadoLegajoService service) {
        if (service == null) {
            throw new IllegalArgumentException("EmpleadoLegajoService override no puede ser null");
        }
        empleadoLegajoOverride = service;
    }

    /**
     * Restaura las implementaciones por defecto.
     * <p>Para Empleado/Legajo usar {@link ServiceFactory#resetOverrides()} si se necesitan limpiar overrides allí.</p>
     */
    public static void resetOverrides() {
        empleadoLegajoOverride = null;
    }
}
