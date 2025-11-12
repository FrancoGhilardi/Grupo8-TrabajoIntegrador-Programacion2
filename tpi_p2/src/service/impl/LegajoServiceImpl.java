package service.impl;

import dao.LegajoDao;
import dao.impl.LegajoDaoJdbcImpl;
import entities.Legajo;
import service.AbstractTransactionalService;
import service.LegajoService;
import service.ServiceException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Implementación de {@link LegajoService} con manejo transaccional unificado.
 *
 * <p>Responsabilidades principales:
 * <ul>
 *   <li>Validar entradas: número de legajo, estado, fechas y strings.</li>
 *   <li>Orquestar transacciones con {@link #withTransaction} y lecturas con {@link #withReadOnly}.</li>
 *   <li>Invocar {@link LegajoDao} y traducir errores técnicos a {@link ServiceException}.</li>
 * </ul>
 * </p>
 */
public class LegajoServiceImpl extends AbstractTransactionalService implements LegajoService {

    private final LegajoDao legajoDao;

    /**
     * Constructor por defecto: instancia la implementación JDBC.
     */
    public LegajoServiceImpl() {
        this(new LegajoDaoJdbcImpl());
    }

    /**
     * Constructor inyectable.
     *
     * @param legajoDao dao a utilizar
     */
    public LegajoServiceImpl(LegajoDao legajoDao) {
        this.legajoDao = requireNonNull(legajoDao, "legajoDao no puede ser null");
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // CRUD
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     */
    @Override
    public Legajo insertar(Legajo entidad) {
        return withTransaction(conn -> {
            Legajo l = requireNonNull(entidad, "Legajo requerido");
            normalizarStrings(l);
            validarLegajoAlta(l, conn);

            try {
                return legajoDao.crear(l, conn);
            } catch (ServiceException se) {
                throw se;
            } catch (SQLIntegrityConstraintViolationException dup) {
                throw ServiceException.conflict("Número de legajo duplicado: " + dup.getMessage());
            } catch (Exception ex) {
                throw ServiceException.infrastructure("No se pudo insertar el legajo", ex);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Legajo actualizar(Legajo entidad) {
        return withTransaction(conn -> {
            Legajo l = requireNonNull(entidad, "Legajo requerido");
            require(l.getId() != null, "ID requerido para actualizar");
            normalizarStrings(l);
            validarLegajoActualizacion(l, conn);

            try {
                return legajoDao.actualizar(l, conn);
            } catch (SQLIntegrityConstraintViolationException dup) {
                throw ServiceException.conflict("Número de legajo duplicado al actualizar: " + dup.getMessage());
            } catch (Exception ex) {
                throw ServiceException.infrastructure("No se pudo actualizar el legajo", ex);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminar(long id) {
        withTransactionVoid(conn -> {
            Optional<Legajo> existente = legajoDao.leer(id, conn);
            if (existente.isEmpty()) {
                throw ServiceException.notFound("Legajo id=" + id + " no existe");
            }
            try {
                legajoDao.eliminar(id, conn);
            } catch (Exception ex) {
                throw ServiceException.infrastructure("No se pudo eliminar el legajo id=" + id, ex);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Legajo> getById(long id) {
        return withReadOnly(conn -> {
            try {
                return legajoDao.leer(id, conn);
            } catch (Exception ex) {
                throw ServiceException.infrastructure("Error leyendo legajo por id", ex);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Legajo> getAll() {
        return withReadOnly(conn -> {
            try {
                return legajoDao.leerTodos(conn);
            } catch (Exception ex) {
                throw ServiceException.infrastructure("Error listando legajos", ex);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Consultas específicas
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Legajo> getByNumero(String numero) {
        return withReadOnly(conn -> {
            String normalized = validarNumeroYDevolver(numero);
            try {
                return legajoDao.buscarPorNroLegajo(normalized, conn);
            } catch (Exception ex) {
                throw ServiceException.infrastructure("Error buscando legajo por número", ex);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByNumero(String numero) {
        return getByNumero(numero).isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Legajo> searchByEstado(String estadoLike) {
        final String needle = (estadoLike == null ? "" : estadoLike.trim().toLowerCase(Locale.ROOT));
        return withReadOnly(conn -> {
            try {
                List<Legajo> all = legajoDao.leerTodos(conn);
                if (needle.isEmpty()) return all;

                return all.stream()
                        .filter(l -> l.getEstado() != null
                                && l.getEstado().name().toLowerCase(Locale.ROOT).contains(needle))
                        .toList();
            } catch (Exception ex) {
                throw ServiceException.infrastructure("Error filtrando legajos por estado", ex);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Validaciones de negocio
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Valida precondiciones para alta de legajo.
     *
     * @param l     legajo a validar
     * @param conn  conexión activa
     */
    private void validarLegajoAlta(Legajo l, java.sql.Connection conn) {
        try {
            validarCampos(l);
            if (legajoDao.buscarPorNroLegajo(l.getNroLegajo(), conn).isPresent()) {
                throw ServiceException.conflict("Ya existe un legajo con número " + l.getNroLegajo());
            }
        } catch (ServiceException se) {
            throw se;
        } catch (Exception ex) {
            throw ServiceException.infrastructure("Error validando alta de legajo", ex);
        }
    }

    /**
     * Valida precondiciones para actualización.
     *
     * @param l     legajo a validar
     * @param conn  conexión activa
     */
    private void validarLegajoActualizacion(Legajo l, java.sql.Connection conn) {
        try {
            validarCampos(l);
            Optional<Legajo> porNumero = legajoDao.buscarPorNroLegajo(l.getNroLegajo(), conn);
            if (porNumero.isPresent() && !porNumero.get().getId().equals(l.getId())) {
                throw ServiceException.conflict("Otro legajo ya posee el número " + l.getNroLegajo());
            }
        } catch (ServiceException se) {
            throw se;
        } catch (Exception ex) {
            throw ServiceException.infrastructure("Error validando actualización de legajo", ex);
        }
    }

    /**
     * Valida campos obligatorios y consistencia de valores.
     *
     * @param l legajo a validar
     */
    private void validarCampos(Legajo l) {
        // Número
        String numero = validarNumeroYDevolver(l.getNroLegajo());
        l.setNroLegajo(numero);

        // Estado requerido
        requireNonNull(l.getEstado(), "El estado del legajo es requerido");

        // Fecha de alta no futura
        LocalDate hoy = LocalDate.now();
        if (l.getFechaAlta() != null) {
            require(!l.getFechaAlta().isAfter(hoy), "La fecha de alta no puede ser futura");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Normalización y helpers
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Normaliza strings básicos del legajo.
     *
     * @param l legajo
     */
    private void normalizarStrings(Legajo l) {
        if (l.getNroLegajo() != null) l.setNroLegajo(l.getNroLegajo().trim());
        if (l.getCategoria() != null) l.setCategoria(l.getCategoria().trim());
        if (l.getObservaciones() != null) l.setObservaciones(l.getObservaciones().trim());
    }

    /**
     * Valida formato del número de legajo y devuelve el valor normalizado.
     * <p>Regla: 3 a 20 caracteres alfanuméricos (se permiten guiones y puntos), sin espacios.</p>
     *
     * @param numero valor a validar
     * @return número normalizado
     */
    private String validarNumeroYDevolver(String numero) {
        requireNonNull(numero, "Número de legajo requerido");
        String n = numero.trim();
        require(n.matches("[A-Za-z0-9][A-Za-z0-9.-]{2,19}"),
                "Número de legajo inválido (3–20 chars, alfanumérico, guión o punto)");
        return n;
    }
}
