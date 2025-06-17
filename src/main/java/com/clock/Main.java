package com.clock;

import com.clock.controller.CountdownController;
import com.clock.view.CountdownGUI;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CountdownController controller = new CountdownController();
            CountdownGUI gui = new CountdownGUI(controller);
            gui.setVisible(true);
        });
    }
} 