package domain.controller;

import domain.enumeration.*;
import domain.model.*;
import java.util.*;
//  import domain.controller.*;
// import presentation.controller.*;
import persistence.*;

/**
 * Central domain controller that manages the business logic of the application.
 * Implements the Singleton pattern to ensure a unique instance.
 * Coordinates operations related to Hidatos, users, and game sessions.
 */
public class DomainController {
    /**
     * Controller instance for Hidato logic.
     */
    private HidatoController hidatoController = HidatoController.getInstance();

    /**
     * Controller instance for user logic.
     */
    private UserController userController = UserController.getInstance();

    /**
     * Persistence manager for Hidatos.
     */
    private DataManager<Hidato, UUID> dataHidatoManager;

    /**
     * Persistence manager for game sessions.
     */
    private DataManager<GameSession, UUID> dataGameSessionManager;

    /**
     * Persistence manager for users.
     */
    private DataManager<User, String> dataUserManager;

    /**
     * Persistence list manager for Hidatos.
     */
    private DataListManager<Hidato, UUID> dataListHidatoManager;

    /**
     * Persistence list manager for game sessions.
     */
    private DataListManager<GameSession, UUID> dataListGameSessionManager;

    /**
     * Current match (gamesession).
     */
    private GameSession currentSession_;

    /**
     * Private constructor to implement the Singleton pattern.
     */
    private DomainController() {
    }

    /**
     * Inner class holder for thread-safe Singleton implementation.
     */
    private static class SingletonHolder {
        private static final DomainController instance = new DomainController();
    }

    /**
     * Gets the DomainController instance and initializes the persistence managers.
     * Can only be called once; subsequent calls throw an exception.
     *
     * @param hidatoManager          Persistence manager for Hidatos
     * @param gameSessionManager     Persistence manager for game sessions
     * @param userManager            Persistence manager for users
     * @param hidatoListManager      Persistence list manager for Hidatos
     * @param gameSessionListManager Persistence list manager for game sessions
     * @return The initialized singleton instance of DomainController
     * @throws IllegalStateException if persistence managers have already been
     *                               configured
     */
    public static DomainController getInstance(DataManager<Hidato, UUID> hidatoManager,
            DataManager<GameSession, UUID> gameSessionManager,
            DataManager<User, String> userManager, DataListManager<Hidato, UUID> hidatoListManager,
            DataListManager<GameSession, UUID> gameSessionListManager) {
        DomainController instance = SingletonHolder.instance;
        synchronized (instance) {
            if (instance.dataHidatoManager != null || instance.dataGameSessionManager != null
                    || instance.dataUserManager != null || instance.dataListHidatoManager != null
                    || instance.dataListGameSessionManager != null) {
                throw new IllegalStateException("DataManager and DataListManager have already been set.");
            }
            instance.dataHidatoManager = hidatoManager;
            instance.dataGameSessionManager = gameSessionManager;
            instance.dataUserManager = userManager;
            instance.dataListHidatoManager = hidatoListManager;
            instance.dataListGameSessionManager = gameSessionListManager;
        }
        return instance;
    }

    /**
     * Gets the already initialized singleton instance of DomainController.
     * Must have been previously initialized via getInstance with parameters.
     *
     * @return The singleton instance of DomainController
     * @throws IllegalStateException if persistence managers have not been
     *                               configured
     */
    public static DomainController getInstance() {
        DomainController instance = SingletonHolder.instance;
        if (instance.dataHidatoManager == null || instance.dataGameSessionManager == null
                || instance.dataUserManager == null || instance.dataListHidatoManager == null
                || instance.dataListGameSessionManager == null) {
            throw new IllegalStateException(
                    "DataManager and DataListManager must be set before accessing the DomainController instance.");
        }
        return DomainController.SingletonHolder.instance;
    }

    /**
     * Converts an integer value to its corresponding Adjacency enumeration.
     * Maps the values based on the topology and desired adjacency type.
     *
     * @param i Integer value representing the topology and adjacency type:
     *          <p>
     *          <ul>
     *          <li>0 = Squares + C</li>
     *          <li>1 = Squares + CA</li>
     *          <li>2 = Triangles + C</li>
     *          <li>3 = Triangles + CA</li>
     *          <li>4 = Hexagons (CA only)</li>
     *          </ul>
     *          </p>
     * @return The corresponding Adjacency enumeration, or null if the value is
     *         invalid
     */
    private Adjacency int2Adjacency(int i) {
        switch (i) {
            case 0:
                return Adjacency.C; // Squares + C
            case 1:
                return Adjacency.CA; // Squares +CA
            case 2:
                return Adjacency.C; // Triangles + C
            case 3:
                return Adjacency.CA; // Triangles +CA
            case 4:
                return Adjacency.CA; // Hexagons (only accept CA)
            default:
                return null;
        }
    }

    /**
     * Converts an integer value to its corresponding topology string.
     * Maps numeric indices to their topology representations (Q = Squares, T =
     * Triangles, H = Hexagons).
     *
     * @param i Integer value representing the topology index:
     *          <p>
     *          <ul>
     *          <li>0, 1 = "Q" (Squares)</li>
     *          <li>2, 3 = "T" (Triangles)</li>
     *          <li>4 = "H" (Hexagons)</li>
     *          </ul>
     *          </p>
     * @return The corresponding topology string ("Q", "T", "H"), or null if the
     *         value is invalid
     */
    private String int2Topology(int i) {
        switch (i) {
            case 0:
                return "Q";
            case 1:
                return "Q";
            case 2:
                return "T";
            case 3:
                return "T";
            case 4:
                return "H";
            default:
                return null;
        }
    }

    /**
     * Generates a new Hidato randomly and stores it.
     * Randomly selects a topology, adjacency type and number of cells.
     * The number of cells ranges from 3 to 200.
     *
     * @param dif The desired difficulty level: "EASY", "MEDIUM", or "HARD"
     * @return A string representing the generated Hidato with its topology and
     *         content
     */
    public String generateHidato(String dif) {
        Random rand = new Random();
        int r = rand.nextInt(5);
        String topology = int2Topology(r);
        Adjacency ad = int2Adjacency(r);
        int nC = rand.nextInt(197) + 3; // nC has to go from 3..200
        Difficulty diff = Difficulty.HARD;
        if (dif.equals("EASY"))
            diff = Difficulty.EASY;
        else if (dif.equals("MEDIUM"))
            diff = Difficulty.MEDIUM;
        /*
         * String hidato = hidatoController.generateHidato(topology, ad, nC, diff);
         * if (!saveHidato(hidato)) return null;
         * return hidato;
         * 
         */
        return hidatoController.generateHidato(topology, ad, nC, diff);
    }

    /**
     * Generates a new Hidato with full control over topology, adjacency, difficulty
     * and cell count.
     *
     * @param topology The board shape: "Squares", "Triangles", or "Hexagons"
     * @param Adja     The adjacency type: "Sides" or "Sides + Vertices"
     * @param dif      The desired difficulty level: "EASY", "MEDIUM", or "HARD"
     * @param nC       The requested number of cells (between 3 and 200)
     * @return A string representing the generated Hidato, or null if generation
     *         failed
     */
    public String generateHidato(String topology, String Adja, String dif, int nC) {
        Difficulty diff = Difficulty.HARD;
        if (dif.equals("EASY"))
            diff = Difficulty.EASY;
        else if (dif.equals("MEDIUM"))
            diff = Difficulty.MEDIUM;
        String top = "Q";
        if (topology.equals("Triangles"))
            top = "T";
        else if (topology.equals("Hexagons"))
            top = "H";
        Adjacency adj = Adjacency.CA;
        if (Adja.equals("Sides"))
            adj = Adjacency.C;
        return hidatoController.generateHidato(top, adj, nC, diff);
    }

    /**
     * Creates a Hidato object from its string representation.
     * Parses the string to determine the topology (Triangle, Square, or Hexagon)
     * and creates the corresponding instance.
     *
     * @param hidato String containing the Hidato representation with its topology
     * @return Hidato object created according to its topology, or null if it cannot
     *         be created
     */
    private Hidato createHidato(String hidato) {
        String[] hidatoArray = hidato.split("\n");
        String[] firstLine = hidatoArray[0].split(",");
        String type = firstLine[0];
        Hidato h = null;
        if (type.equals("T"))
            h = new Triangle(hidato);
        else if (type.equals("Q"))
            h = new Square(hidato);
        else
            h = new Hexagon(hidato);
        h.setIdH(UUID.randomUUID());
        return h;
    }

    /**
     * Saves a Hidato to the persistence layer.
     * Extracts the Hidato content from its string representation,
     * creates the corresponding object, and persists it.
     *
     * @param hidato String containing the Hidato representation
     * @return true if the Hidato was saved successfully, false otherwise
     */
    private boolean saveHidato(String hidato) {
        if (hidato == null)
            return false;
        Hidato h = createHidato(hidato);
        if (h == null)
            return false;
        try {
            dataHidatoManager.save(h);
            dataListHidatoManager.addTo("all", h.getIdH());
            return true;
        } catch (Exception e) {
            System.out.print("Could not save the hidato " + e.getMessage());
        }
        return false;
    }

    /**
     * Converts a Hidato into a solvable version by applying specified difficulty
     * criteria.
     * Stores the solvable Hidato in the persistence layer.
     * Hides cells according to the difficulty level and requested number of cells.
     *
     * @param hidato String containing the complete Hidato
     * @param dif    Difficulty level: "EASY", "MEDIUM", or "HARD"
     * @param nC     Number of cells of the hidato
     * @return String representing the solvable Hidato, or null if it could not be
     *         created
     */
    public String makeSolvable(String hidato, String dif, int nC) {
        String solvable = hidatoController.makeSolvable(hidato, dif, nC);
        if (!saveHidato(solvable))
            return null;
        return solvable;
    }

    /**
     * Performs user login by verifying credentials.
     * Validates that the username and password are correct.
     *
     * @param username The username
     * @param password The user's password
     * @return true if login is successful, false otherwise
     */
    public boolean login(String username, String password) {

        boolean result = userController.login(username, password);
        if (!result) {
            User u = (User) dataUserManager.load(username);
            if (u != null) {
                // If the user exists in persistence, check the stored password first:
                if (u.isCorrectPsswd(password)) {
                    // Register the loaded user into the in-memory controller (so users_ map
                    // contains it)
                    userController.register(username, password, password);
                    // Now perform login to set current_user_
                    return userController.login(username, password);
                } else {
                    // password mismatch with persisted user
                    return false;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Registers a new user in the system.
     * Verifies that the passwords match and creates a new user account.
     *
     * @param username The username to register
     * @param password The first password
     * @param p2       The second password (must match password)
     * @return true if registration is successful, false otherwise
     */
    public boolean register(String username, String password, String p2) {

        User u = (User) dataUserManager.load(username);
        if (u == null) {
            // First time this user has been seen
            boolean result = userController.register(username, password, p2);
            if (result) {
                dataUserManager.save(new User(username, password));
            }
            return true;
        }
        // If the user is already in the database then check that the credentials are
        // correct add it to the current session if not already in it
        // and inform the user that it is already registered
        if (u.isCorrectPsswd(password) && u.isCorrectPsswd(p2)) {
            userController.register(username, password, p2);
        }
        return false;
    }

    /**
     * Gets the name of the currently authenticated user.
     *
     * @return The name of the current user, or null if no user is authenticated
     */
    public String getCurrentUser() {
        return userController.getCurrentUser();
    }

    /**
     * Verifies whether a Hidato is a valid solution.
     * Checks that there is a correct path from the cell containing the value 1 to
     * the cell containing the biggest value.
     *
     * @param hidato String containing the Hidato representation to verify
     * @return true if the Hidato is a valid solution, false otherwise
     */
    public boolean isSolution(String hidato) {
        Hidato h = createHidato(hidato);
        if (h == null)
            return false; // error control
        return hidatoController.isSolution(h);
    }

    public List<String> getRanking() {
        List<String> list = dataUserManager.getAll();
        List<User> uList = new ArrayList<>();
        for (String s : list) {
            uList.add(dataUserManager.load(s));
        }
        return userController.getRanking(uList);
    }

    /**
     * Creates a new GameSession for the current user and the given hidato.
     * Called when the user starts solving a new hidato (casual or ranked).
     * * @param hidatoStr The string representation of the hidato being played.
     */
    public void startGameSession(String hidatoStr) {
        Hidato h = createHidato(hidatoStr);
        
        try {
            if (dataHidatoManager != null) {
                dataHidatoManager.save(h);
            }
        } catch (Exception e) {
            System.err.println("Could not save base hidato: " + e.getMessage());
        }
        
        String author = userController.getCurrentUser();
        currentSession_ = new GameSession(UUID.randomUUID(), h.getIdH(), h, author, 0);
        currentSession_.setCurrentBoard(h);
    }

    /**
     * Retrieves the original starting board string for a given game session.
     * This is used to differentiate between given numbers and user-placed numbers.
     *
     * @param sessionId The UUID string of the game session.
     * @return The string representation of the original board, or null if not found.
     */
    public String loadOriginalBoardFromSession(String sessionId) {
        try {
            UUID id = UUID.fromString(sessionId);
            GameSession gs = (GameSession) dataGameSessionManager.load(id);
            if (gs == null) return null;
            
            Hidato original = (Hidato) dataHidatoManager.load(gs.getCreatedFrom());
            if (original == null) return null;
            
            return Hidato.stringFromHidato(original);
        } catch (Exception e) {
            System.err.println("Error loading original board: " + e.getMessage());
            return null;
        }
    }

    /**
     * Finishes the current GameSession and updates the user's score.
     * Called when the user completes a hidato correctly.
     * * @param score The score obtained in this session.
     */
    public void endGameSession(int score) {
        if (currentSession_ == null)
            return;
        userController.addScore(score);
        User u = dataUserManager.load(currentSession_.getAuthor());
        u.addScore(score);
        dataUserManager.save(u);
        currentSession_ = null;
    }

    /**
     * Adds the given score to the currently logged-in user's total score.
     * 
     * @param score
     */
    public void addScore(int score) {
        userController.addScore(score);
    }

    /**
     * Provides a hint for the current game session by solving a copy
     * of the current board and picking a random cell from the solved result.
     * 
     * @param boardStr
     * @return An int array {row, col, value} for the cell as a hint or null
     *         if it is not possible to provide it (f. i. already solved).
     */
    public int[] getHint(String boardStr) {
        if (currentSession_ == null)
            return null;

        // Solve the original board (ignores wrong user input)
        Hidato copy = currentSession_.getCurrentBoard().copyHidato();
        GameSession solved = hidatoController.solve(copy);
        if (solved == null)
            return null;

        // Use current board only to know which cells are still empty
        Hidato current = createHidato(boardStr);
        if (current == null)
            return null;

        int[][] currentGrid = current.getRepresentation();
        int[][] solvedGrid = solved.getCurrentBoard().getRepresentation();

        // Find all empty cells (value 0 = ?, -1 = hole/wall)
        java.util.List<int[]> emptyCells = new java.util.ArrayList<>();
        for (int r = 0; r < currentGrid.length; r++) {
            for (int c = 0; c < currentGrid[r].length; c++) {
                if (currentGrid[r][c] == 0) {
                    emptyCells.add(new int[] { r, c, solvedGrid[r][c] });
                }
            }
        }
        if (emptyCells.isEmpty())
            return null;

        java.util.Collections.shuffle(emptyCells);
        return emptyCells.get(0);
    }

    /**
     * Retrieves the fully solved board string using the 2D representation array.
     * Maps the solved integers back onto the original string layout.
     * * @param boardStr The string representation of the current board.
     */
    public String getSolvedBoard(String boardStr) {
        Hidato current = createHidato(boardStr);
        if (current == null)
            return null;

        // Solve a copy so the original board is not modified
        Hidato copy = current.copyHidato();
        GameSession solved = hidatoController.solve(copy);
        if (solved == null)
            return null;

        int[][] solvedGrid = solved.getCurrentBoard().getRepresentation();

        String[] lines = boardStr.trim().split("\n");
        StringBuilder sb = new StringBuilder();

        // Append header (e.g., "Q,C,3,3")
        sb.append(lines[0]).append("\n");

        for (int r = 1; r < lines.length; r++) {
            String[] cells = lines[r].trim().split(",", -1);
            for (int c = 0; c < cells.length; c++) {
                String valStr = cells[c].trim();

                // Keep holes and bounding tiles exactly as they were
                if (valStr.equals("*") || valStr.equals("#")) {
                    sb.append(valStr);
                } else {
                    // Replace playable cells with the solved integer
                    sb.append(solvedGrid[r - 1][c]);
                }

                if (c < cells.length - 1) {
                    sb.append(",");
                }
            }
            if (r < lines.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Triggered by Save and Exit button, saves all the information for
     * the current match, such as:
     * 
     * @param currentBoard Current hidato state.
     * @param elapsedTime  Time elapsed until now.
     * @param hintsUsed    Hints used until now.
     */
    public void saveGameSession(String currentBoard, long elapsedTime, int hintsUsed) {
        if (currentSession_ == null)
            return;
        currentSession_.setCurrentBoard(createHidato(currentBoard));
        currentSession_.setElapsedTime(elapsedTime);
        currentSession_.setHintsUsed(hintsUsed);
        dataGameSessionManager.save(currentSession_);
        String author = currentSession_.getAuthor();
        User u = (User) dataUserManager.load(author);
        if (u != null) {
            if (!u.getGameSessions().contains(currentSession_.getIdGS())) {
                u.addGameSession(currentSession_.getIdGS());
                dataUserManager.save(u);
            }
        }
    }

    /**
     * Returns the list of saved game session IDs for the given user.
     *
     * @param username The username whose saved sessions are to be retrieved.
     * @return A list of UUID strings, one per saved session. Returns an empty list
     *         if the user
     *         does not exist or has no saved sessions.
     */
    public List<String> getSavedSessions(String username) {
        User u = (User) dataUserManager.load(username);
        List<String> result = new java.util.ArrayList<>();
        if (u == null)
            return result;
        for (UUID id : u.getGameSessions())
            result.add(id.toString());
        return result;
    }

    /**
     * Loads a previously saved game session and sets it as the current active
     * session.
     *
     * @param sessionId The UUID string of the game session to load.
     * @return The string representation of the session's current board, or null if
     *         the session
     *         cannot be found or the board is absent.
     */
    public String loadGameSession(String sessionId) {
        try {
            UUID id = UUID.fromString(sessionId);
            GameSession gs = (GameSession) dataGameSessionManager.load(id);
            if (gs == null)
                return null;
            currentSession_ = gs;
            if (gs.getCurrentBoard() == null)
                return null;
            return Hidato.stringFromHidato(gs.getCurrentBoard());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the elapsed time in seconds of the current active game session.
     *
     * @return The elapsed time in seconds, or 0 if there is no active session.
     */
    public long getCurrentSessionElapsedTime() {
        return currentSession_ == null ? 0 : currentSession_.getElapsedTime();
    }

    /**
     * Returns the number of hints used in the current active game session.
     *
     * @return The hint count, or 0 if there is no active session.
     */
    public int getCurrentSessionHintsUsed() {
        return currentSession_ == null ? 0 : currentSession_.getHintsUsed();
    }

    /**
     * Returns all custom Hidato list names belonging to the current user, excluding
     * the built-in "all" list.
     *
     * @return A list of user-created list names. Returns an empty list if none
     *         exist.
     */
    public List<String> getUserHidatoLists() {
        return dataListHidatoManager.getLists()
                .stream()
                .filter(name -> !name.equals("all"))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Returns a map of UUID string to human-readable label for every Hidato in the
     * given list.
     * The label format is: "Topology · Rows x Cols (shortUUID...)"
     *
     * @param listName The name of the Hidato list to retrieve labels for.
     * @return A map where each key is the Hidato UUID string and each value is its
     *         readable label.
     *         Returns an empty map if the list does not exist or contains no valid
     *         Hidatos.
     */
    public Map<String, String> getHidatoLabels(String listName) {
        Map<String, String> labels = new java.util.LinkedHashMap<>();
        List<UUID> ids = dataListHidatoManager.getList(listName);
        if (ids == null)
            return labels;
        for (UUID id : ids) {
            Hidato h = (Hidato) dataHidatoManager.load(id);
            if (h == null)
                continue;
            String topology = (h instanceof Square) ? "Squares"
                    : (h instanceof Triangle) ? "Triangles"
                            : "Hexagons";
            String label = topology + " · "
                    + h.getRows() + "x" + h.getCols()
                    + "  (" + id.toString().substring(0, 8) + "...)";
            labels.put(id.toString(), label);
        }
        return labels;
    }

    /**
     * Creates a new empty Hidato list with the given name in the persistence layer.
     *
     * @param name The name to assign to the new list.
     */
    public void createHidatoList(String name) {
        dataListHidatoManager.addTo(name, null);
    }

    /**
     * Deletes the Hidato list with the given name from the persistence layer.
     * The individual Hidato objects are not deleted; only the list entry is
     * removed.
     *
     * @param name The name of the list to delete.
     */
    public void deleteHidatoList(String name) {
        dataListHidatoManager.deleteList(name);
    }

    /**
     * Adds the Hidato identified by the given UUID string to the specified list.
     *
     * @param hidatoId The UUID string of the Hidato to add.
     * @param listName The name of the target list.
     */
    public void addHidatoToList(String hidatoId, String listName) {
        dataListHidatoManager.addTo(listName, UUID.fromString(hidatoId));
    }

    /**
     * Removes the Hidato identified by the given UUID string from the specified
     * list.
     * The Hidato object itself is not deleted from persistence.
     *
     * @param hidatoId The UUID string of the Hidato to remove.
     * @param listName The name of the list to remove it from.
     */
    public void removeHidatoFromList(String hidatoId, String listName) {
        dataListHidatoManager.removeFrom(listName, UUID.fromString(hidatoId));
    }

    /**
     * Loads a Hidato from persistence by its UUID string and returns its CSV string
     * representation.
     *
     * @param hidatoId The UUID string of the Hidato to load.
     * @return The string representation of the Hidato, or null if the UUID is
     *         invalid or the
     *         Hidato cannot be found.
     */
    public String loadHidatoById(String hidatoId) {
        try {
            UUID id = UUID.fromString(hidatoId);
            Hidato h = (Hidato) dataHidatoManager.load(id);
            if (h == null)
                return null;
            return Hidato.stringFromHidato(h);
        } catch (Exception e) {
            return null;
        }
    }

}
