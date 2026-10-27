package persistence;

import java.util.List;

/**
 * Interface defining the functions of the persistence layer related to managing
 * lists for domain objects.
 * 
 * @param <T>  Class type of the list's objects.
 * @param <ID> Type of the identification for the objects.
 */
public interface DataListManager<T, ID> {

    /**
     * Adds an object's id to a list.
     * 
     * @param listName Name of the list.
     * @param id       ID of the object to add to the list.
     */
    void addTo(String listName, ID id);

    /**
     * Removes an object's id from a list.
     * 
     * @param listName Name of the list.
     * @param id       ID of the object to remove from the list.
     */
    void removeFrom(String listName, ID id);

    /**
     * Returns a stored list.
     * 
     * @param listName Name of the list.
     * @return List of al ids on the list
     */
    List<ID> getList(String listName);

    /**
     * Returns a list of all the names of the lists created.
     * 
     * @return A list of strings. For instance {"All", "list1", "listRanked"}.
     */
    List<String> getLists();

    /**
     * Deletes a list.
     * 
     * @param listName Name of the list.
     */
    void deleteList(String listName);

    /**
     * Changes the path for the directory where the lists are stored.
     * Does not transfer files to the new folder.
     * 
     * @param path New path of the directory.
     */
    void setPath(String path);
}
