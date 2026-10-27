package presentation.classes;

import presentation.controller.PresentationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * View class to import a custom Hidato string via text paste or file selection.
 */
public class ViewCreateHidato extends JPanel implements ActionListener {
    private PresentationController presentationController;

    private JTextArea textAreaBoard;
    private JButton buttonLoadFile;
    private JButton buttonPlay;
    private JButton buttonBack;

    /**
     * Default creator of ViewCreateHidato given an instance of
     * PresentationController.
     * It initializes the components of the JPanel.
     * 
     * @param presentationController
     */
    public ViewCreateHidato(PresentationController presentationController) {
        this.presentationController = presentationController;
        initComponents();
    }

    /**
     * Initializes all used components and sets the appropriate layout.
     * It contains a JLabel for the title, a JTextArea to paste an hidato in
     * a string format, a JScrollPane in case the hidato pasted is too large,
     * a JButon to select a file containing an hidato, and two buttons
     * to go back and to play the hidato.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Create / Import Hidato");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(20, 50, 10, 20);
        add(titleLabel, c);

        textAreaBoard = new JTextArea(15, 30);
        JScrollPane scrollPane = new JScrollPane(textAreaBoard);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(10, 50, 10, 50);
        add(scrollPane, c);

        buttonLoadFile = new JButton("Select File");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.5;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 50, 20, 10);
        add(buttonLoadFile, c);
        buttonLoadFile.addActionListener(this);

        // New JPanel inside this panel to accomodate the 2 buttons: Back and Play.
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtons.setOpaque(false);
        buttonBack = new JButton("Back");
        buttonPlay = new JButton("Play Hidato");
        rightButtons.add(buttonBack);
        rightButtons.add(buttonPlay);
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 10, 20, 50);
        add(rightButtons, c);
        buttonBack.addActionListener(this);
        buttonPlay.addActionListener(this);
    }

    /**
     * Treatment for all the Action Listeners.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // Load the hidato and put it in the TextAreaBoard
        if (source.equals(buttonLoadFile)) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    String content = new String(Files.readAllBytes(selectedFile.toPath()));
                    textAreaBoard.setText(content);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "File Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        // Play the hidato
        else if (source.equals(buttonPlay)) {
            String hidatoData = textAreaBoard.getText().trim();
            if (hidatoData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please paste a Hidato string or load a file first.", "Empty Data",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            presentationController.playImportedHidato(hidatoData);
        }
        // Go back
        else if (source.equals(buttonBack)) {
            presentationController.showMainMenu();
        }
    }
}