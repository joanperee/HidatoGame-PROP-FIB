package persistence;

import domain.model.Hidato;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Extends the JSON files' manager for Hidato objects.
 * Although this class was intentioned to store hidatos as .json
 * files, it has now been changed to follow the subject's rules.
 */
public class JsonHidatoManager extends JsonManager<Hidato, UUID> {

    /** Instance for making the JSON Hidato manager singleton */
    private static JsonHidatoManager instance;

    /**
     * Constructor for the Hidato manager. Sets the path, class type and ID type.
     */
    private JsonHidatoManager() {
        super("data/hidato", Hidato.class, UUID.class);
    }

    /**
     * Method to get the singleton instance of JsonHidatoManager
     * 
     * @return The singleton instance of JsonHidatoManager
     */
    public static JsonHidatoManager getInstance() {
        if (instance == null) {
            instance = new JsonHidatoManager();
        }
        return instance;
    }

    /**
     * Returns the UUID of a Hidato object.
     * 
     * @param obj hidato to get the id from.
     * @return UUID of the hidato.
     */
    @Override
    protected UUID getId(Hidato obj) {
        return obj.getIdH();
    }

    /**
     * Converts a hidato's ID from String to UUID.
     * 
     * @param str ID in string format.
     * @return UUID of the hidato.
     */
    @Override
    protected UUID IDfromString(String str) {
        return UUID.fromString(str);
    }

    /**
     * Gets the path for a hidato's JSON file.
     * 
     * @param id UUID of the hidato.
     * @return Path of the hidato's file.
     */
    @Override
    protected String getFilePath(UUID id) {
        return basePath + "/all/" + id + ".csv";
    }

    /**
     * Gets the path for te directory where hidatos are stored.
     * 
     * @return Path where all hidatos are stored.
     */
    @Override
    protected String getAllPath() {
        return basePath + "/all";
    }

    /**
     * Checks if /all and /lists subfolders exist. If not, creates them.
     */
    @Override
    protected void checkSubfolders() {
        File dirAll = new File(basePath + "/all");
        if (!dirAll.exists()) {
            dirAll.mkdirs();
        }
        File dirList = new File(basePath + "/lists");
        if (!dirList.exists()) {
            dirList.mkdirs();
        }
    }

    /**
     * Gets the list of all stored Hidato's IDs.
     *
     * @return List of all Hidatos UUIDs.
     */
    @Override
    public List<UUID> getAll() {
        File dir = new File(getAllPath());
        if (!dir.exists())
            return new ArrayList<>();

        File[] files = dir.listFiles();
        if (files == null)
            return new ArrayList<>();
        List<UUID> list = new ArrayList<>();

        for (File f : files) {
            String name = f.getName().replace(".csv", "");
            list.add(IDfromString(name));
        }
        return list;
    }


    /**
     * Implements the load method for the JsonHidatoManager.
     * The Hidato is loaded from a String format.
     * Even though initially it was planned to be saved as a JSON file,
     * it has been changed to csv to follow the subject's rules.
     * 
     * @param id UUID of the Hidato to load.
     * @return The loaded Hidato.
     */
    @Override
    public Hidato load(UUID id) {
        try (Scanner scanner = new Scanner(new File(getFilePath(id)))) {
            StringBuilder data = new StringBuilder();
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine()).append("\n");
            }
            Hidato h = Hidato.loadFromString(data.toString());
            h.setIdH(id);
            return h;
        } catch (IOException e) {
            throw new RuntimeException("Error loading: " + id, e);
        }
    }

    /**
     * Implements the save method for the JsonHidatoManager.
     * Since it's a complex abstract class with 3 implementations,
     * it can't be directly serialized. Therefore, it is stored as
     * a String, what also saves up a lot of space.
     * Even though initially it was planned to be saved as a JSON file,
     * it has been changed to csv to follow the subject's rules.
     * 
     * @param h The hidato to store.
     */
    @Override
    public void save(Hidato h) {
        UUID id = getId(h);
        File f = new File(getFilePath(id));
        f.getParentFile().mkdirs();

        try {
            Files.writeString(f.toPath(), Hidato.stringFromHidato(h));
        } catch (IOException e) {
            throw new RuntimeException("Error saving: " + id, e);
        }
    }

}
