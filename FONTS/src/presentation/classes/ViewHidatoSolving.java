package presentation.classes;

import presentation.controller.PresentationController;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Main menu view class which inherits from the ViewLogin Frame.
 */
public class ViewHidatoSolving extends JPanel implements ActionListener {
    private PresentationController presentationController;

    private JButton buttonStart;
    private JButton buttonBack;
    private JButton buttonHint;
    private JButton buttonMachineSolve;
    private JButton buttonPrevNumber;
    private JButton buttonNextNumber;
    private JButton buttonSave;

    private HidatoBoard hidatoBoard;

    private JLabel labelChronometer;
    private Timer chronometerTimer;
    private int secondsElapsed;
    private boolean isRunning;

    private JLabel labelSelectedNumber;
    private List<Integer> availableNumbers;
    private int currentNumberIndex = 0;

    private boolean isRanked_;
    private int hintsUsed_;
    private String difficulty_;
    private String baseHidato_;
    private boolean isModifiable_;

    /**
     * Default constructor for a ViewHidatoSolving instance.
     * 
     * @param presentationController Instance of the controller who communicates
     *                               with domain.
     * @param board                  The hidato represented in a valid string.
     * @param isRanked               True if in PlayRanked, false if in PlayCasual.
     * @param difficulty             EASY, MEDIUM or HARD.
     */
    public ViewHidatoSolving(PresentationController presentationController, String board,
                             boolean isRanked, String difficulty, boolean isModifiable) {
        this.presentationController = presentationController;
        this.isRanked_ = isRanked;
        this.secondsElapsed = 0;
        this.isRunning = false;
        this.chronometerTimer = new Timer(1000, this);
        this.hintsUsed_ = 0;
        this.difficulty_ = difficulty;
        this.baseHidato_ = board;
        this.isModifiable_ = isModifiable;
        initComponents(board);
        if (difficulty != null)
            this.buttonStart.doClick();
    }

    public ViewHidatoSolving(PresentationController presentationController, String board,
                             int elapsedTime, int hintsUsed, boolean isModifiable, boolean isRanked) {
        this.presentationController = presentationController;
        this.secondsElapsed = elapsedTime;
        this.hintsUsed_ = hintsUsed;
        this.isRunning = false;
        this.chronometerTimer = new Timer(1000, this);
        // ← sin chronometerTimer.start() aquí
        this.baseHidato_ = board;
        this.isModifiable_ = isModifiable;
        initComponents(board);
        this.buttonStart.doClick();
    }

    /**
     * Initializes all the graphic components of the current ViewHidatoSolving.
     * It contains 4 buttons (Start/Pause, Back, Hint and Machine Solve), the
     * HidatoBoard where the hidato is painted and a chronometer.
     * 
     * @param board A valid string representation for the hidato wanted.
     */
    private void initComponents(String board) {
        String header[] = board.split(",");
        String adjacencyType = (header[1].equals("C")) ? "sides" : "sides and vertices";

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0;

        buttonStart = new JButton("Start");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 50, 10, 10);
        add(buttonStart, c);
        buttonStart.addActionListener(this);

        JLabel labelAdjacency = new JLabel("Adjacency: " + adjacencyType);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        add(labelAdjacency, c);

        labelChronometer = new JLabel("00:00");
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        add(labelChronometer, c);

        buttonHint = new JButton("Hint");
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 10, 10, 50);
        add(buttonHint, c);
        buttonHint.addActionListener(this);

        // Creation of the painted hidato.
        hidatoBoard = new HidatoBoard(board);
        hidatoBoard.setVisible(true);
        // Logic to select cells when the user clicks on them. We override
        // the basic logic of mouseClicked to get the cell where the click occurred.
        // We used the SOURCE:
        // https://www.geeksforgeeks.org/java/mouselistener-mousemotionlistener-java/
        hidatoBoard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!isRunning)
                    return;
                int[] clickedCell = hidatoBoard.getCellAt(e.getX(), e.getY());
                if (clickedCell != null) {
                    int row = clickedCell[0];
                    int col = clickedCell[1];
                    presentationController.cellClicked(row, col);
                }
            }
        });

        // Scroll pane in case the hidato is too large for (1024, 768)px window
        JScrollPane scr = new JScrollPane(hidatoBoard);
        scr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 50, 10, 50);
        add(scr, c);

        calculateAvailableNumbers(board);

        // From now on we work on a new subPanel to select the number we want to fill
        // in.
        JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        numberPanel.setOpaque(false);

        buttonPrevNumber = new JButton("<");
        buttonPrevNumber.setPreferredSize(new Dimension(60, 30));

        buttonNextNumber = new JButton(">");
        buttonNextNumber.setPreferredSize(new Dimension(60, 30));

        String initialText = availableNumbers.isEmpty() ? "-" : String.valueOf(availableNumbers.get(0));
        labelSelectedNumber = new JLabel(initialText, SwingConstants.CENTER);
        labelSelectedNumber.setPreferredSize(new Dimension(80, 30));

        numberPanel.add(buttonPrevNumber);
        numberPanel.add(labelSelectedNumber);
        numberPanel.add(buttonNextNumber);

        buttonPrevNumber.addActionListener(this);
        buttonNextNumber.addActionListener(this);

        updateNumberLabel();

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 0.0;
        c.insets = new Insets(10, 50, 10, 50);
        add(numberPanel, c);

        buttonBack = new JButton("Back");
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 50, 10, 10);
        add(buttonBack, c);
        buttonBack.addActionListener(this);

        buttonSave = new JButton("Save & Exit");
        c.gridx = 2;
        c.gridy = 3;
        add(buttonSave, c);
        buttonSave.addActionListener(this);

        buttonMachineSolve = new JButton("Machine Solve");
        c.gridx = 3;
        c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 10, 10, 50);
        add(buttonMachineSolve, c);
        buttonMachineSolve.addActionListener(this);
    }

    /**
     * Helper method to set the crhonometer label with the
     * appropriate format minutes:seconds (instead of just seconds).
     */
    private void updateChronometerDisplay() {
        int minutes = (secondsElapsed % 3600) / 60;
        int seconds = secondsElapsed % 60;
        labelChronometer.setText(String.format("%02d:%02d", minutes, seconds));
    }

    /**
     * @return True if PlayRanked mode, false if PlayCasual mode.
     */
    public boolean isRanked() {
        return isRanked_;
    }

    /**
     * Computes the final score obtained when completing an hidato.
     * We designed the formula given a base score (which is the maximum
     * a user can recieve), the number of hints used and the time used,
     * alog with some penalties given the difficulty.
     * 
     * @return The score obtained (integer).
     */
    public int getScore() {
        int base, hintPenalty, timePenalty;

        switch (difficulty_) {
            case "HARD":
                base = 3000;
                hintPenalty = 300;
                timePenalty = 3;
                break;
            case "MEDIUM":
                base = 2000;
                hintPenalty = 200;
                timePenalty = 2;
                break;
            default:
                base = 1000;
                hintPenalty = 100;
                timePenalty = 1;
                break;
        }
        // FORMULA
        int score = base - (hintsUsed_ * hintPenalty) - (secondsElapsed * timePenalty);
        return Math.max(0, score);
    }

    /**
     * Treatment of all action listeners implemented.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (isModifiable_) {
            if (source.equals(chronometerTimer)) {
                secondsElapsed++;
                updateChronometerDisplay();
            } else if (source.equals(buttonNextNumber)) {
                if (availableNumbers != null && !availableNumbers.isEmpty()) {
                    currentNumberIndex = (currentNumberIndex + 1) % availableNumbers.size();
                    updateNumberLabel();
                }
            } else if (source.equals(buttonPrevNumber)) {
                if (availableNumbers != null && !availableNumbers.isEmpty()) {
                    currentNumberIndex = (currentNumberIndex - 1 + availableNumbers.size()) % availableNumbers.size();
                    updateNumberLabel();
                }
            } else if (source.equals(buttonStart)) {
                if (!isRunning) {
                    chronometerTimer.start();
                    buttonStart.setText("Pause");
                    isRunning = true;
                    hidatoBoard.setVisible(true);
                } else {
                    chronometerTimer.stop();
                    buttonStart.setText("Start");
                    isRunning = false;
                    hidatoBoard.setVisible(false);
                }
                revalidate();
                repaint();
            } else if (source.equals(buttonHint)) {
                if (isRunning) {
                    hintsUsed_++;
                    presentationController.getHint();
                }
            } else if (source.equals(buttonMachineSolve)) {
                chronometerTimer.stop();
                isRunning = false;
                presentationController.machineSolve(baseHidato_, isRanked_);
            }
        }
        if (source.equals(buttonSave)) {
            chronometerTimer.stop();
            isRunning = false;
            presentationController.saveGameSession(getBoard().exportBoard(), secondsElapsed, hintsUsed_);
        } else if (source.equals(buttonBack)) {
            chronometerTimer.stop();
            presentationController.showMainMenu();
        }
    }

    /**
     * @return The hidato board which is currently being played.
     */
    public HidatoBoard getBoard() {
        return hidatoBoard;
    }

    /**
     * Select number logic: calculates all numeric values available.
     * 
     * @param board The hidato board which is currently being played in a string
     *              format.
     */
    private void calculateAvailableNumbers(String board) {
        availableNumbers = new ArrayList<>();
        HashSet<Integer> usedNumbers = new HashSet<>();
        int totalPlayableCells = 0;

        String[] lines = board.trim().split("\n");
        String[] header = lines[0].trim().split(",");
        int rows = Integer.parseInt(header[2].trim());

        for (int i = 1; i <= rows; i++) {
            if (i < lines.length) {
                String[] cells = lines[i].trim().split(",");
                for (String cell : cells) {
                    cell = cell.trim();
                    if (!cell.equals("#") && !cell.equals("*")) {
                        totalPlayableCells++;
                        if (!cell.equals("?")) {
                            usedNumbers.add(Integer.parseInt(cell));
                        }
                    }
                }
            }
        }

        for (int i = 1; i <= totalPlayableCells; i++) {
            if (!usedNumbers.contains(i)) {
                availableNumbers.add(i);
            }
        }
        Collections.sort(availableNumbers);
        currentNumberIndex = 0;
    }

    /**
     * Changes the text of the number selected label. For instance,
     * if the user places a "67", then this label updates to the next
     * available number which would be "69".
     */
    private void updateNumberLabel() {
        if (availableNumbers.isEmpty()) {
            labelSelectedNumber.setText("-");
        } else {
            labelSelectedNumber.setText(String.valueOf(availableNumbers.get(currentNumberIndex)));
        }
    }

    /**
     * @return The selected number which is the text of the number label.
     */
    public Integer getSelectedNumber() {
        if (availableNumbers.isEmpty())
            return null;
        return availableNumbers.get(currentNumberIndex);
    }

    /**
     * When the user places a number in the hidato this function is
     * triggered to know not to show again that number.
     * 
     * @param n The integer filled in the hidato cell.
     */
    public void useNumber(Integer n) {
        availableNumbers.remove(n);
        if (availableNumbers.isEmpty()) {
            currentNumberIndex = 0;
        } else if (currentNumberIndex >= availableNumbers.size()) {
            currentNumberIndex = 0;
        }
        updateNumberLabel();
    }

    /**
     * When the user clicks on a filled cell (not already solved) this
     * function is triggered to return that value to the number carrussel.
     * 
     * @param n The integer inside the previous filled cell.
     */
    public void returnNumber(Integer n) {
        if (!availableNumbers.contains(n)) {
            availableNumbers.add(n);
            Collections.sort(availableNumbers);
            currentNumberIndex = availableNumbers.indexOf(n);
        }
        updateNumberLabel();
    }

    /**
     * @return True if all numeric values are already filled in all empty cells,
     *         false otherwise.
     */
    public boolean areAllNumbersPlaced() {
        return availableNumbers.isEmpty();
    }

    /**
     * @return The elapsed time taken to solve the hidato. It is shown in a message
     *         dialog.
     */
    public String getTimeTaken() {
        int minutes = (secondsElapsed % 3600) / 60;
        int seconds = secondsElapsed % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Method used in PresentationController to stop the crhono from there.
     */
    public void stopChronometer() {
        chronometerTimer.stop();
        isRunning = false;
    }

    /**
     * Method used in PresentationController to increment the hints used from there.
     */
    public void incrementHints() {
        this.hintsUsed_++;
    }
}