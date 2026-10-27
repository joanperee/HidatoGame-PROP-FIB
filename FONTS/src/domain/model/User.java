package domain.model;

import java.util.*;
import java.util.List;

/**
 * Represents a user with their credentials and gameSessions.
 * @author Joan Pere
 */
public class User {
    /** User's name, which identifies them (must be unique). */
    private String username;
    /** User's password for authentication. */
    private String psswd;
    /** Total accumulated score of the user. */
    private int score;
    /** List of game sessions completed by the user. */
    private ArrayList<UUID> gameSessions;
    
    /** Assigns the paramethers that are common to all constructors. */
    private void initDefaults() {
        score = 0;
        gameSessions = new ArrayList<>();
    }

    /**
     * Default constructor for a guest user. 
     */
    public User() {
        username = "Guest";
        psswd = "";
        initDefaults();
    }

    /**
     * Constructor for a real user. 
     * @param uName The user's unique identifying name.
     * @param psswd The password chosen by the user.
     * @throws NullPointerException if any of the paramethers is null.
     * @throws IllegalArgumentException if any of the paramethers is an empty string.
     */
    public User(String uName, String psswd) {
        if (uName == null || psswd == null) {
            throw new NullPointerException("Username and password can't be null.");
        }
        if (uName.trim().isEmpty() || psswd.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password must be valid strings");
        }
        this.username = uName;
        this.psswd = psswd;
        initDefaults();
    }

    /**
     * Constructor for the system user.
     * @param uName System name.
     * @param isSystem Boolean that assigns an internal password if true.
     */
    public User(String uName, boolean isSystem) {
        this.username = uName;
        if (!isSystem) throw new IllegalArgumentException("This constructor must have the boolean isSystem true");
        this.psswd = "INTERNAL_CONTROL_KEY";
        initDefaults();
    }

    /**
     * Checks if the provided password matches the user's password.
     * @param psswd The password to verify.
     * @return True if the passwords match, false otherwise.
     */
    public boolean isCorrectPsswd(String psswd) {
        if (psswd == null) return false;
        if (this.username.equals("SYSTEM_CPU")) return false;   // protect users from logging as system
        return this.psswd.equals(psswd);
    }

    /**
     * Adds points to the user's total score.
     * @param points Points to add, where points > 0.
     * @throws IllegalArgumentException if points paramether is negative or 0
     */
    public void addScore(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("The added points must be strictly positive");
        }
        this.score += points;
    }

    /**
     * Setter for the user's score points.
     * @param points The user's score.
     */
    public void setScore(int points) {
        this.score = points;
    }

    /**
     * Resets the score to 0.
     */
    public void resetScore() {
        this.score = 0;
    }

    /**
     * Records a new game session and updates the user's score.
     * @param s The game session to add.
     * @throws NullPointerException if GameSession s is null
     */
    public void addGameSession(UUID s) {
        if (s == null) {
            throw new NullPointerException("Can not add a null GameSession");
        }
        this.gameSessions.add(s);
    }
    
    /** @return The name of the instantiated user object. */
    public String getUsername() {
        return this.username;
    }

    /** @return The user's current total score. */
    public int getScore() {
        return this.score;
    }

    public void setGameSession(List<UUID> gameSessions) {
        this.gameSessions =  new ArrayList<>(gameSessions);
    }

    /** @return The list of the user's game sessions. */
    public List<UUID> getGameSessions() {
        return new ArrayList<>(this.gameSessions);
    }

    /** @param newpsswd The user's new password. 
     * @throws NullPointerException if newpsswd is null
     * @throws IllegalArgumentException if newpsswd is an empty string
    */
    public void setPsswd(String newpsswd) {
        if (newpsswd == null) throw new NullPointerException("The new password can not be null");
        if (newpsswd.trim().isEmpty()) throw new IllegalArgumentException("The new password can not be an empty string");
        this.psswd = newpsswd;
    }

    /**
     * Getter for the user's password.
     * @return The user's password.
     */
    public String getPsswd() {
        return  this.psswd;
    }
}