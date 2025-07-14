package com.wsxdev.simuladorcircuitos;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.wsxdev.simuladorcircuitos.vista.MainWindowNew;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal del simulador de circuitos eléctricos
 * Implementa un simulador visual con conexiones dinámicas
 */
public class AppMain {
    
    public static void main(String[] args) {
        // Configurar el Look and Feel FlatLaf
        SwingUtilities.invokeLater(() -> {
            setupLookAndFeel();
            
            // Crear y mostrar la ventana principal
            try {
                MainWindowNew ventanaPrincipal = new MainWindowNew();
                ventanaPrincipal.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error al inicializar la aplicación: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Configura el Look and Feel de la aplicación usando FlatLaf
     */
    private static void setupLookAndFeel() {
        try {
            // Configurar FlatLaf Dark como tema por defecto
            UIManager.setLookAndFeel(new FlatDarkLaf());
            
            // Configuraciones adicionales para FlatLaf
            System.setProperty("flatlaf.useWindowDecorations", "false");
            System.setProperty("flatlaf.menuBarEmbedded", "false");
            
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel FlatLaf: " + e.getMessage());
            // Fallback al Look and Feel del sistema
            try {
                // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}