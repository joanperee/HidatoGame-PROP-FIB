package presentation.classes;

import presentation.controller.PresentationController;
import persistence.*;
import domain.controller.DomainController;
import domain.model.User;
import domain.model.Hidato;
import domain.model.GameSession;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.util.UUID;

/**
 * Entry point of our Hidato Game application. First class executed.
 *
 * It initializes the program running the GUI through the
 * PresentationController.
 * To ensure stability and security between layers, we create a new thread using
 * "invokeLater" method and establish it to "Runnable". This way, the thread
 * management
 * takes control of it, over the MainThread.
 *
 * We declare the usage of the following SOURCES:
 * Orcale tutorial invokeLater (https://shorturl.at/RJfni)
 * Laboratorio Java GUI - Universidad Carlos III de Madrid
 * (https://shorturl.at/IQHPK)
 * Nimbus Look And Feel (https://shorturl.at/G0Kmg)
 */
public class Main {
    public static void main(String[] args) {
        // Apply the Nimbus GUI to all Swing components
        setLookAndFeel();

        // Instantiate the data managers and the GUI to start our game
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DataManager<User, String> userManager = JsonUserManager.getInstance();
                DataManager<Hidato, UUID> hidatoManager = JsonHidatoManager.getInstance();
                DataManager<GameSession, UUID> gameSessionManager = JsonGameSessionManager.getInstance();
                DataListManager<Hidato, UUID> hidatoListManager = JsonHidatoManager.getInstance();
                DataListManager<GameSession, UUID> gameSessionListManager = JsonGameSessionManager.getInstance();

                DomainController.getInstance(
                        hidatoManager, gameSessionManager, userManager,
                        hidatoListManager, gameSessionListManager);

                PresentationController.get_instance().loadGUI();
            }
        });
    }

    /**
     * Applies the Nimbus Look & Feel to all GUI elements and overrides its default
     * colours.
     */
    private static void setLookAndFeel() {
        try {
            NimbusLookAndFeel nimbusLAF = new NimbusLookAndFeel();
            nimbusLAF.getDefaults().put("defaultFont", new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
            UIManager.setLookAndFeel(nimbusLAF);

            // We applied a color palette obtained randomly thanks to: https://coolors.co/
            UIManager.put("control", new java.awt.Color(245, 240, 230));
            UIManager.put("text", new java.awt.Color(60, 45, 30));
            UIManager.put("nimbusBase", new java.awt.Color(160, 100, 50));
            UIManager.put("nimbusBlueGrey", new java.awt.Color(200, 185, 165));
            UIManager.put("nimbusSelectionBackground", new java.awt.Color(190, 120, 60));
            UIManager.put("nimbusLightBackground", new java.awt.Color(255, 252, 245));
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("The Nimbus LAF couldn't be applied." + e.getMessage());
        }
    }
}