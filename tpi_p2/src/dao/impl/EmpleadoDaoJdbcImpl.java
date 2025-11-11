package dao.impl;

import config.DatabaseConnection;
import dao.EmpleadoDao;
import entities.Empleado;
import entities.Legajo;
import entities.EstadoLegajo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC de {@link EmpleadoDao} para el acceso a datos de la entidad {@link Empleado}.
 *
 * <p>
 * Esta clase utiliza la API JDBC (clases {@link Connection}, {@link PreparedStatement},
 * {@link ResultSet}) para interactuar con la base de datos MySQL.
 * </p>
 *
 * <p>
 * Responsabilidades principales:
 * </p>
 * <ul>
 *   <li>Ejecutar operaciones CRUD (crear, leer, actualizar, eliminar lógico) sobre la tabla {@code Empleado}.</li>
 *   <li>Recuperar el {@link Legajo} asociado mediante un LEFT JOIN, respetando la relación 1→1.</li>
 *   <li>Ofrecer métodos que gestionen su propia conexión y métodos que operen con una {@link Connection}
 *       externa para participar en transacciones coordinadas desde la capa Service.</li>
 * </ul>
 *
 * <p>
 * Todas las consultas se realizan utilizando {@link PreparedStatement} para evitar inyección SQL
 * y mejorar la legibilidad.
 * </p>
 *
 * @author Grupo 8
 * @version 1.0
 */
public class EmpleadoDaoJdbcImpl implements EmpleadoDao {

    // ─────────────────────────────────────────────────────────────
    // Sentencias SQL parametrizadas
    // ─────────────────────────────────────────────────────────────

    /**
     * Inserta un nuevo empleado asociándolo a un legajo existente.
     */
    private static final String INSERT_SQL = """
            INSERT INTO Empleado (nombre, apellido, dni, email, fechaIngreso, area, legajo, eliminado)
            VALUES (?, ?, ?, ?, ?, ?, ?, 0)
            """;

    /**
     * Consulta un empleado por ID junto con su legajo (si no fue eliminado).
     */
    private static final String SELECT_BY_ID_SQL = """
            SELECT e.*,
                   l.id AS l_id,
                   l.nroLegajo,
                   l.categoria,
                   l.estado,
                   l.fechaAlta,
                   l.observaciones,
                   l.eliminado AS l_eliminado
            FROM Empleado e
            LEFT JOIN Legajo l ON l.id = e.legajo AND l.eliminado = 0
            WHERE e.id = ? AND e.eliminado = 0
            """;

    /**
     * Lista todos los empleados activos junto con su legajo asociado.
     */
    private static final String SELECT_ALL_SQL = """
            SELECT e.*,
                   l.id AS l_id,
                   l.nroLegajo,
                   l.categoria,
                   l.estado,
                   l.fechaAlta,
                   l.observaciones,
                   l.eliminado AS l_eliminado
            FROM Empleado e
            LEFT JOIN Legajo l ON l.id = e.legajo AND l.eliminado = 0
            WHERE e.eliminado = 0
            """;

    /**
     * Actualiza los campos básicos de un empleado.
     */
    private static final String UPDATE_SQL = """
            UPDATE Empleado
            SET nombre = ?, apellido = ?, dni = ?, email = ?, fechaIngreso = ?, area = ?
            WHERE id = ? AND eliminado = 0
            """;

    /**
     * Realiza la baja lógica de un empleado (marca eliminado = 1).
     */
    private static final String LOGICAL_DELETE_SQL = """
            UPDATE Empleado
            SET eliminado = 1
            WHERE id = ?
            """;

    /**
     * Busca un empleado por DNI junto con su legajo.
     */
    private static final String SELECT_BY_DNI_SQL = """
            SELECT e.*,
                   l.id AS l_id,
                   l.nroLegajo,
                   l.categoria,
                   l.estado,
                   l.fechaAlta,
                   l.observaciones,
                   l.eliminado AS l_eliminado
            FROM Empleado e
            LEFT JOIN Legajo l ON l.id = e.legajo AND l.eliminado = 0
            WHERE e.dni = ? AND e.eliminado = 0
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
    public Empleado crear(Empleado empleado) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return crear(empleado, conn);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Este método abre y cierra su propia conexión a la base de datos.</p>
     */
    @Override
    public Optional<Empleado> leer(long id) throws SQLException {
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
    public List<Empleado> leerTodos() throws SQLException {
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
    public Empleado actualizar(Empleado empleado) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return actualizar(empleado, conn);
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
    public Optional<Empleado> buscarPorDni(String dni) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return buscarPorDni(dni, conn);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Métodos con Connection externa (uso en transacciones Service)
    // ─────────────────────────────────────────────────────────────

    /**
     * Inserta un nuevo empleado utilizando una conexión externa.
     * <p>
     * El empleado debe tener asociado un {@link Legajo} con ID válido,
     * ya que la FK {@code Empleado.legajo} es NOT NULL.
     * </p>
     *
     * @param empleado empleado a insertar.
     * @param conn     conexión JDBC existente (no se cierra en este método).
     * @return el empleado con su ID generado.
     * @throws SQLException si ocurre algún error al insertar o si el legajo es nulo/inválido.
     */
    @Override
    public Empleado crear(Empleado empleado, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getDni());
            ps.setString(4, empleado.getEmail());

            if (empleado.getFechaIngreso() != null) {
                ps.setDate(5, Date.valueOf(empleado.getFechaIngreso()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setString(6, empleado.getArea());

            if (empleado.getLegajo() != null && empleado.getLegajo().getId() != null) {
                ps.setLong(7, empleado.getLegajo().getId());
            } else {
                throw new SQLException("El empleado debe tener un legajo con id para insertarlo.");
            }

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo insertar el empleado");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    empleado.setId(rs.getLong(1));
                }
            }
        }
        return empleado;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Empleado> leer(long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEmpleadoConLegajo(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Empleado> leerTodos(Connection conn) throws SQLException {
        List<Empleado> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearEmpleadoConLegajo(rs));
            }
        }

        return lista;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Empleado actualizar(Empleado empleado, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getDni());
            ps.setString(4, empleado.getEmail());

            if (empleado.getFechaIngreso() != null) {
                ps.setDate(5, Date.valueOf(empleado.getFechaIngreso()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setString(6, empleado.getArea());
            ps.setLong(7, empleado.getId());

            ps.executeUpdate();
        }
        return empleado;
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
    public Optional<Empleado> buscarPorDni(String dni, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_DNI_SQL)) {
            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEmpleadoConLegajo(rs));
                }
            }
        }
        return Optional.empty();
    }

    // ─────────────────────────────────────────────────────────────
    // Mapper ResultSet -> Empleado (y Legajo asociado)
    // ─────────────────────────────────────────────────────────────

    /**
     * Mapea una fila del {@link ResultSet} a un objeto {@link Empleado},
     * incluyendo el {@link Legajo} si está presente en el JOIN.
     *
     * @param rs resultado de la consulta posicionado en una fila válida.
     * @return instancia de {@link Empleado} con sus datos y, si corresponde, el legajo asociado.
     * @throws SQLException si ocurre un error al leer las columnas.
     */
    private Empleado mapearEmpleadoConLegajo(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setId(rs.getLong("id"));
        e.setEliminado(rs.getBoolean("eliminado"));
        e.setNombre(rs.getString("nombre"));
        e.setApellido(rs.getString("apellido"));
        e.setDni(rs.getString("dni"));
        e.setEmail(rs.getString("email"));

        Date fechaIngreso = rs.getDate("fechaIngreso");
        e.setFechaIngreso(fechaIngreso != null ? fechaIngreso.toLocalDate() : null);

        e.setArea(rs.getString("area"));

        Long legajoId = rs.getLong("l_id");
        if (!rs.wasNull()) {
            Legajo l = new Legajo();
            l.setId(legajoId);
            l.setEliminado(rs.getBoolean("l_eliminado"));
            l.setNroLegajo(rs.getString("nroLegajo"));
            l.setCategoria(rs.getString("categoria"));

            String estadoStr = rs.getString("estado");
            if (estadoStr != null) {
                l.setEstado(EstadoLegajo.valueOf(estadoStr));
            }

            Date fechaAlta = rs.getDate("fechaAlta");
            l.setFechaAlta(fechaAlta != null ? fechaAlta.toLocalDate() : null);

            l.setObservaciones(rs.getString("observaciones"));

            e.setLegajo(l);
        }

        return e;
    }
}
