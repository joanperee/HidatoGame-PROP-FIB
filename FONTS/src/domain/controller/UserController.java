package domain.controller;

import java.util.*;
import domain.model.User;

/**
 * Controller responsible for user-related operations. It acts as a bridge
 * between
 * domain and presentation layers for authentication and statistics (ranking).
 * 
 * @author Joan Pere
 */
public class UserController {
    /** Unique singleton instance of UserController */
    private static UserController instance;

    /**
     * A map containing all usernames (key) and the User object.
     * This way lookups for specific users will be O(1), and operations
     * such as ranking computation will be faster.
     */
    private HashMap<String, User> users_;

    /** The user who is currently playing the game. */
    private User current_user_;

    /** Default constructor for the UserController class. */
    private UserController() {
        this.users_ = new HashMap<>();
        this.current_user_ = null;
    }

    /**
     * Method for the other controllers to get the single instance.
     * 
     * @return The instance of UserController
     */
    public static UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }

    /**
     * Handles user authentication. Checks if the user exists
     * and if the password is correct.
     * 
     * @param uName The username to be verified.
     * @param psswd The password to be verified.
     * @return true if the user logged in correctly, false otherwise.
     */
    public boolean login(String uName, String psswd) {
        User u = users_.get(uName);
        if (u != null && u.isCorrectPsswd(psswd)) {
            this.current_user_ = u;
            return true;
        }
        return false;
    }

    /**
     * Handles user registration. Checks if the user already exists
     * and if the password is valid (that is, psswd1 = psswd2)
     * 
     * @param uName  The username to be registered.
     * @param psswd1 The password given by the new username.
     * @param psswd2 The password confirmation.
     * @return true if a new user was registered successfully.
     */
    public boolean register(String uName, String psswd1, String psswd2) {
        if (uName == null || psswd1 == null ||
                psswd1.trim().isEmpty() || uName.trim().isEmpty()) {
            return false;
        }
        if (uName.equals("SYSTEM_CPU"))
            return false;
        if (!users_.containsKey(uName) && psswd1.equals(psswd2)) {
            User u = new User(uName, psswd1);
            users_.put(uName, u);
            return true;
        }
        return false;
    }

    /**
     * Computes the ranking using users_ hashmap and sorting it.
     * 
     * @return The list of the 100 users that have the best score.
     */
    public List<String> getRanking() {
        List<User> users = new ArrayList<>(users_.values());
        users.sort((u1, u2) -> Integer.compare(u2.getScore(), u1.getScore()));

        int end = Math.min(100, users.size());
        List<User> top100 = users.subList(0, end);

        List<String> ranking = new ArrayList<>();
        for (User u : top100) {
            ranking.add(u.getUsername() + "," + u.getScore());
        }
        return ranking;
    }

    /**
     * Computes the ranking using the list of users passed as a parameter
     * 
     * @param users The list of users from which we have to extract the ranking
     * @return
     */
    public List<String> getRanking(List<User> users) {
        users.sort((u1, u2) -> Integer.compare(u2.getScore(), u1.getScore()));

        int end = Math.min(100, users.size());
        List<User> top100 = users.subList(0, end);

        List<String> ranking = new ArrayList<>();
        for (User u : top100) {
            ranking.add(u.getUsername() + "," + u.getScore());
        }
        return ranking;
    }

    /** @return The list of all users registered in our game. */
    public List<User> getUsers() {
        return new ArrayList<>(users_.values());
    }

    /** @return The current user logged in. */
    public String getCurrentUser() {
        return this.current_user_.getUsername();
    }

    /**
     * The current user logs out of the system.
     * That is, in the presentation layer will return to the log in page.
     */
    public void logout() {
        this.current_user_ = null;
    }

    /**
     * Adds the score to the current user after completing
     * a valid hidato.
     * 
     * @param score
     */
    public void addScore(int score) {
        this.current_user_.addScore(score);
    }
}