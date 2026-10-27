package presentation.controller;

import domain.controller.DomainController;
import presentation.classes.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Controller class for the presentation layer.
 */
public class PresentationController {
    /** Singleton instance of the controller. */
    private static PresentationController instance;
    private static DomainController domainController = DomainController.getInstance();

    // All static views are declared here so the controller
    // can open, close and manage them.
    private ViewLogin viewLogin;
    private ViewMenu menu;
    private ViewPlayCasual playCasualView;
    private ViewGenerateHidato generateHidato;
    private ViewRanking viewRanking;
    private ViewCreateHidato viewCreateHidato;
    private ViewConsultList viewConsultList;
    private ViewHidatoSolving currentSolvingView;

    /** Private constructor as Singleton requires. */
    private PresentationController() {
    }

    /**
     * Singleton public method to get the instance of this class.
     * * @return The unique instance of PresentationController.
     */
    public static PresentationController get_instance() {
        if (instance == null)
            instance = new PresentationController();
        return instance;
    }

    /**
     * Loads all the GUI views so that changes are smooth and fast.
     */
    public void loadGUI() {
        viewLogin = new ViewLogin(this);
        viewLogin.setVisible(true);

        // Optimització: El menú es carrega aquí de manera estàtica
        menu = new ViewMenu(this);
        menu.setVisible(false);

        playCasualView = new ViewPlayCasual(this);
        playCasualView.setVisible(false);

        generateHidato = new ViewGenerateHidato(this);
        generateHidato.setVisible(false);

        viewRanking = new ViewRanking(this);
        viewRanking.setVisible(false);

        viewCreateHidato = new ViewCreateHidato(this);
        viewCreateHidato.setVisible(false);

        viewConsultList = new ViewConsultList(this);
        viewConsultList.setVisible(false);
    }

    /**
     * Login logic connected to DomainController (domain layer).
     * 
     * @param username The username the user is trying to log in with.
     * @param password The password the user is trying to log in with.
     */
    public void login(String username, String password) {
        boolean success = domainController.login(username, password);

        if (success) {
            showMainMenu();
        } else {
            JOptionPane.showMessageDialog(viewLogin, "Error: User and/or Password incorrect", "Login error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Register logic connected to DomainController (domain layer).
     * 
     * @param username The user that is trying to register.
     * @param password The password the user is trying to register with.
     * @param p2       The password confirmed, which should be equal to password
     */
    public void register(String username, String password, String p2) {
        boolean success = domainController.register(username, password, p2);
        if (success) {
            JOptionPane.showMessageDialog(viewLogin, "User correctly registered!");
        } else {
            JOptionPane.showMessageDialog(viewLogin, "Error: User already exists or incorrect input", "Register error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows PlayCasual view. This function
     * is triggered to update the frame and connect it to the new JPanel,
     * just as we did inside the login function.
     */
    public void showPlayCasual() {
        JFrame mainframe = viewLogin;
        mainframe.getContentPane().removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        playCasualView.setVisible(true);
        mainframe.add(playCasualView, c);
        mainframe.setTitle("HIDATO GAME - CASUAL MODE");
        mainframe.revalidate();
        mainframe.repaint();
    }

    /**
     * This function is triggered when the Generate Hidato button
     * from the main Menu is pressed. It shows the GUI to let the
     * user freely create an Hidato with some constraints.
     */
    public void showGenerateHidato() {
        JFrame mainframe = viewLogin;
        mainframe.getContentPane().removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        generateHidato.setVisible(true);
        mainframe.add(generateHidato, c);
        mainframe.setTitle("GENERATING HIDATO");
        mainframe.revalidate();
        mainframe.repaint();
    }

    /**
     * After pressing a "Go Back" button inside any of the game modes
     * and options, this function returns to the main Menu.
     */
    public void showMainMenu() {
        JFrame mainframe = viewLogin;
        mainframe.getContentPane().removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        menu.setVisible(true);
        mainframe.add(menu, c);
        mainframe.setTitle("HIDATO GAME MENU - Welcome " + domainController.getCurrentUser());
        mainframe.revalidate();
        mainframe.repaint();
    }

    /**
     * Returns from the main Menu to the Login view, cleaning
     * the content Pane.
     */
    public void showLogin() {
        JFrame mainframe = viewLogin;
        mainframe.getContentPane().removeAll();
        mainframe.setLayout(new GridBagLayout());
        mainframe.setTitle("HIDATO GAME LOGIN");
        viewLogin.reinitComponents();
        mainframe.revalidate();
        mainframe.repaint();
    }

    /**
     * It shows the Pane containing the ranking.
     */
    public void showRanking() {
        List<String> entries = domainController.getRanking();
        viewRanking.loadRanking(entries);

        JFrame mainframe = viewLogin;
        mainframe.getContentPane().removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        viewRanking.setVisible(true);
        mainframe.add(viewRanking, c);
        mainframe.setTitle("HIDATO GAME - RANKING");
        mainframe.revalidate();
        mainframe.repaint();
    }

    /**
     * Shows the Create / Import Hidato view to allow users to paste or upload a
     * string.
     */
    public void showCreateHidato() {
        JFrame mainframe = viewLogin;
        mainframe.getContentPane().removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        viewCreateHidato.setVisible(true);
        mainframe.add(viewCreateHidato, c);
        mainframe.setTitle("HIDATO GAME - CREATE / IMPORT");
        mainframe.revalidate();
        mainframe.repaint();
    }

    /**
     * It communicates with the domain DomainController to
     * generate a valid hidato based on the paramether difficulty.
     * 
     * @param difficulty EASY, MEDIUM or HARD
     */
    public String generateHidato(String difficulty) {
        String hidato = domainController.generateHidato(difficulty);
        // The first line of hidato now has the value of nC, the rest of the string is
        // the Hidato solved itself.
        String[] hidatoArray = hidato.split("\n");
        int nC = Integer.parseInt(hidatoArray[0]);
        // Reconstruct the hidato without the first line
        hidato = String.join("\n", Arrays.copyOfRange(hidatoArray, 1, hidatoArray.length));
        // return hidato;
        return domainController.makeSolvable(hidato, difficulty, nC);
    }

    /**
     * It communicates with the domain DomainController to generate a valid hidato
     * based on the paramether difficulty.
     * 
     * @param topology "Squares", "Triangles", "Hexagons"
     * @param adja     "Sides", "Sides + Vertices"
     * @param dif      Difficulty: "EASY", "MEDIUM", "HARD"
     * @param nC       integer representing the number of cells of the hidato to
     *                 generate
     * @return
     */
    public String generateHidato(String topology, String adja, String dif, int nC) {
        String hidato = domainController.generateHidato(topology, adja, dif, nC);
        // The first line of hidato now has the value of nC, the rest of the string is
        // the Hidato solved itself.
        String[] hidatoArray = hidato.split("\n");
        nC = Integer.parseInt(hidatoArray[0]);
        // Reconstruct the hidato without the first line
        hidato = String.join("\n", Arrays.copyOfRange(hidatoArray, 1, hidatoArray.length));
        // return hidato;
        return domainController.makeSolvable(hidato, dif, nC);
    }

    /**
     * Inside PlayCasual or PlayRanked modes, when the user presses
     * the "Start Solving" button this function is triggered to
     * communicate with the domain DomainController and generate
     * an hidato. Also, the view is from the class ViewHidatoSolving.
     * 
     * @param difficulty The hidato is generated using a difficulty paramether.
     *                   This impacts the initial % of solved cells and its number.
     */
    public void startSolving(String difficulty, boolean isRanked) {
        // If we want an hidato that can be solved (not already solved) whe have to use
        // this function
        String hidato = generateHidato(difficulty);
        System.out.println(hidato);

        // In case generation fails, print a valid mock
        if (hidato == null) {
            hidato = "Q,C,3,3\n" +
                    "1,*,?\n" +
                    "*,?,?\n" +
                    "?,?,?";
        }

        domainController.startGameSession(hidato);

        JFrame mainframe = viewLogin;
        ViewHidatoSolving paintHidato = new ViewHidatoSolving(this, hidato, isRanked, difficulty, true);

        this.currentSolvingView = paintHidato;

        mainframe.getContentPane().removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        mainframe.add(paintHidato, c);
        if (isRanked)
            mainframe.setTitle("PLAYING RANKED HIDATO");
        else
            mainframe.setTitle("PLAYING CASUAL HIDATO");
        mainframe.revalidate();
        mainframe.repaint();
    }

    /**
     * It creates a new ViewHidatoSolving with the baseHidato solved.
     * 
     * @param baseHidato String representing the unsolved original hidato.
     * @param isRanked   Boolean to know where to go if button Back is pressed.
     */
    public void machineSolve(String baseHidato, boolean isRanked) {
        try {
            long startTime = System.currentTimeMillis();
            String hidatoSolved = domainController.getSolvedBoard(baseHidato);
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            JFrame mainframe = viewLogin;
            ViewHidatoSolving machineSolveView = new ViewHidatoSolving(this, hidatoSolved, isRanked, null, false);
            this.currentSolvingView = machineSolveView;
            mainframe.getContentPane().removeAll();
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 1.0;
            mainframe.add(machineSolveView, c);
            mainframe.setTitle("MACHINE SOLVED HIDATO");
            mainframe.revalidate();
            mainframe.repaint();

            JOptionPane.showMessageDialog(
                    viewLogin,
                    "CPU Time: " + elapsedTime + " milliseconds",
                    "Machine Solve Completed",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(currentSolvingView, "The hidato created has no valid solution.",
                    "UNSOLVABLE HIDATO", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * This function is called when the player clicks a valid cell (not #/*)
     * 
     * @param row is the row of the clicked cell.
     * @param col is the column of the clicked cell.
     */
    public void cellClicked(int row, int col) {

        if (currentSolvingView == null)
            return;

        HidatoBoard board = currentSolvingView.getBoard();
        String currentValStr = board.getCellValue(row, col);

        if (currentValStr.equals("#") || currentValStr.equals("*")) {
            return;
        }

        if (board.isGiven(row, col)) {
            return;
        }

        if (currentValStr.equals("?")) {
            Integer selected = currentSolvingView.getSelectedNumber();
            if (selected != null) {
                board.setCellValue(row, col, String.valueOf(selected));
                currentSolvingView.useNumber(selected);

                if (currentSolvingView.areAllNumbersPlaced()) {
                    checkBoardCompletion();
                }
            }
        } else {
            try {
                int val = Integer.parseInt(currentValStr);
                board.setCellValue(row, col, "?");
                currentSolvingView.returnNumber(val);
            } catch (NumberFormatException ex) {
            }
        }
    }

    /**
     * Retrieves the current board state and asks the Domain to validate it,
     * and shows the appropriate end-game pop-ups.
     */
    private void checkBoardCompletion() {
        String currentBoardStr = currentSolvingView.getBoard().exportBoard();

        boolean isCorrect = domainController.isSolution(currentBoardStr);
        boolean isRanked = currentSolvingView.isRanked();

        if (isCorrect && !isRanked) {
            currentSolvingView.stopChronometer();
            String time = currentSolvingView.getTimeTaken();
            domainController.endGameSession(0);

            JOptionPane.showMessageDialog(
                    viewLogin,
                    "Hidato Completed!\nTime taken: " + time,
                    "Congratulations!",
                    JOptionPane.INFORMATION_MESSAGE);

            showMainMenu();
        } else if (isCorrect && isRanked) {
            currentSolvingView.stopChronometer();
            String time = currentSolvingView.getTimeTaken();
            int score = currentSolvingView.getScore();
            domainController.endGameSession(score);
            JOptionPane.showMessageDialog(
                    viewLogin,
                    "Time taken: " + time + "\nYou scored " + score + " points!",
                    "Hidato Completed!",
                    JOptionPane.INFORMATION_MESSAGE);

            showMainMenu();
        } else {
            JOptionPane.showMessageDialog(
                    viewLogin,
                    "The board is full, but the solution is incorrect. Check your numbers and keep trying!",
                    "Incorrect Solution",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * After the user asks for a hint checks whether it can be presented to
     * him or not, and does so. If possible, selects a random cell from the
     * solved version of the current hidato and marks it as solved and fixed.
     */
    public void getHint() {
        if (currentSolvingView == null)
            return;

        HidatoBoard board = currentSolvingView.getBoard();
        String boardStr = board.exportBoard();
        String solvedBoardStr = domainController.getSolvedBoard(boardStr);

        if (solvedBoardStr != null && !solvedBoardStr.isEmpty()) {
            String[] currentLines = boardStr.trim().split("\n");
            String[] solvedLines = solvedBoardStr.trim().split("\n");

            boolean removedWrong = false;

            for (int r = 1; r < currentLines.length; r++) {
                String[] currentCells = currentLines[r].trim().split(",", -1);
                String[] solvedCells = solvedLines[r].trim().split(",", -1);

                for (int c = 0; c < currentCells.length; c++) {
                    if (c >= solvedCells.length)
                        continue;

                    String curVal = currentCells[c].trim();
                    String solVal = solvedCells[c].trim();

                    if (!curVal.equals("?") && !curVal.equals("#") && !curVal.equals("*") && !board.isGiven(r - 1, c)) {
                        if (!curVal.equals(solVal)) {
                            int val = Integer.parseInt(curVal);
                            board.setCellValue(r - 1, c, "?");
                            currentSolvingView.returnNumber(val);
                            removedWrong = true;
                        }
                    }
                }
            }

            if (removedWrong) {
                currentSolvingView.incrementHints();
                JOptionPane.showMessageDialog(viewLogin, "Incorrect cells were removed. This counts as TWO hints!",
                        "Hint Used", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int[] hint = domainController.getHint(boardStr);
        if (hint == null) {
            JOptionPane.showMessageDialog(viewLogin, "No hint available.", "Hint", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int row = hint[0], col = hint[1], value = hint[2];
        currentSolvingView.getBoard().setCellValue(row, col, String.valueOf(value));
        currentSolvingView.useNumber(value);
        currentSolvingView.repaint();

        if (currentSolvingView.areAllNumbersPlaced()) {
            checkBoardCompletion();
        }
    }

    /**
     * Initializes a GameSession with a manually imported Hidato string.
     * 
     * @param hidatoData The raw string data of the Hidato board.
     */
    public void playImportedHidato(String hidatoData) {
        try {
            domainController.startGameSession(hidatoData);

            JFrame mainframe = viewLogin;

            ViewHidatoSolving paintHidato = new ViewHidatoSolving(this, hidatoData, false, null, true);

            this.currentSolvingView = paintHidato;

            mainframe.getContentPane().removeAll();
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 1.0;
            mainframe.add(paintHidato, c);
            mainframe.setTitle("PLAYING CUSTOM HIDATO");
            mainframe.revalidate();
            mainframe.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    viewLogin,
                    "Invalid Hidato format. Please check your string or file.",
                    "Parsing Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Saves the current gameSession to be continued later on.
     * 
     * @param currentBoard
     * @param seconds
     * @param hints
     */
    public void saveGameSession(String currentBoard, long seconds, int hints) {
        domainController.saveGameSession(currentBoard, seconds, hints);
        showMainMenu();
    }

    /**
     * Loads an existing GameSession for the current user.
     */
    public void loadMatch() {
        String username = domainController.getCurrentUser();
        List<String> sessions = domainController.getSavedSessions(username);

        if (sessions == null || sessions.isEmpty()) {
            JOptionPane.showMessageDialog(viewLogin,
                    "No saved matches found.", "LOAD MATCH NOT POSSIBLE",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] options = sessions.toArray(new String[0]);
        String chosen = (String) JOptionPane.showInputDialog(
                viewLogin,
                "Select a saved match:",
                "Load Match",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (chosen == null)
            return;


        String boardStr = domainController.loadGameSession(chosen);
        String originalBoardStr = domainController.loadOriginalBoardFromSession(chosen);

        if (boardStr == null) {
            JOptionPane.showMessageDialog(viewLogin,
                    "Failed to load match.", "LOAD MATCH NOT POSSIBLE",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        long elapsedTime = domainController.getCurrentSessionElapsedTime();
        int hintsUsed = domainController.getCurrentSessionHintsUsed();
        boolean solved = domainController.isSolution(boardStr);

        JFrame mainframe = viewLogin;
        ViewHidatoSolving paintHidato = new ViewHidatoSolving(this, boardStr, (int) elapsedTime, hintsUsed, !solved, false);
        this.currentSolvingView = paintHidato;


        if (originalBoardStr != null) {
            this.currentSolvingView.getBoard().setOriginalBoard(originalBoardStr);
        }

        mainframe.getContentPane().removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        mainframe.add(paintHidato, c);
        mainframe.setTitle("PLAYING CASUAL HIDATO (Loaded)");
        mainframe.revalidate();
        mainframe.repaint();
    }

    /**
     * Shows the view of ConsultList
     */
    public void showConsultList() {
        viewConsultList.refresh();
        JFrame mainframe = viewLogin;
        mainframe.getContentPane().removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        viewConsultList.setVisible(true);
        mainframe.add(viewConsultList, c);
        mainframe.setTitle("MY HIDATO LISTS");
        mainframe.revalidate();
        mainframe.repaint();
    }

    /**
     * It returns all the hidato lists.
     */
    public List<String> getAvailableLists() {
        return domainController.getUserHidatoLists();
    }

    /**
     * Returns a map of key: Label and value: UUID to make
     * the hidatos understandable in the lists.
     * 
     * @param listName
     * @return The map of label and UUID of hidatos from listName.
     */
    public Map<String, String> getHidatoLabelsForList(String listName) {
        return domainController.getHidatoLabels(listName);
    }

    /**
     * Creates a new empty list (Create List button).
     * 
     * @param listName
     */
    public void createHidatoList(String listName) {
        domainController.createHidatoList(listName);
    }

    /**
     * Deletes an existing list (hidatos are not erased as
     * all of them are stored permanently in "All" list).
     * 
     * @param listName
     */
    public void deleteHidatoList(String listName) {
        domainController.deleteHidatoList(listName);
    }

    /**
     * Adds an hidato hidatoId to a list.
     * 
     * @param hidatoId
     * @param listName
     */
    public void addHidatoToList(String hidatoId, String listName) {
        domainController.addHidatoToList(hidatoId, listName);
    }

    /**
     * Deletes an hidatoId from a listName
     * 
     * @param hidatoId
     * @param listName
     */
    public void removeHidatoFromList(String hidatoId, String listName) {
        domainController.removeHidatoFromList(hidatoId, listName);
    }

    /**
     * Loads an hidato from consultLists
     * 
     * @param hidatoId
     */
    public void playHidatoById(String hidatoId) {
        String boardStr = domainController.loadHidatoById(hidatoId);
        if (boardStr == null) {
            JOptionPane.showMessageDialog(viewLogin,
                    "Could not load the selected Hidato.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        domainController.startGameSession(boardStr);
        JFrame mainframe = viewLogin;
        ViewHidatoSolving view = new ViewHidatoSolving(this, boardStr, false, null, true);
        this.currentSolvingView = view;
        mainframe.getContentPane().removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        mainframe.add(view, c);
        mainframe.setTitle("PLAYING HIDATO FROM LIST");
        mainframe.revalidate();
        mainframe.repaint();
    }

}