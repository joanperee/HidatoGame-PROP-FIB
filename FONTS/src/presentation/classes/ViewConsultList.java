package presentation.classes;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import presentation.controller.PresentationController;

/**
 * View that allows the user to consult, create, and delete lists of Hidatos,
 * as well as add/remove Hidatos from them and start playing a selected Hidato.
 *
 * No domain imports — all data arrives as Strings from PresentationController.
 */
public class ViewConsultList extends JPanel implements ActionListener {

    private final PresentationController presentationController;

    private JList<String> listLists;
    private DefaultListModel<String> modelLists;
    private JList<String> listHidatos;
    private DefaultListModel<String> modelHidatos;

    private JButton buttonCreateList;
    private JButton buttonDeleteList;
    private JButton buttonAddHidato;
    private JButton buttonRemoveHidato;
    private JButton buttonPlayHidato;
    private JButton buttonBack;

    /**
     * Maps the Hidato readable label of (key) to its Hidato UUID (value).
     * The map is Hash: we do not want repeated hidatos in a list.
     * The map is Linked: we want to mantain the same order of hidatos
     * in the list so this way after some deletions and
     * additions the order is the same.
     */
    private final Map<String, String> labelToId = new LinkedHashMap<>();

    /**
     * Default constructor for this class.
     * 
     * @param presentationController
     */
    public ViewConsultList(PresentationController presentationController) {
        this.presentationController = presentationController;
        initComponents();
    }

    /**
     * Every time a modification is done, this method is triggered
     * to refresh the view with the new data.
     */
    public void refresh() {
        loadListsData();
    }

    /**
     * Initializes all of the components. The main structure is:
     * Two panels, one to list all of the lists and another to list
     * all the hidatos inside the selected list. There are buttons to
     * Create/delete a list and to Add/Delete an hidato from a list.
     * Disclaimer: the list "All" contains all hidatos ever generated,
     * and is read-only for all users.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // This panel will contain all the lists stored.
        JPanel panelLeft = new JPanel(new GridBagLayout());
        panelLeft.setOpaque(false);

        modelLists = new DefaultListModel<>();
        listLists = new JList<>(modelLists);
        listLists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listLists.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                loadHidatosForSelectedList();
        });

        // listLists inside a scroll pane in case we have
        // too many lists to fit in the window.
        JScrollPane scrollLists = new JScrollPane(listLists);

        GridBagConstraints cLeft = new GridBagConstraints();
        cLeft.gridx = 0;
        cLeft.gridy = 0;
        cLeft.gridwidth = 2;
        cLeft.weightx = 1.0;
        cLeft.weighty = 1.0;
        cLeft.fill = GridBagConstraints.BOTH;
        cLeft.insets = new Insets(10, 10, 10, 10);
        panelLeft.add(scrollLists, cLeft);

        buttonCreateList = new JButton("Create");
        buttonDeleteList = new JButton("Delete");
        buttonCreateList.addActionListener(this);
        buttonDeleteList.addActionListener(this);

        cLeft.gridwidth = 1;
        cLeft.weighty = 0.0;
        cLeft.fill = GridBagConstraints.HORIZONTAL;
        cLeft.gridy = 1;
        cLeft.gridx = 0;
        cLeft.insets = new Insets(0, 10, 10, 5);
        panelLeft.add(buttonCreateList, cLeft);

        cLeft.gridx = 1;
        cLeft.insets = new Insets(0, 5, 10, 10);
        panelLeft.add(buttonDeleteList, cLeft);

        // This panel will contain the hidatos inside a
        // selected list ("All" by default).
        JPanel panelRight = new JPanel(new GridBagLayout());
        panelRight.setOpaque(false);

        modelHidatos = new DefaultListModel<>();
        listHidatos = new JList<>(modelHidatos);
        listHidatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollHidatos = new JScrollPane(listHidatos);

        GridBagConstraints cRight = new GridBagConstraints();
        cRight.gridx = 0;
        cRight.gridy = 0;
        cRight.gridheight = 3;
        cRight.weightx = 1.0;
        cRight.weighty = 1.0;
        cRight.fill = GridBagConstraints.BOTH;
        cRight.insets = new Insets(10, 10, 10, 10);
        panelRight.add(scrollHidatos, cRight);

        buttonPlayHidato = new JButton("Play Hidato");
        buttonAddHidato = new JButton("Add Hidato");
        buttonRemoveHidato = new JButton("Remove Hidato");

        buttonPlayHidato.setFont(new Font("Segoe UI", Font.BOLD, 13));
        buttonPlayHidato.setBackground(new Color(0x82, 0x92, 0x98));

        buttonPlayHidato.addActionListener(this);
        buttonAddHidato.addActionListener(this);
        buttonRemoveHidato.addActionListener(this);

        cRight.gridx = 1;
        cRight.gridheight = 1;
        cRight.weightx = 0.0;
        cRight.weighty = 0.0;
        cRight.fill = GridBagConstraints.HORIZONTAL;
        cRight.anchor = GridBagConstraints.NORTH;

        cRight.gridy = 0;
        cRight.insets = new Insets(10, 0, 5, 10);
        panelRight.add(buttonPlayHidato, cRight);
        cRight.gridy = 1;
        cRight.insets = new Insets(5, 0, 5, 10);
        panelRight.add(buttonAddHidato, cRight);
        cRight.gridy = 2;
        cRight.insets = new Insets(5, 0, 10, 10);
        panelRight.add(buttonRemoveHidato, cRight);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.35;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 5, 5);
        add(panelLeft, c);

        c.gridx = 1;
        c.weightx = 0.65;
        c.insets = new Insets(10, 5, 5, 10);
        add(panelRight, c);

        buttonBack = new JButton("Back");
        buttonBack.addActionListener(this);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 10, 10, 10);
        add(buttonBack, c);
    }

    /**
     * Loads all of the lists saved.
     */
    private void loadListsData() {
        modelLists.clear();
        modelLists.addElement("all");
        List<String> userLists = presentationController.getAvailableLists();
        for (String name : userLists) {
            if (!name.equals("all"))
                modelLists.addElement(name);
        }
        if (modelLists.size() > 0)
            listLists.setSelectedIndex(0);
    }

    /**
     * Loads all of the hidatos stored inside the selected list.
     */
    private void loadHidatosForSelectedList() {
        modelHidatos.clear();
        labelToId.clear();
        String selectedList = listLists.getSelectedValue();
        if (selectedList == null)
            return;

        Map<String, String> labels = presentationController.getHidatoLabelsForList(selectedList);
        for (Map.Entry<String, String> entry : labels.entrySet()) {
            labelToId.put(entry.getValue(), entry.getKey()); // value = label, key = UUID
            modelHidatos.addElement(entry.getValue());
        }

        // We want to make All list readable-only so disable buttons
        boolean isAll = selectedList.equals("all");
        buttonDeleteList.setEnabled(!isAll);
        buttonAddHidato.setEnabled(!isAll);
        buttonRemoveHidato.setEnabled(!isAll);
    }

    /**
     * Returns the UUID of the currently selected hidato, or null.
     */
    private String getSelectedHidatoId() {
        String label = listHidatos.getSelectedValue();
        if (label == null)
            return null;
        return labelToId.get(label);
    }

    /**
     * Treatment of all interatvie actions in this class.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == buttonBack) {
            presentationController.showMainMenu();

        } else if (source == buttonPlayHidato) {
            String id = getSelectedHidatoId();
            if (id == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a Hidato to play.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            presentationController.playHidatoById(id);

        } else if (source == buttonCreateList) {
            String name = JOptionPane.showInputDialog(this, "Enter the name of the new list:");
            if (name == null || name.trim().isEmpty())
                return;
            name = name.trim();
            if (modelLists.contains(name)) {
                JOptionPane.showMessageDialog(this,
                        "A list with that name already exists.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            presentationController.createHidatoList(name);
            loadListsData();
            listLists.setSelectedValue(name, true);

        } else if (source == buttonDeleteList) {
            String selectedList = listLists.getSelectedValue();
            if (selectedList == null || selectedList.equals("all"))
                return;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete list '" + selectedList + "'?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                presentationController.deleteHidatoList(selectedList);
                loadListsData();
            }

        } else if (source == buttonAddHidato) {
            String selectedList = listLists.getSelectedValue();
            if (selectedList == null || selectedList.equals("all"))
                return;

            // Updates the labels for all hidatos
            Map<String, String> allLabels = presentationController.getHidatoLabelsForList("all");
            Map<String, String> listLabels = presentationController.getHidatoLabelsForList(selectedList);
            Set<String> alreadyIn = new HashSet<>(listLabels.keySet());
            Map<String, String> available = new LinkedHashMap<>();

            // Iterates through allLabels Map and puts the new hidatos inside available map.
            for (Map.Entry<String, String> entry : allLabels.entrySet()) {
                if (!alreadyIn.contains(entry.getKey())) {
                    available.put(entry.getValue(), entry.getKey());
                }
            }

            if (available.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "All available Hidatos are already in this list.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] choices = available.keySet().toArray(new String[0]);
            String chosen = (String) JOptionPane.showInputDialog(this,
                    "Select a Hidato to add to '" + selectedList + "':",
                    "Add Hidato", JOptionPane.PLAIN_MESSAGE,
                    null, choices, choices[0]);

            if (chosen != null) {
                presentationController.addHidatoToList(available.get(chosen), selectedList);
                loadHidatosForSelectedList();
            }

        } else if (source == buttonRemoveHidato) {
            String selectedList = listLists.getSelectedValue();
            String id = getSelectedHidatoId();
            if (selectedList == null || id == null || selectedList.equals("all")) {
                JOptionPane.showMessageDialog(this,
                        "Please select a Hidato to remove.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            presentationController.removeHidatoFromList(id, selectedList);
            loadHidatosForSelectedList();
        }
    }
}