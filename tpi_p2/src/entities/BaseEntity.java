package entities;

import java.util.Objects;

/**
 * Clase abstracta base para todas las entidades del sistema.
 * 
 * Proporciona los campos comunes requeridos por el TPI:
 * - id: Identificador único de la entidad (Primary Key)
 * - eliminado: Flag para implementar eliminación lógica (soft delete)
 * 
 * Esta clase implementa el patrón de diseño "Template" al definir
 * la estructura común que heredarán todas las entidades del dominio.
 * 
 * @author Grupo 8
 * @version 1.0
 */
public abstract class BaseEntity {
    
    /**
     * Identificador único de la entidad.
     * Corresponde a la clave primaria (PK) en la base de datos.
     */
    private Long id;
    
    /**
     * Flag de eliminación lógica.
     * - true: la entidad ha sido dada de baja (soft delete)
     * - false o null: la entidad está activa
     * 
     * Se utiliza para mantener la integridad referencial en la BD
     * sin eliminar físicamente los registros.
     */
    private Boolean eliminado;

    /**
     * Obtiene el identificador único de la entidad.
     * 
     * @return el ID de la entidad
     */
    public Long getId() { 
        return id; 
    }
    
    /**
     * Establece el identificador único de la entidad.
     * 
     * @param id el ID a asignar
     */
    public void setId(Long id) { 
        this.id = id; 
    }

    /**
     * Obtiene el estado de eliminación lógica de la entidad.
     * 
     * @return true si está eliminada, false o null si está activa
     */
    public Boolean getEliminado() { 
        return eliminado; 
    }
    
    /**
     * Establece el estado de eliminación lógica de la entidad.
     * 
     * @param eliminado true para marcar como eliminada, false para activa
     */
    public void setEliminado(Boolean eliminado) { 
        this.eliminado = eliminado; 
    }

    /**
     * Verifica si la entidad está activa (no eliminada lógicamente).
     * 
     * @return true si la entidad NO está dada de baja lógica
     */
    public boolean isActiva() {
        return eliminado == null || !eliminado;
    }

    /**
     * Marca la entidad como eliminada lógicamente.
     * Este método implementa el soft delete, manteniendo el registro
     * en la base de datos pero marcándolo como inactivo.
     */
    public void marcarEliminado() { 
        this.eliminado = true; 
    }
    
    /**
     * Marca la entidad como activa (no eliminada).
     * Permite reactivar una entidad previamente eliminada lógicamente.
     */
    public void marcarActivo() { 
        this.eliminado = false; 
    }

    /**
     * Compara esta entidad con otro objeto para determinar igualdad.
     * Dos entidades son iguales si tienen el mismo ID.
     * 
     * @param o el objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Genera un código hash para la entidad basado en su ID.
     * Necesario para mantener el contrato equals-hashCode.
     * 
     * @return el código hash de la entidad
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
