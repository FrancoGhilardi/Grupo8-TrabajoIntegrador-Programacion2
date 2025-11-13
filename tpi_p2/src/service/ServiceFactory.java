package service;

import service.impl.EmpleadoServiceImpl;
import service.impl.LegajoServiceImpl;

/**
 * Fábrica/registro simple de servicios de dominio.
 *
 * <p>Objetivos:
 * <ul>
 *   <li>Proveer instancias listas para usar de {@link EmpleadoService} y {@link LegajoService}.</li>
 *   <li>Permitir <b>inyección de overrides</b> (dobles o implementaciones alternativas).</li>
 *   <li>Evitar dependencias circulares con frameworks de DI; es liviano y suficiente para el TFI.</li>
 * </ul>
 * </p>
 *
 * <h3>Uso típico</h3>
 * <pre>{@code
 *   EmpleadoService empleados = ServiceFactory.empleadoService();
 *   LegajoService   legajos   = ServiceFactory.legajoService();
 * }</pre>
 *
 * <h3>Overrides/Inyección</h3>
 * <pre>{@code
 *   // En tests o bootstrap:
 *   ServiceFactory.setEmpleadoService(new EmpleadoServiceFake());
 *   ServiceFactory.setLegajoService(new LegajoServiceFake());
 *   // ServiceFactory.resetOverrides(); // para volver a las impl por defecto
 * }</pre>
 */
public final class ServiceFactory {

    private ServiceFactory() { /* no instancias */ }

    // Overrides inyectables. Volatile para visibilidad entre hilos.
    private static volatile EmpleadoService empleadoServiceOverride;
    private static volatile LegajoService   legajoServiceOverride;

    /**
     * Obtiene la instancia de {@link EmpleadoService}.
     * <p>Si no hay override, retorna la implementación por defecto
     * {@link service.impl.EmpleadoServiceImpl}.</p>
     *
     * @return instancia de servicio
     */
    public static EmpleadoService empleadoService() {
        EmpleadoService s = empleadoServiceOverride;
        return (s != null) ? s : Defaults.EMPLEADOS;
    }

    /**
     * Define un override para {@link EmpleadoService}.
     * <p>Útil en tests o para cambiar la implementación en runtime.</p>
     *
     * @param service implementación a registrar
     */
    public static void setEmpleadoService(EmpleadoService service) {
        if (service == null) {
            throw new IllegalArgumentException("EmpleadoService override no puede ser null");
        }
        empleadoServiceOverride = service;
    }

    /**
     * Obtiene la instancia de {@link LegajoService}.
     * <p>Si no hay override, retorna la implementación por defecto
     * {@link service.impl.LegajoServiceImpl}.</p>
     *
     * @return instancia de servicio
     */
    public static LegajoService legajoService() {
        LegajoService s = legajoServiceOverride;
        return (s != null) ? s : Defaults.LEGAJOS;
    }

    /**
     * Define un override para {@link LegajoService}.
     *
     * @param service implementación a registrar
     */
    public static void setLegajoService(LegajoService service) {
        if (service == null) {
            throw new IllegalArgumentException("LegajoService override no puede ser null");
        }
        legajoServiceOverride = service;
    }

    /**
     * Limpia todos los overrides, restaurando las implementaciones por defecto.
     */
    public static void resetOverrides() {
        empleadoServiceOverride = null;
        legajoServiceOverride = null;
    }

    /**
     * Contenedor de singletons por defecto, thread-safe por inicialización estática.
     */
    private static final class Defaults {
        private static final EmpleadoService EMPLEADOS = new EmpleadoServiceImpl();
        private static final LegajoService   LEGAJOS   = new LegajoServiceImpl();
    }
}
