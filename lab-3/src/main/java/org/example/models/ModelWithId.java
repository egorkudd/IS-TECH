package org.example.models;

/**
 * Interface for models of database
 * All models have to have id of long type
 */
public interface ModelWithId<T> {
    T getId();
    void setId(T id);
}
