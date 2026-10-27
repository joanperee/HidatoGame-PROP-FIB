package presentation.classes;

import presentation.controller.PresentationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PlayCasual mode menu. The user can load an existing match, load an
 * existing hidato or start solving a new one.
 */
public class ViewPlayCasual extends JPanel implements ActionListener {
    private PresentationController presentationController;

    private JButton buttonLoadMatch;
    private JButton buttonLoadHidato;
    private JButton buttonStartSolving;
    private JButton buttonBack;

    /**
     * Default constructor.
     * 
     * @param presentationController
     */
    public ViewPlayCasual(PresentationController presentationController) {
        this.presentationController = presentationController;
        initComponents();
    }

    /**
     * Initializes the layout and the components, which are only buttons
     * and a label for the title.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        c.weighty = 0;
        c.insets = new Insets(10, 50, 10, 10);
        c.gridx = 0;

        JLabel labelTitle = new JLabel("CASUAL MODE");
        c.gridy = 0;
        c.insets = new Insets(20, 50, 20, 10);
        add(labelTitle, c);
        c.insets = new Insets(10, 50, 10, 10);

        buttonLoadMatch = new JButton("Load Match");
        c.gridy = 1;
        add(buttonLoadMatch, c);
        buttonLoadMatch.addActionListener(this);

        buttonLoadHidato = new JButton("Load Hidato");
        c.gridy = 2;
        add(buttonLoadHidato, c);
        buttonLoadHidato.addActionListener(this);

        buttonStartSolving = new JButton("Start Solving");
        c.gridy = 3;
        add(buttonStartSolving, c);
        buttonStartSolving.addActionListener(this);

        buttonBack = new JButton("Back to Main Menu");
        c.gridy = 4;
        c.insets = new Insets(30, 50, 10, 10);
        add(buttonBack, c);
        buttonBack.addActionListener(this);
    }

    /**
     * Treatment for all action listeners implemented.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object button = e.getSource();
        if (button.equals(buttonLoadHidato)) {
            presentationController.showConsultList();
        } else if (button.equals(buttonLoadMatch)) {
            presentationController.loadMatch();
        } else if (button.equals(buttonStartSolving)) {
            String[] options = { "EASY", "MEDIUM", "HARD" };
            String d = (String) JOptionPane.showInputDialog(
                    this, // parent component
                    "Select a difficulty:", // message
                    "Start Solving", // title
                    JOptionPane.QUESTION_MESSAGE, // element "?"
                    null, // icon
                    options, // selection values
                    options[0] // initial selection
            );
            if (d != null)
                presentationController.startSolving(d, false);
        } else if (button.equals(buttonBack)) {
            presentationController.showMainMenu();
        }
    }
}