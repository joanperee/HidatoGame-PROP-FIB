package presentation.classes;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import presentation.controller.PresentationController;

/**
 * Main menu view which inherits of the ViewLogin Frame.
 */
public class ViewMenu extends JPanel implements ActionListener {
    private PresentationController presentationController;

    // buttons
    private JButton buttonPlayCasual;
    private JButton buttonPlayRanked;
    private JButton buttonGenerateHidato;
    private JButton buttonConsultList;
    private JButton buttonViewRanking;
    private JButton buttonCreateHidato;
    private JButton buttonLogout;

    /**
     * Default constructor.
     * 
     * @param presentationController
     */
    public ViewMenu(PresentationController presentationController) {
        this.presentationController = presentationController;
        initComponents();
    }

    /**
     * Initializes the layout and all the buttons (playable options).
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.weighty = 0;
        c.insets = new Insets(10, 50, 10, 10);

        buttonPlayCasual = new JButton("Play Casual");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        add(buttonPlayCasual, c);
        buttonPlayCasual.addActionListener(this);

        buttonPlayRanked = new JButton("Play Ranked");
        c.gridy = 1;
        add(buttonPlayRanked, c);
        buttonPlayRanked.addActionListener(this);

        buttonGenerateHidato = new JButton("Generate Hidato");
        c.gridy = 2;
        add(buttonGenerateHidato, c);
        buttonGenerateHidato.addActionListener(this);

        buttonCreateHidato = new JButton("Create/Import Hidato");
        c.gridy = 3;
        add(buttonCreateHidato, c);
        buttonCreateHidato.addActionListener(this);

        buttonViewRanking = new JButton("View Ranking");
        c.gridy = 4;
        add(buttonViewRanking, c);
        buttonViewRanking.addActionListener(this);

        buttonConsultList = new JButton("Consult Lists");
        c.gridy = 5;
        add(buttonConsultList, c);
        buttonConsultList.addActionListener(this);

        buttonLogout = new JButton("LogOut");
        c.gridy = 6;
        add(buttonLogout, c);
        buttonLogout.addActionListener(this);
    }

    /**
     * Treatment for all action listeners implemented.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object button = e.getSource();

        if (button.equals(buttonPlayCasual)) {
            presentationController.showPlayCasual();
        } else if (button.equals(buttonGenerateHidato)) {
            presentationController.showGenerateHidato();
        } else if (button.equals(buttonPlayRanked)) {
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
                presentationController.startSolving(d, true);
        } else if (button.equals(buttonViewRanking)) {
            presentationController.showRanking();
        } else if (button.equals(buttonLogout)) {
            presentationController.showLogin();
        } else if (button.equals(buttonCreateHidato)) {
            presentationController.showCreateHidato();
        } else if (button.equals(buttonConsultList)) {
            presentationController.showConsultList();
        }
    }
}