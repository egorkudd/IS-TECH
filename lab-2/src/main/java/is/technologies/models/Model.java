package is.technologies.models;

/**
 * Interface for models of database
 * All models have to have id of long type
 */
public interface Model {
    long getId();
    void setId(long id);
}
