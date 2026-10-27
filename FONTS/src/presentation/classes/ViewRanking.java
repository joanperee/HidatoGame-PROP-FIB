package presentation.classes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import presentation.controller.PresentationController;

/**
 * View that displays the top 100 ranked scores.
 * Fetches data from DomainController via PresentationController.
 */
public class ViewRanking extends JPanel implements ActionListener {
    private PresentationController presentationController;

    private JTable rankingTable;
    private DefaultTableModel tableModel;
    private JButton buttonBack;

    /**
     * Default constructor.
     * 
     * @param presentationController
     */
    public ViewRanking(PresentationController presentationController) {
        this.presentationController = presentationController;
        initComponents();
    }

    /**
     * Initializes the layout and its components, which are the JTable
     * containing the ranking, a button and a title.
     */
    private void initComponents() {
        setLayout(new BorderLayout());

        JLabel labelTitle = new JLabel("TOP 100 RANKING", SwingConstants.CENTER);
        labelTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(labelTitle, BorderLayout.NORTH);

        String[] columns = { "#", "Username", "Total Score" };
        tableModel = new DefaultTableModel(columns, 0) {
            // We overwrite the function as by default all cells are editable,
            // and we do not want the users to be able to modify the ranking even locally.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rankingTable = new JTable(tableModel);
        rankingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankingTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));
        buttonBack = new JButton("Back");
        buttonBack.addActionListener(this);
        bottomPanel.add(buttonBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads the top 100 ranking data into the table.
     * Expected format per entry: "username,score,time".
     * 
     * @param entries List of ranking entries from DomainController.
     */
    public void loadRanking(List<String> entries) {
        tableModel.setRowCount(0);
        int rank = 1;
        for (String entry : entries) {
            String[] parts = entry.split(",");
            tableModel.addRow(new Object[] { rank, parts[0], parts[1] });
            rank++;
        }
    }

    /**
     * Treatment for the action listener implemented.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(buttonBack)) {
            presentationController.showMainMenu();
        }
    }
}