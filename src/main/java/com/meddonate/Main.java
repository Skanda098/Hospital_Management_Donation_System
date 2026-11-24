package com.meddonate;

import com.formdev.flatlaf.FlatLightLaf;
import com.meddonate.ui.MainDashboardFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            MainDashboardFrame frame = new MainDashboardFrame();
            frame.setVisible(true);
        });
    }
}