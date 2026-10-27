package persistence;

import domain.model.User;

/**
 * Extends the JSON files' manager for User objects.
 */
public class JsonUserManager extends JsonManager<User, String>{

    /** Instance for making the JSON User manager singleton*/
    private static JsonUserManager instance;

    /**
     * Constructor for the User manager. Sets the path, class type and ID type.
     */
    private JsonUserManager() {
        super("data/users", User.class, String.class);
    }

    /**
     * Method to get the singleton instance of JsonUserManager
     * @return The singleton instance of JsonUserManager
     */
    public static JsonUserManager getInstance() {
        if (instance == null) {
            instance = new JsonUserManager();
        }
        return instance;
    }

    /**
     * Returns the username of a User object.
     * @param user User to get the username from.
     * @return Username of the user.
     */
    @Override
    protected String getId(User user) {
        return user.getUsername();
    }

    /**
     * Converts a user's ID from String to username.
     * In this case, does nothing.
     */
    @Override
    protected String IDfromString(String str) {
        return str;
    }

    /**
     * Gets the path for a user's JSON file.
     * @param id username of the user.
     * @return Path of the user's file.
     */
    @Override
    protected String getFilePath(String id) {
        return basePath + "/" + id + ".json";
    }

    /**
     * Gets the path for te directory where users are stored.
     * @return Path where all users are stored.
     */
    @Override
    protected String getAllPath() {
        return basePath;
    }

    /**
     * Checks if necessary subfolders exist. If not, creates them.
     * In this case, does nothing.
     */
    @Override
    protected void checkSubfolders() {

    }
}
