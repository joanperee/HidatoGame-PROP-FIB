package persistence;

import java.util.List;

/**
 * Interface defining the functions of the persistence layer related to storage
 * of domain objects.
 * 
 * @param <T>  Class type of the objects.
 * @param <ID> Type of the identification for the objects.
 */
public interface DataManager<T, ID> {

    /**
     * Stores an object.
     * 
     * @param object Object to store.
     */
    void save(T object);

    /**
     * Loads an object.
     * 
     * @param id ID of the object to load.
     * @return The loaded object.
     */
    T load(ID id);

    /**
     * Deletes an object.
     * 
     * @param id ID of the object to store.
     */
    void delete(ID id);

    /**
     * Returns the list of stored objects.
     * 
     * @return List of IDs of the objects stored.
     */
    List<ID> getAll();

    /**
     * Changes the path for the directory where the lists are stored.
     * Does not transfer files to the new folder.
     * 
     * @param path New path of the directory.
     */
    void setPath(String path);
}
