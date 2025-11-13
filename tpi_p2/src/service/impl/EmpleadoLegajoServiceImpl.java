package service.impl;

import dao.EmpleadoDao;
import dao.LegajoDao;
import dao.impl.EmpleadoDaoJdbcImpl;
import dao.impl.LegajoDaoJdbcImpl;
import entities.Empleado;
import entities.Legajo;
import service.AbstractTransactionalService;
import service.EmpleadoLegajoService;
import service.ServiceException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

/**
 * Implementación atómica de alta de {@link Empleado} + {@link Legajo}.
 *
 * <p>Se ejecuta en una única transacción: si algo falla (validación, unicidad,
 * I/O), se hace rollback completo.</p>
 */
public class EmpleadoLegajoServiceImpl extends AbstractTransactionalService implements EmpleadoLegajoService {

    private final EmpleadoDao empleadoDao;
    private final LegajoDao legajoDao;

    /**
     * Ctor por defecto: usa impls JDBC.
     */
    public EmpleadoLegajoServiceImpl() {
        this(new EmpleadoDaoJdbcImpl(), new LegajoDaoJdbcImpl());
    }

    /**
     * Ctor inyectable (tests/overrides).
     */
    public EmpleadoLegajoServiceImpl(EmpleadoDao empleadoDao, LegajoDao legajoDao) {
        this.empleadoDao = requireNonNull(empleadoDao, "empleadoDao no puede ser null");
        this.legajoDao   = requireNonNull(legajoDao,   "legajoDao no puede ser null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Empleado altaEmpleadoConLegajo(Empleado empleado, Legajo legajo) {
        return withTransaction(conn -> {
            // --- Normalización simple
            requireNonNull(empleado, "Empleado requerido");
            requireNonNull(legajo,   "Legajo requerido");
            normalizarEmpleado(empleado);
            normalizarLegajo(legajo);

            // --- Validaciones de negocio
            validarLegajoAlta(legajo);
            validarEmpleadoAlta(empleado);

            // --- Unicidades en DB
            try {
                // nroLegajo único
                Optional<Legajo> legajoExistente = legajoDao.buscarPorNroLegajo(legajo.getNroLegajo(), conn);
                if (legajoExistente.isPresent()) {
                    throw ServiceException.conflict("Ya existe un legajo con número " + legajo.getNroLegajo());
                }
                // DNI único
                Optional<Empleado> empPorDni = empleadoDao.buscarPorDni(empleado.getDni(), conn);
                if (empPorDni.isPresent()) {
                    throw ServiceException.conflict("Ya existe un empleado con DNI " + empleado.getDni());
                }

                // --- Persistencia
                Legajo legajoCreado = legajoDao.crear(legajo, conn);

                // Asociar el legajo recién creado al empleado antes de crear
                empleado.setLegajo(legajoCreado);

                Empleado empleadoCreado = empleadoDao.crear(empleado, conn);

                return empleadoCreado;

            } catch (ServiceException se) {
                throw se;
            } catch (SQLIntegrityConstraintViolationException dup) {
                throw ServiceException.conflict("Violación de unicidad al crear empleado/legajo: " + dup.getMessage());
            } catch (Exception ex) {
                throw ServiceException.infrastructure("No se pudo completar el alta de empleado + legajo", ex);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Validaciones específicas
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Valida los campos mínimos del empleado para alta.
     *
     * @param e empleado
     */
    private void validarEmpleadoAlta(Empleado e) {
        requireNonNull(e.getNombre(),  "Nombre requerido");
        requireNonNull(e.getApellido(), "Apellido requerido");

        // DNI: 7–11 dígitos
        String dni = requireNonNull(e.getDni(), "DNI requerido").trim();
        require(dni.matches("\\d{7,11}"), "DNI inválido (debe ser numérico, 7–11 dígitos)");
        e.setDni(dni);

        // Email opcional pero válido si viene
        if (e.getEmail() != null && !e.getEmail().isBlank()) {
            String email = e.getEmail().trim();
            require(email.contains("@") && email.indexOf('@') > 0 && email.indexOf('@') < email.length() - 1,
                    "Email inválido");
            e.setEmail(email);
        }

        // Fecha de ingreso no futura
        if (e.getFechaIngreso() != null) {
            require(!e.getFechaIngreso().isAfter(LocalDate.now()), "La fecha de ingreso no puede ser futura");
        }
    }

    /**
     * Valida los campos mínimos del legajo para alta.
     *
     * @param l legajo
     */
    private void validarLegajoAlta(Legajo l) {
        // Número requerido (3–20 alfanumérico, admite '-' y '.')
        String numero = requireNonNull(l.getNroLegajo(), "Número de legajo requerido").trim();
        require(numero.matches("[A-Za-z0-9][A-Za-z0-9.-]{2,19}"),
                "Número de legajo inválido (3–20 chars, alfanumérico, guión o punto)");
        l.setNroLegajo(numero);

        // Estado requerido
        requireNonNull(l.getEstado(), "El estado del legajo es requerido");

        // Fecha alta no futura (si viene)
        if (l.getFechaAlta() != null) {
            require(!l.getFechaAlta().isAfter(LocalDate.now()), "La fecha de alta no puede ser futura");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Normalizadores
    // ─────────────────────────────────────────────────────────────────────────────

    private void normalizarEmpleado(Empleado e) {
        if (e.getNombre()   != null) e.setNombre(e.getNombre().trim());
        if (e.getApellido() != null) e.setApellido(e.getApellido().trim());
        if (e.getArea()     != null) e.setArea(e.getArea().trim());
        if (e.getEmail()    != null) e.setEmail(e.getEmail().trim().toLowerCase(Locale.ROOT));
    }

    private void normalizarLegajo(Legajo l) {
        if (l.getNroLegajo()     != null) l.setNroLegajo(l.getNroLegajo().trim());
        if (l.getCategoria()     != null) l.setCategoria(l.getCategoria().trim());
        if (l.getObservaciones() != null) l.setObservaciones(l.getObservaciones().trim());
    }
}
