package dao.impl;

import config.DatabaseConnection;
import dao.LegajoDao;
import entities.EstadoLegajo;
import entities.Legajo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC de {@link LegajoDao} para el acceso a datos de la entidad {@link Legajo}.
 *
 * <p>
 * Esta clase se encarga de realizar las operaciones CRUD sobre la tabla {@code Legajo}
 * utilizando la API JDBC ({@link Connection}, {@link PreparedStatement}, {@link ResultSet}).
 * </p>
 *
 * <p>
 * Responsabilidades principales:
 * </p>
 * <ul>
 *   <li>Insertar, leer, actualizar y realizar la baja lógica de legajos.</li>
 *   <li>Ofrecer métodos que gestionen su propia conexión y métodos que operen
 *       con una {@link Connection} externa para participar en transacciones
 *       coordinadas desde la capa Service.</li>
 *   <li>Mantener el mapeo entre filas de la tabla {@code Legajo} y la entidad
 *       {@link Legajo} del dominio.</li>
 * </ul>
 *
 * <p>
 * Todas las operaciones utilizan {@link PreparedStatement} para evitar inyección SQL
 * y mejorar la legibilidad del código.
 * </p>
 *
 * @author Grupo 8
 * @version 1.0
 */
public class LegajoDaoJdbcImpl implements LegajoDao {

    // ─────────────────────────────────────────────────────────────
    // Sentencias SQL parametrizadas
    // ─────────────────────────────────────────────────────────────

    /**
     * Inserta un nuevo legajo. El campo eliminado se inicializa en 0 (false).
     */
    private static final String INSERT_SQL = """
            INSERT INTO Legajo (eliminado, nroLegajo, categoria, estado, fechaAlta, observaciones)
            VALUES (0, ?, ?, ?, ?, ?)
            """;

    /**
     * Consulta un legajo por su ID, solo si no fue eliminado lógicamente.
     */
    private static final String SELECT_BY_ID_SQL = """
            SELECT id, eliminado, nroLegajo, categoria, estado, fechaAlta, observaciones
            FROM Legajo
            WHERE id = ? AND eliminado = 0
            """;

    /**
     * Recupera todos los legajos activos (no eliminados).
     */
    private static final String SELECT_ALL_SQL = """
            SELECT id, eliminado, nroLegajo, categoria, estado, fechaAlta, observaciones
            FROM Legajo
            WHERE eliminado = 0
            """;

    /**
     * Actualiza los campos de un legajo existente.
     */
    private static final String UPDATE_SQL = """
            UPDATE Legajo
            SET nroLegajo = ?, categoria = ?, estado = ?, fechaAlta = ?, observaciones = ?
            WHERE id = ? AND eliminado = 0
            """;

    /**
     * Realiza la baja lógica de un legajo (eliminado = 1).
     */
    private static final String LOGICAL_DELETE_SQL = """
            UPDATE Legajo
            SET eliminado = 1
            WHERE id = ?
            """;

    /**
     * Busca un legajo por su número único.
     */
    private static final String SELECT_BY_NRO_SQL = """
            SELECT id, eliminado, nroLegajo, categoria, estado, fechaAlta, observaciones
            FROM Legajo
            WHERE nroLegajo = ? AND eliminado = 0
            """;

    // ─────────────────────────────────────────────────────────────
    // Métodos sin Connection (crean y cierran su propia conexión)
    // ─────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * <p>Este método abre y cierra su propia conexión a la base de datos.</p>
     */
    @Override
    public Legajo crear(Legajo legajo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return crear(legajo, conn);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Este método abre y cierra su propia conexión a la base de datos.</p>
     */
    @Override
    public Optional<Legajo> leer(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return leer(id, conn);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Este método abre y cierra su propia conexión a la base de datos.</p>
     */
    @Override
    public List<Legajo> leerTodos() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return leerTodos(conn);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Este método abre y cierra su propia conexión a la base de datos.</p>
     */
    @Override
    public Legajo actualizar(Legajo legajo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return actualizar(legajo, conn);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Este método abre y cierra su propia conexión a la base de datos.</p>
     */
    @Override
    public void eliminar(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            eliminar(id, conn);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Este método abre y cierra su propia conexión a la base de datos.</p>
     */
    @Override
    public Optional<Legajo> buscarPorNroLegajo(String nroLegajo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return buscarPorNroLegajo(nroLegajo, conn);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Métodos con Connection externa (uso en transacciones Service)
    // ─────────────────────────────────────────────────────────────

    /**
     * Inserta un nuevo legajo utilizando una conexión externa.
     *
     * @param legajo entidad a insertar.
     * @param conn   conexión JDBC activa (no se cierra en este método).
     * @return el legajo con su ID generado.
     * @throws SQLException si ocurre un error de inserción o si estado/fechaAlta son nulos.
     */
    @Override
    public Legajo crear(Legajo legajo, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, legajo.getNroLegajo());
            ps.setString(2, legajo.getCategoria());

            if (legajo.getEstado() != null) {
                ps.setString(3, legajo.getEstado().name()); // ACTIVO / INACTIVO
            } else {
                throw new SQLException("El estado del legajo no puede ser nulo.");
            }

            if (legajo.getFechaAlta() != null) {
                ps.setDate(4, Date.valueOf(legajo.getFechaAlta()));
            } else {
                throw new SQLException("La fechaAlta del legajo no puede ser nula.");
            }

            ps.setString(5, legajo.getObservaciones());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo insertar el legajo");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    legajo.setId(rs.getLong(1));
                }
            }
        }
        return legajo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Legajo> leer(long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLegajo(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Legajo> leerTodos(Connection conn) throws SQLException {
        List<Legajo> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLegajo(rs));
            }
        }

        return lista;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Legajo actualizar(Legajo legajo, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, legajo.getNroLegajo());
            ps.setString(2, legajo.getCategoria());

            if (legajo.getEstado() != null) {
                ps.setString(3, legajo.getEstado().name());
            } else {
                throw new SQLException("El estado del legajo no puede ser nulo.");
            }

            if (legajo.getFechaAlta() != null) {
                ps.setDate(4, Date.valueOf(legajo.getFechaAlta()));
            } else {
                throw new SQLException("La fechaAlta del legajo no puede ser nula.");
            }

            ps.setString(5, legajo.getObservaciones());
            ps.setLong(6, legajo.getId());

            ps.executeUpdate();
        }
        return legajo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminar(long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(LOGICAL_DELETE_SQL)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Legajo> buscarPorNroLegajo(String nroLegajo, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_NRO_SQL)) {
            ps.setString(1, nroLegajo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLegajo(rs));
                }
            }
        }
        return Optional.empty();
    }

    // ─────────────────────────────────────────────────────────────
    // Mapper ResultSet -> Legajo
    // ─────────────────────────────────────────────────────────────

    /**
     * Mapea una fila del {@link ResultSet} a un objeto {@link Legajo}.
     *
     * @param rs resultado de la consulta posicionado en una fila válida.
     * @return instancia de {@link Legajo} con los datos de la fila.
     * @throws SQLException si ocurre un error al leer las columnas.
     */
    private Legajo mapearLegajo(ResultSet rs) throws SQLException {
        Legajo l = new Legajo();

        l.setId(rs.getLong("id"));
        l.setEliminado(rs.getBoolean("eliminado"));
        l.setNroLegajo(rs.getString("nroLegajo"));
        l.setCategoria(rs.getString("categoria"));

        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            l.setEstado(EstadoLegajo.valueOf(estadoStr)); // ACTIVO / INACTIVO
        }

        Date fechaAlta = rs.getDate("fechaAlta");
        l.setFechaAlta(fechaAlta != null ? fechaAlta.toLocalDate() : null);

        l.setObservaciones(rs.getString("observaciones"));

        return l;
    }
}
