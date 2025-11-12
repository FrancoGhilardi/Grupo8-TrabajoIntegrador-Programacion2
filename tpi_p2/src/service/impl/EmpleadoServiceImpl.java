package service.impl;

import dao.EmpleadoDao;
import dao.LegajoDao;
import dao.impl.EmpleadoDaoJdbcImpl;
import dao.impl.LegajoDaoJdbcImpl;
import entities.Empleado;
import entities.Legajo;
import service.AbstractTransactionalService;
import service.EmpleadoService;
import service.ServiceException;

import java.sql.Connection;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Implementación de {@link EmpleadoService} con manejo transaccional.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Validar entradas (DNI, nombre/apellido, email, fechas).</li>
 *   <li>Orquestar transacciones con {@link #withTransaction} / {@link #withReadOnly}.</li>
 *   <li>Invocar DAOs y mapear errores técnicos a {@link ServiceException}.</li>
 *   <li>Hacer cumplir reglas 1→1: el {@link Legajo} debe existir y estar activo.</li>
 * </ul>
 * </p>
 */
public class EmpleadoServiceImpl extends AbstractTransactionalService implements EmpleadoService {

    private final EmpleadoDao empleadoDao;
    private final LegajoDao legajoDao;

    /**
     * Constructor por defecto: instancia DAOs JDBC.
     */
    public EmpleadoServiceImpl() {
        this(new EmpleadoDaoJdbcImpl(), new LegajoDaoJdbcImpl());
    }

    /**
     * Constructor para inyección.
     */
    public EmpleadoServiceImpl(EmpleadoDao empleadoDao, LegajoDao legajoDao) {
        this.empleadoDao = requireNonNull(empleadoDao, "empleadoDao no puede ser null");
        this.legajoDao = requireNonNull(legajoDao, "legajoDao no puede ser null");
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // CRUD
    // ─────────────────────────────────────────────────────────────────────────────

    @Override
    public Empleado insertar(Empleado entidad) {
        return withTransaction(conn -> {
            Empleado e = requireNonNull(entidad, "Empleado requerido");
            normalizarStrings(e);
            validarEmpleadoAlta(e, conn);

            try {
                return empleadoDao.crear(e, conn);
            } catch (ServiceException se) {
                throw se;
            } catch (SQLIntegrityConstraintViolationException dup) {
                throw ServiceException.conflict("Violación de unicidad (DNI o Legajo ya usado): " + dup.getMessage());
            } catch (Exception ex) {
                throw ServiceException.infrastructure("No se pudo insertar el empleado", ex);
            }
        });
    }

    @Override
    public Empleado actualizar(Empleado entidad) {
        return withTransaction(conn -> {
            Empleado e = requireNonNull(entidad, "Empleado requerido");
            require(e.getId() != null, "ID requerido para actualizar");
            normalizarStrings(e);
            validarEmpleadoActualizacion(e, conn);

            try {
                return empleadoDao.actualizar(e, conn);
            } catch (SQLIntegrityConstraintViolationException dup) {
                throw ServiceException.conflict("Violación de unicidad al actualizar (DNI o Legajo): " + dup.getMessage());
            } catch (Exception ex) {
                throw ServiceException.infrastructure("No se pudo actualizar el empleado", ex);
            }
        });
    }

    @Override
    public void eliminar(long id) {
        withTransactionVoid(conn -> {
            // Confirmar existencia antes de eliminar
            Optional<Empleado> existente = empleadoDao.leer(id, conn);
            if (existente.isEmpty()) {
                throw ServiceException.notFound("Empleado id=" + id + " no existe");
            }
            try {
                empleadoDao.eliminar(id, conn);
            } catch (Exception ex) {
                throw ServiceException.infrastructure("No se pudo eliminar el empleado id=" + id, ex);
            }
        });
    }

    @Override
    public Optional<Empleado> getById(long id) {
        return withReadOnly(conn -> {
            try {
                return empleadoDao.leer(id, conn);
            } catch (Exception ex) {
                throw ServiceException.infrastructure("Error leyendo empleado por id", ex);
            }
        });
    }

    @Override
    public List<Empleado> getAll() {
        return withReadOnly(conn -> {
            try {
                return empleadoDao.leerTodos(conn);
            } catch (Exception ex) {
                throw ServiceException.infrastructure("Error listando empleados", ex);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Consultas específicas de dominio
    // ─────────────────────────────────────────────────────────────────────────────

    @Override
    public Optional<Empleado> getByDni(String dni) {
        return withReadOnly(conn -> {
            String normalized = validarDniYDevolver(dni);
            try {
                return empleadoDao.buscarPorDni(normalized, conn);
            } catch (Exception ex) {
                throw ServiceException.infrastructure("Error buscando empleado por DNI", ex);
            }
        });
    }

    @Override
    public boolean existsByDni(String dni) {
        return getByDni(dni).isPresent();
    }

    /**
     * Búsqueda simple por apellido.
     * <p>Nota: si se requiere eficiencia con grandes volúmenes,
     * conviene agregar un método DAO que haga el filtro en SQL (LIKE + índice).</p>
     */
    @Override
    public List<Empleado> searchByApellido(String apellidoLike) {
        final String needle = (apellidoLike == null ? "" : apellidoLike.trim().toLowerCase(Locale.ROOT));
        return withReadOnly(conn -> {
            try {
                List<Empleado> all = empleadoDao.leerTodos(conn);
                return all.stream()
                        .filter(e -> e.getApellido() != null &&
                                e.getApellido().toLowerCase(Locale.ROOT).contains(needle))
                        .toList();
            } catch (Exception ex) {
                throw ServiceException.infrastructure("Error buscando por apellido", ex);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Validaciones de negocio
    // ─────────────────────────────────────────────────────────────────────────────

    private void validarEmpleadoAlta(Empleado e, Connection conn) {
        try {
            validarCamposObligatorios(e);
            validarFechas(e);

            // DNI único
            if (empleadoDao.buscarPorDni(e.getDni(), conn).isPresent()) {
                throw ServiceException.conflict("Ya existe un empleado con DNI " + e.getDni());
            }

            // Legajo existente y activo
            validarLegajoAsociado(e, conn);

        } catch (ServiceException se) {
            throw se;
        } catch (Exception ex) {
            throw ServiceException.infrastructure("Error validando alta de empleado", ex);
        }
    }

    private void validarEmpleadoActualizacion(Empleado e, Connection conn) {
        try {
            validarCamposObligatorios(e);
            validarFechas(e);

            // Unicidad de DNI permitiendo el propio
            Optional<Empleado> porDni = empleadoDao.buscarPorDni(e.getDni(), conn);
            if (porDni.isPresent() && !porDni.get().getId().equals(e.getId())) {
                throw ServiceException.conflict("Otro empleado ya posee el DNI " + e.getDni());
            }

            validarLegajoAsociado(e, conn);

        } catch (ServiceException se) {
            throw se;
        } catch (Exception ex) {
            throw ServiceException.infrastructure("Error validando actualización de empleado", ex);
        }
    }

    private void validarCamposObligatorios(Empleado e) {
        requireNonNull(e.getNombre(), "Nombre requerido");
        requireNonNull(e.getApellido(), "Apellido requerido");
        String dni = validarDniYDevolver(e.getDni());
        e.setDni(dni);

        // Email opcional, pero si viene debe tener formato básico válido
        if (e.getEmail() != null && !e.getEmail().isBlank()) {
            String email = e.getEmail().trim();
            require(email.contains("@") && email.indexOf('@') > 0 && email.indexOf('@') < email.length() - 1,
                    "Email inválido");
            e.setEmail(email);
        }

        // Área opcional pero normalizada si viene
        if (e.getArea() != null) {
            e.setArea(e.getArea().trim());
        }
    }

    private void validarFechas(Empleado e) {
        LocalDate hoy = LocalDate.now();
        if (e.getFechaIngreso() != null) {
            require(!e.getFechaIngreso().isAfter(hoy), "La fecha de ingreso no puede ser futura");
        }
    }

    private void validarLegajoAsociado(Empleado e, Connection conn) {
        try {
            requireNonNull(e.getLegajo(), "El empleado debe tener un legajo asociado");
            require(e.getLegajo().getId() != null, "El legajo debe tener ID");

            Optional<entities.Legajo> leg = legajoDao.leer(e.getLegajo().getId(), conn);
            if (leg.isEmpty()) {
                throw ServiceException.validation("El legajo id=" + e.getLegajo().getId() + " no existe");
            }
            if (Boolean.TRUE.equals(leg.get().getEliminado())) {
                throw ServiceException.validation("El legajo id=" + e.getLegajo().getId() + " está eliminado");
            }

        } catch (ServiceException se) {
            throw se;
        } catch (Exception ex) {
            throw ServiceException.infrastructure("Error validando legajo asociado", ex);
        }
    }

    private String validarDniYDevolver(String dni) {
        requireNonNull(dni, "DNI requerido");
        String normalized = dni.trim();
        require(normalized.matches("\\d{7,11}"), "DNI inválido (debe ser numérico, 7–11 dígitos)");
        return normalized;
    }

    private void normalizarStrings(Empleado e) {
        if (e.getNombre() != null) e.setNombre(e.getNombre().trim());
        if (e.getApellido() != null) e.setApellido(e.getApellido().trim());
    }
}
