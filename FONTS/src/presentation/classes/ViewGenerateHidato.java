package presentation.classes;

import presentation.controller.PresentationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * View class when generating a new Hidato.
 * Users can select topology, adjacency and the number of rows and columns.
 */
public class ViewGenerateHidato extends JPanel implements ActionListener {
    private PresentationController presentationController;

    private JComboBox<String> comboTopology;
    private JComboBox<String> comboAdjacency;
    private JComboBox<String> comboDifficulty;
    private JTextField textFieldCells;
    private JLabel labelAdjacency;
    private JLabel labelDifficulty;
    private JButton buttonGenerate;
    private JButton buttonBack;

    /**
     * Default creator of ViewCreateHidato given an instance of
     * PresentationController.
     * It initializes the components of the JPanel.
     * 
     * @param presentationController
     */
    public ViewGenerateHidato(PresentationController presentationController) {
        this.presentationController = presentationController;
        initComponents();
    }

    /**
     * Initializes all used components and sets the appropriate layout.
     * It contains: a JLabel for the title, a JLable for the topology,
     * a JComboBox for the topologies choosing and the same for the adjacencies.
     * Then, labels and textfields for the (rows, cols) input, and finally two
     * buttons: generate and go back.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;

        JLabel labelTitle = new JLabel("GENERATE HIDATO");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(20, 10, 20, 10);
        add(labelTitle, c);

        c.gridwidth = 1;
        c.insets = new Insets(5, 10, 5, 10);
        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Topology:"), c);

        String[] topologies = { "Squares", "Triangles", "Hexagons" };
        comboTopology = new JComboBox<>(topologies);
        comboTopology.addActionListener(this);
        c.gridx = 1;
        add(comboTopology, c);

        c.gridx = 0;
        c.gridy = 2;
        labelAdjacency = new JLabel("Adjacency:");
        add(labelAdjacency, c);

        String[] adjacencies = { "Sides", "Sides + Vertices" };
        comboAdjacency = new JComboBox<>(adjacencies);
        c.gridx = 1;
        add(comboAdjacency, c);

        c.gridx = 0;
        c.gridy = 3;
        labelDifficulty = new JLabel("Difficulty:");
        add(labelDifficulty, c);

        String[] difficulties = { "EASY", "MEDIUM", "HARD" };
        comboDifficulty = new JComboBox<>(difficulties);
        c.gridx = 1;
        add(comboDifficulty, c);

        c.gridx = 0;
        c.gridy = 4;
        add(new JLabel("Number of Cells:"), c);
        textFieldCells = new JTextField(5);
        c.gridx = 1;
        add(textFieldCells, c);

        buttonGenerate = new JButton("Generate");
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 10, 5, 10);
        add(buttonGenerate, c);
        buttonGenerate.addActionListener(this);

        buttonBack = new JButton("Back");
        c.gridy = 6;
        c.insets = new Insets(5, 10, 10, 10);
        add(buttonBack, c);
        buttonBack.addActionListener(this);
    }

    /**
     * Treatment for all the Action Listeners.s
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(comboTopology)) {
            String selected = (String) comboTopology.getSelectedItem();
            boolean needsAdjacency = !selected.equals("Hexagons");
            labelAdjacency.setVisible(needsAdjacency);
            comboAdjacency.setVisible(needsAdjacency);
        } else if (source.equals(buttonGenerate)) {
            String topo = (String) comboTopology.getSelectedItem();
            String adj = comboAdjacency.isVisible() ? (String) comboAdjacency.getSelectedItem() : "Sides + Vertices";
            String dif = (String) comboDifficulty.getSelectedItem();

            try {
                int cells = Integer.parseInt(textFieldCells.getText());

                if (cells > 200)
                    throw new IllegalArgumentException("An Hidato cannot exceed 200 cells.");
                if (cells < 3)
                    throw new IllegalArgumentException("An Hidato must have at least 3 cells.");

                // To be implemented in PresentationController logic
                String hidato = presentationController.generateHidato(topo, adj, dif, cells);
                presentationController.playImportedHidato(hidato);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number of cells.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source.equals(buttonBack)) {
            presentationController.showMainMenu();
        }
    }
}