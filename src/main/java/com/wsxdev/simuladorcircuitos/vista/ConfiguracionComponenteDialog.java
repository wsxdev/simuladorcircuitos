package com.wsxdev.simuladorcircuitos.vista;

import com.wsxdev.simuladorcircuitos.modelo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Diálogo para configurar las propiedades de los componentes
 */
public class ConfiguracionComponenteDialog extends JDialog {
    private Componente componente;
    private boolean aceptado = false;
    
    // Campos de entrada
    private JTextField campoNombre;
    private JTextField campoValorPrincipal;
    private JTextField campoValorSecundario;
    private JLabel etiquetaUnidad;
    private JLabel etiquetaValorSecundario;
    
    public ConfiguracionComponenteDialog(Window parent, Componente componente) {
        super(parent, "Configurar " + componente.getTipo(), ModalityType.APPLICATION_MODAL);
        this.componente = componente;
        initializeDialog();
        setupComponents();
        cargarValores();
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
    }
    
    private void setupComponents() {
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        JLabel titulo = new JLabel("Configurar " + componente.getTipo().toUpperCase());
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        panelPrincipal.add(titulo, gbc);
        
        gbc.gridwidth = 1;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        panelPrincipal.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        campoNombre = new JTextField(componente.getNombre(), 15);
        panelPrincipal.add(campoNombre, gbc);
        
        gbc.gridwidth = 1;
        
        // Valor principal
        gbc.gridx = 0; gbc.gridy = 2;
        panelPrincipal.add(new JLabel(getEtiquetaValorPrincipal() + ":"), gbc);
        gbc.gridx = 1;
        campoValorPrincipal = new JTextField(String.valueOf(componente.getValorPrincipal()), 10);
        panelPrincipal.add(campoValorPrincipal, gbc);
        gbc.gridx = 2;
        etiquetaUnidad = new JLabel(componente.getUnidad());
        panelPrincipal.add(etiquetaUnidad, gbc);
        
        // Valor secundario (si aplica)
        String etiquetaSecundaria = getEtiquetaValorSecundario();
        if (etiquetaSecundaria != null) {
            gbc.gridx = 0; gbc.gridy = 3;
            etiquetaValorSecundario = new JLabel(etiquetaSecundaria + ":");
            panelPrincipal.add(etiquetaValorSecundario, gbc);
            gbc.gridx = 1;
            campoValorSecundario = new JTextField(String.valueOf(getValorSecundario()), 10);
            panelPrincipal.add(campoValorSecundario, gbc);
            gbc.gridx = 2;
            panelPrincipal.add(new JLabel(getUnidadSecundaria()), gbc);
        }
        
        // Información adicional específica del componente
        agregarInformacionEspecifica(panelPrincipal, gbc);
        
        add(panelPrincipal, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton botonAceptar = new JButton("Aceptar");
        JButton botonCancelar = new JButton("Cancelar");
        
        botonAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validarYAplicarCambios()) {
                    aceptado = true;
                    dispose();
                }
            }
        });
        
        botonCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(botonAceptar);
        panelBotones.add(botonCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarValores() {
        // Los valores ya se cargan en setupComponents
    }
    
    private String getEtiquetaValorPrincipal() {
        if (componente instanceof Resistencia) return "Resistencia";
        if (componente instanceof FuenteVoltaje) return "Voltaje";
        if (componente instanceof Led) return "Voltaje Directo";
        if (componente instanceof Voltimetro) return "Rango Máximo";
        if (componente instanceof Amperimetro) return "Rango Máximo";
        return "Valor";
    }
    
    private String getEtiquetaValorSecundario() {
        if (componente instanceof Resistencia) return "Potencia";
        if (componente instanceof Led) return "Corriente Máxima";
        if (componente instanceof FuenteVoltaje) return "Frecuencia";
        return null;
    }
    
    private double getValorSecundario() {
        if (componente instanceof Resistencia) {
            return ((Resistencia) componente).getPotencia();
        }
        if (componente instanceof Led) {
            return ((Led) componente).getCorrienteMaxima() * 1000; // Convertir a mA
        }
        if (componente instanceof FuenteVoltaje) {
            return ((FuenteVoltaje) componente).getFrecuencia();
        }
        return 0.0;
    }
    
    private String getUnidadSecundaria() {
        if (componente instanceof Resistencia) return "W";
        if (componente instanceof Led) return "mA";
        if (componente instanceof FuenteVoltaje) return "Hz";
        return "";
    }
    
    private void agregarInformacionEspecifica(JPanel panel, GridBagConstraints gbc) {
        int filaActual = (campoValorSecundario != null) ? 4 : 3;
        
        if (componente instanceof FuenteVoltaje) {
            FuenteVoltaje fuente = (FuenteVoltaje) componente;
            
            gbc.gridx = 0; gbc.gridy = filaActual;
            panel.add(new JLabel("Tipo:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 2;
            
            JComboBox<String> comboTipo = new JComboBox<>(new String[]{"DC", "AC"});
            comboTipo.setSelectedItem(fuente.isEsAC() ? "AC" : "DC");
            panel.add(comboTipo, gbc);
            
            gbc.gridwidth = 1;
        }
        
        if (componente instanceof Led) {
            Led led = (Led) componente;
            
            gbc.gridx = 0; gbc.gridy = filaActual;
            panel.add(new JLabel("Color:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 2;
            
            JComboBox<String> comboColor = new JComboBox<>(
                new String[]{"rojo", "verde", "azul", "amarillo", "blanco", "naranja"});
            comboColor.setSelectedItem(led.getColor());
            panel.add(comboColor, gbc);
            
            gbc.gridwidth = 1;
        }
    }
    
    private boolean validarYAplicarCambios() {
        try {
            // Validar nombre
            String nuevoNombre = campoNombre.getText().trim();
            if (nuevoNombre.isEmpty()) {
                mostrarError("El nombre no puede estar vacío");
                return false;
            }
            
            // Validar valor principal
            double valorPrincipal = Double.parseDouble(campoValorPrincipal.getText());
            if (valorPrincipal <= 0) {
                mostrarError("El valor principal debe ser mayor que cero");
                return false;
            }
            
            // Validar valor secundario si existe
            double valorSecundario = 0;
            if (campoValorSecundario != null) {
                valorSecundario = Double.parseDouble(campoValorSecundario.getText());
                if (valorSecundario <= 0) {
                    mostrarError("El valor secundario debe ser mayor que cero");
                    return false;
                }
            }
            
            // Aplicar cambios
            componente.setNombre(nuevoNombre);
            componente.setValorPrincipal(valorPrincipal);
            
            // Aplicar valor secundario específico
            if (componente instanceof Resistencia && campoValorSecundario != null) {
                ((Resistencia) componente).setPotencia(valorSecundario);
            } else if (componente instanceof Led && campoValorSecundario != null) {
                ((Led) componente).setCorrienteMaxima(valorSecundario / 1000); // Convertir de mA a A
            } else if (componente instanceof FuenteVoltaje && campoValorSecundario != null) {
                ((FuenteVoltaje) componente).setFrecuencia(valorSecundario);
            }
            
            return true;
            
        } catch (NumberFormatException e) {
            mostrarError("Por favor ingrese valores numéricos válidos");
            return false;
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isAceptado() {
        return aceptado;
    }
}
