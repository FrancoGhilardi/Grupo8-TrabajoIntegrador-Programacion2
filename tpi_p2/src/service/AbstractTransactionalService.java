package service;

import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Clase base para servicios con manejo transaccional uniforme.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Obtener conexiones desde {@link DatabaseConnection}</li>
 *   <li>Configurar/Restaurar auto-commit y modo read-only</li>
 *   <li>Realizar {@code commit}/{@code rollback}</li>
 *   <li>Encapsular errores técnicos en {@link ServiceException}</li>
 * </ul>
 * </p>
 *
 * <p>Patrón de uso típico en subclases:
 * <pre>{@code
 *  return withTransaction(conn -> {
 *      // validaciones de negocio
 *      // invocar DAOs usando 'conn'
 *      // devolver resultado de alto nivel
 *  });
 * }</pre>
 * </p>
 */
public abstract class AbstractTransactionalService {

    /**
     * Ejecuta una unidad de trabajo en una transacción.
     *
     * <p>Realiza:
     * <ol>
     *   <li>{@code setAutoCommit(false)}</li>
     *   <li>Ejecuta el bloque de trabajo</li>
     *   <li>{@code commit()} si todo va bien, o {@code rollback()} ante excepciones</li>
     *   <li>Restaura el auto-commit previo y cierra la conexión</li>
     * </ol>
     * </p>
     *
     * @param work función que recibe la {@link Connection} transaccional y retorna un resultado
     * @param <R>  tipo del resultado
     * @return resultado del bloque de trabajo
     * @throws ServiceException si ocurre un error de validación o infraestructura
     */
    protected <R> R withTransaction(Function<Connection, R> work) {
        Connection conn = null;
        boolean previousAutoCommit = true;
        try {
            conn = DatabaseConnection.getConnection();
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            R result = work.apply(conn);

            conn.commit();
            return result;
        } catch (ServiceException se) {
            rollbackQuietly(conn);
            throw se;
        } catch (Exception ex) {
            rollbackQuietly(conn);
            throw ServiceException.infrastructure("Error transaccional en service", ex);
        } finally {
            restoreAndClose(conn, previousAutoCommit, false);
        }
    }

    /**
     * Variante sin valor de retorno para operaciones de solo efectos.
     *
     * @param work bloque de trabajo que puede lanzar excepciones
     * @throws ServiceException si ocurre un error de validación o infraestructura
     */
    protected void withTransactionVoid(ThrowingConsumer<Connection> work) {
        withTransaction(conn -> {
            try {
                work.accept(conn);
                return null;
            } catch (ServiceException se) {
                throw se;
            } catch (Exception ex) {
                throw ServiceException.infrastructure("Error transaccional (void)", ex);
            }
        });
    }


    /**
     * Ejecuta una unidad de trabajo en conexión <strong>read-only</strong> y auto-commit por defecto.
     *
     * <p>No inicia transacción explícita; es útil para consultas livianas y listas.</p>
     *
     * @param work función que recibe la {@link Connection} y retorna un resultado
     * @param <R>  tipo del resultado
     * @return resultado del bloque de trabajo
     * @throws ServiceException si ocurre un error técnico
     */
    protected <R> R withReadOnly(Function<Connection, R> work) {
        Connection conn = null;
        boolean previousReadOnly = false;
        try {
            conn = DatabaseConnection.getConnection();
            previousReadOnly = conn.isReadOnly();
            conn.setReadOnly(true);

            return work.apply(conn);
        } catch (ServiceException se) {
            throw se;
        } catch (Exception ex) {
            throw ServiceException.infrastructure("Error en operación read-only", ex);
        } finally {
            restoreAndClose(conn, null, previousReadOnly);
        }
    }

    // -----------------------
    // Helpers internos
    // -----------------------

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignore) {
                // no-op: estamos en manejo de error
            }
        }
    }

    /**
     * Restaura flags de conexión y cierra, con tolerancia a fallos.
     *
     * @param conn               conexión (puede ser null)
     * @param previousAutoCommit valor previo de auto-commit; si es null, no se restaura
     * @param previousReadOnly   valor previo de read-only; si es null, no se restaura
     */
    private void restoreAndClose(Connection conn, Boolean previousAutoCommit, Boolean previousReadOnly) {
        if (conn == null) return;
        try {
            if (previousAutoCommit != null) {
                conn.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException ignore) { /* no-op */ }
        try {
            if (previousReadOnly != null) {
                conn.setReadOnly(previousReadOnly);
            }
        } catch (SQLException ignore) { /* no-op */ }
        try {
            conn.close();
        } catch (SQLException ignore) { /* no-op */ }
    }

    /**
     * Functional interface para lambdas que pueden lanzar checked exceptions.
     *
     * @param <T> tipo del argumento
     */
    @FunctionalInterface
    protected interface ThrowingConsumer<T> {
        /**
         * Acepta un valor y puede lanzar una excepción.
         *
         * @param t valor de entrada
         * @throws Exception cualquier excepción checked
         */
        void accept(T t) throws Exception;
    }

    // -----------------------
    // Utilidades de validación
    // -----------------------

    /**
     * Verifica una condición de negocio; si no se cumple, lanza {@code ServiceException.validation}.
     *
     * @param condition condición a validar
     * @param message   mensaje de error si falla
     */
    protected void require(boolean condition, String message) {
        if (!condition) {
            throw ServiceException.validation(message);
        }
    }

    /**
     * Verifica no nulidad.
     *
     * @param value   valor a evaluar
     * @param message mensaje de error si es nulo
     * @param <T>     tipo
     * @return el mismo valor (para fluidez)
     */
    protected <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw ServiceException.validation(message);
        }
        return value;
    }
}
