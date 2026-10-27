package persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import domain.model.GameSession;
import domain.model.Hidato;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Extends the JSON files' manager for GameSession objects.
 */
public class JsonGameSessionManager extends JsonManager<GameSession, UUID> {

    /**
     * Instance for making the JSON GameSession manager singleton
     */
    private static JsonGameSessionManager instance;

    /**
     * Constructor for the GameSession manager. Sets the path, class type and ID
     * type.
     */
    private JsonGameSessionManager() {
        super("data/gamesession", GameSession.class, UUID.class);
    }

    /**
     * Method to get the singleton instance of JsonGameSessionManager
     *
     * @return The singleton instance of JsonGameSessionManager
     */
    public static JsonGameSessionManager getInstance() {
        if (instance == null) {
            instance = new JsonGameSessionManager();
        }
        return instance;
    }

    /**
     * Returns the UUID of a GameSession object.
     *
     * @param obj game session to get the id from.
     * @return UUID of the game session.
     */
    @Override
    protected UUID getId(GameSession obj) {
        return obj.getIdGS();
    }

    /**
     * Converts a game session's ID from String to UUID.
     *
     * @param str ID in string format.
     * @return UUID of the game session.
     */
    @Override
    protected UUID IDfromString(String str) {
        return UUID.fromString(str);
    }

    /**
     * Gets the path for a GameSession's JSON file.
     *
     * @param id UUID of the game session.
     * @return Path of the game session's file.
     */
    @Override
    protected String getFilePath(UUID id) {
        return basePath + "/all/" + id + ".json";
    }

    /**
     * Gets the path for te directory where game sessions are stored.
     *
     * @return Path where all game sessions are stored.
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
     * Implements the save method for the JsonGameSessionManager. Stores a given
     * game session in the game sessions' folder by direct serialization of the
     * object's attributes. Except for the current board Hidato, which has to be
     * converted into String format.
     * 
     * @param gs the game session to be stored.
     */
    @Override
    public void save(GameSession gs) {
        try {
            ObjectNode node = mapper.createObjectNode();

            node.put("idGS", gs.getIdGS().toString());
            node.put("createdFrom", gs.getCreatedFrom().toString());
            node.put("author", gs.getAuthor());

            node.put("hintsUsed", gs.getHintsUsed());
            node.put("elapsedTime", gs.getElapsedTime());

            if (gs.getCurrentBoard() == null) {
                node.putNull("board");
            } else {
                node.put("board", Hidato.stringFromHidato(gs.getCurrentBoard()));
            }

            File f = new File(getFilePath(gs.getIdGS()));
            f.getParentFile().mkdirs();

            mapper.writeValue(f, node);

        } catch (IOException e) {
            throw new RuntimeException("Error saving: " + gs.getIdGS(), e);
        }
    }

    /**
     * Implements the load method for the JsonGameSessionManager. Loads a specified
     * game session from memory by direct serialization of the object's attributes.
     * Except for the current board hidato which has to be converted from String
     * format.
     * 
     * @param id UUID of the GameSession to load.
     * @return The loaded GameSession.
     */
    @Override
    public GameSession load(UUID id) {
        try {
            File f = new File(getFilePath(id));

            JsonNode node = mapper.readTree(f);

            UUID idGS = UUID.fromString(node.get("idGS").asText());
            UUID createdFrom = UUID.fromString(node.get("createdFrom").asText());
            String author = node.get("author").asText();
            GameSession gs = new GameSession(idGS, createdFrom, author);

            gs.setHintsUsed(node.get("hintsUsed").asInt(0));
            gs.setElapsedTime(node.get("elapsedTime").asLong(0));

            JsonNode boardNode = node.get("board");
            if (boardNode == null || boardNode.isNull())
                gs.setCurrentBoard(null);
            else {
                Hidato h = Hidato.loadFromString(boardNode.asText());
                gs.setCurrentBoard(h);
            }

            return gs;

        } catch (IOException e) {
            throw new RuntimeException("Error loading: " + id, e);
        }
    }
}
