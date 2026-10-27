package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that implements the interfaces for data and list management
 * for JSON files storage.
 * The name for an object file is the object's ID and the name for a list file
 * is the name of the list.
 * 
 * @param <T>  Type of the objects.
 * @param <ID> Type of the identification for the objects.
 */
public abstract class JsonManager<T, ID> implements DataManager<T, ID>, DataListManager<T, ID> {
    /** Object mapper to store and load objects. */
    protected final ObjectMapper mapper;
    /** Path of the directory where the objects/lists are stored. */
    protected String basePath;
    /** Class of the objects to store. */
    private final Class<T> classType;
    /** ID type of the objects to store. */
    private final Class<ID> idType;

    /**
     * Constructor for a JSON files/lists manager.
     * 
     * @param path      Path of the directory where to store the files.
     * @param classType Class Type of the objects to store.
     * @param idType    ID type of the objects to store.
     */
    public JsonManager(String path, Class<T> classType, Class<ID> idType) {
        this.basePath = path;
        this.classType = classType;
        this.idType = idType;
        this.mapper = new ObjectMapper();
        checkSubfolders();
    }

    // aux functions-------------------------------------------

    /**
     * Saves a list on the directory.
     * 
     * @param listName Name of the list to save (will also be the name of the file).
     * @param list     List of IDs to save.
     */
    private void saveList(String listName, List<ID> list) {
        try {
            File f = new File(basePath + "/lists/" + listName + ".json");
            mapper.writeValue(f, list);
        } catch (IOException e) {
            throw new RuntimeException("Error saving list: " + listName, e);
        }
    }

    /**
     * returns the ID of an object.
     * 
     * @param obj The object which to get the ID.
     * @return ID of the specified object.
     */
    protected abstract ID getId(T obj);

    /**
     * Transforms a string to the corresponding ID of the object type of the
     * manager.
     * 
     * @param str ID in string format.
     * @return ID obtained from the string.
     */
    protected abstract ID IDfromString(String str);

    /**
     * Gets the full path for a stored object.
     * It doesn't check if it exists, but if it does it's sure correct.
     * 
     * @param id ID of the object.
     * @return Path to the stored object.
     */
    protected abstract String getFilePath(ID id);

    /**
     * Gets the path to the directory where all objects of the manager are stored.
     * 
     * @return Path to the folder where all objects are stored.
     */
    protected abstract String getAllPath();

    /**
     * Checks if needed subfolders have been created and creates them if necessary.
     */
    protected abstract void checkSubfolders();

    // --------------------------------------------------------

    /**
     * Stores an object as a JSON file.
     * 
     * @param obj Object to store.
     */
    public void save(T obj) {

        ID id = getId(obj);
        try {
            File f = new File(getFilePath(id));
            f.getParentFile().mkdirs();
            mapper.writeValue(f, obj);

        } catch (IOException e) {
            throw new RuntimeException("Error saving: " + id, e);
        }

    }

    /**
     * Loads an object from a JSON file. Returns null if it doesn't exist.
     * 
     * @param id ID of the object to load.
     * @return The loaded object.
     */
    public T load(ID id) {
        try {
            File f = new File(getFilePath(id));
            if (!f.exists())
                return null;
            return mapper.readValue(f, classType);

        } catch (IOException e) {
            throw new RuntimeException("Error loading: " + id, e);
        }
    }

    /**
     * Deletes an object's file.
     * 
     * @param id ID of the object from the file to delete.
     */
    public void delete(ID id) {
        File f = new File(getFilePath(id));
        if (f.exists() && !f.delete()) {
            throw new RuntimeException("Could not delete file");
        }
    }

    /**
     * Gets the list of all stored object's IDs.
     * 
     * @return List of all objects IDs.
     */
    public List<ID> getAll() {
        File dir = new File(getAllPath());
        if (!dir.exists())
            return new ArrayList<>();

        File[] files = dir.listFiles();
        if (files == null)
            return new ArrayList<>();
        List<ID> list = new ArrayList<>();

        for (File f : files) {
            String name = f.getName().replace(".json", "");
            list.add(IDfromString(name));
        }
        return list;
    }

    /**
     * Adds an object to a list.
     * 
     * @param listName Name of the list.
     * @param id       ID to add to the list.
     */
    public void addTo(String listName, ID id) {
        List<ID> list = getList(listName);
        if (!list.contains(id)) {
            if (id != null) list.add(id);
            saveList(listName, list);
        }
    }

    /**
     * removes an object from a list.
     * 
     * @param listName Name of the list.
     * @param id       ID to remove from the list.
     */
    public void removeFrom(String listName, ID id) {
        List<ID> list = getList(listName);
        if (list.remove(id)) {
            saveList(listName, list);
        }
    }

    /**
     * Gets all the IDs form a stored list.
     * 
     * @param listName Name of the list.
     * @return The list of IDs.
     */
    public List<ID> getList(String listName) {
        try {
            File f = new File(basePath + "/lists/" + listName + ".json");
            if (!f.exists())
                return new ArrayList<>();

            return mapper.readValue(f, mapper.getTypeFactory().constructCollectionType(List.class, idType));
        } catch (IOException e) {
            throw new RuntimeException("Error getting list: " + listName, e);
        }
    }

    /**
     * Gets all the lists created and puts them in a ArrayList.
     * 
     * @return All the list names stored. 
     */
    public List<String> getLists() {
        java.io.File dir = new java.io.File(basePath + "/lists");
        List<String> result = new java.util.ArrayList<>();
        if (!dir.exists()) return result;
        for (java.io.File f : dir.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".json"))
                result.add(f.getName().replace(".json", ""));
        }
        return result;
    }

    /**
     * Deletes a list's file.
     * 
     * @param listName Name of the list.
     */
    public void deleteList(String listName) {
        File f = new File(basePath + "/lists/" + listName + ".json");
        if (f.exists() && !f.delete()) {
            throw new RuntimeException("Could not delete list");
        }
    }

    /**
     * Changes the main directory where the manager works.
     * It also chack if the necessary subfolders exist, and if not, creates them.
     * 
     * @param path New path of the directory.
     */
    public void setPath(String path) {
        this.basePath = path;
        checkSubfolders();
    }
}
