package domain.model;

import java.util.*;


/**
 * Represents the state of a match or session on an Hidato game.
 * Allows to track the time spent and to pause and unpause the state of the session.
 */
public class GameSession {

    /** UUID of the game session */
    private UUID idGS;
    /** Number of hints used during the match */
    private int hintsUsed;
    /** Time passed since the game firstly started */
    private long elapsedTime;

    /** Base Hidato the user is playing */
    private UUID createdFrom;
    /** Current state of the hidato */
    private Hidato currentBoard;
    /** User who started the game */
    private String author;

    /**
     * Constructor for a new GameSession
     * @param  id UUID of the GameSession
     * @param base Hidato that the user wants to play
     * @param aut User who starts the GameSession
     */
    public GameSession(UUID id, UUID base, String aut){
        idGS = id;
        hintsUsed = 0;
        elapsedTime = 0;
        createdFrom = base;
        author = aut;
    }

    /**
     * Constructor for an already started GameSession
     * @param id UUID of the GameSession
     * @param base Hidato the GameSession was created from
     * @param board State the game was when paused
     * @param aut User who created the GameSession
     * @param et Time passed from the start of the game
     */
    public GameSession(UUID id, UUID base,Hidato board, String aut, long et){
        idGS = id;
        hintsUsed = 0;
        elapsedTime = et;
        createdFrom = base;
        currentBoard = board;
        author = aut;
    }

    /**
     * Getter for the id of the match
     * @return UUID of the Game Session
     */
    public UUID getIdGS() {
        return idGS;
    }


    /**
     * Setter for the UUID of a GameSession.
     * @param id UUID of the GameSession.
     */
    public void setIdGS(UUID id) {
        idGS = id;
    }

    /**
     * Returns the number of hints used by the player during the game
     * @return Number of hints used
     */
    public int getHintsUsed(){
        return hintsUsed;
    }

    /**
     * Sets the number of hints used.
     * @param h number of hints used.
     */
    public void setHintsUsed(int h){
        hintsUsed = h;
    }

    /**
     * Returns the Hidato the game was created from
     * @return The UUID of the base of the game session
     */
    public UUID getCreatedFrom(){
        return createdFrom;
    }

    /**
     * Sets the hidato te game was created from.
     * @param createdFrom UUID of the base of the GameSession
     */
    public void setCreatedFrom(UUID createdFrom){
        this.createdFrom = createdFrom;
    }

    /**
     * Sets the current state of the hidato in the game
     * @param bd The current board for the game session
     */
    public void setCurrentBoard(Hidato bd){
        currentBoard = bd;
    }

    /**
     * Returns the current state of the hidato in the game
     * @return The board of the game session
     */
    public Hidato getCurrentBoard(){
        return currentBoard;
    }

    /**
     * Returns the User who started playing the game session
     * @return Username of the author of the game session
     */
    public String getAuthor(){
        return author;
    }

    /**
     * Sets the author of the GameSession, who started playing the game session.
     * @param a Username of the author.
     */
    public void setAuthor(String a){
        author = a;
    }

    /**
     * Starts the game session
     */
    public void startGame() {
        elapsedTime = 0;
    }

    /**
     * Returns the time passed since the first start of the game session
     * @return Elapsed time for the game session
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Sets the elapsed time of the game session.
     * @param sec elapsed time of the GameSession.
     */
    public void setElapsedTime(long sec) {
        if (sec < 0) {
            throw new IllegalArgumentException("Time cannot be negative");
        }
        this.elapsedTime = sec;
    }
}