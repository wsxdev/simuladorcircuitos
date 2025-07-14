package com.wsxdev.simuladorcircuitos.vista;

import com.wsxdev.simuladorcircuitos.modelo.Circuito;
import com.wsxdev.simuladorcircuitos.modelo.Componente;
import com.wsxdev.simuladorcircuitos.modelo.Conexion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel para mostrar los resultados de la simulación
 */
public class PanelResultados extends JDialog {
    
    private JTextArea areaResultados;
    private JTable tablaComponentes;
    private JTable tablaConexiones;
    private DefaultTableModel modeloComponentes;
    private DefaultTableModel modeloConexiones;
    
    public PanelResultados() {
        initializeDialog();
        createComponents();
        layoutComponents();
    }
    
    private void initializeDialog() {
        setTitle("Resultados de Simulación");
        setModal(false);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }
    
    private void createComponents() {
        // Área de texto para resultados generales
        areaResultados = new JTextArea(8, 50);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaResultados.setBackground(getBackground());
        
        // Tabla de componentes
        String[] columnasComponentes = {"Componente", "Tipo", "Valor", "Unidad", "Estado"};
        modeloComponentes = new DefaultTableModel(columnasComponentes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaComponentes = new JTable(modeloComponentes);
        tablaComponentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Tabla de conexiones
        String[] columnasConexiones = {"Conexión", "Corriente (A)", "Voltaje (V)", "Estado"};
        modeloConexiones = new DefaultTableModel(columnasConexiones, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaConexiones = new JTable(modeloConexiones);
        tablaConexiones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Panel superior con resultados generales
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Resumen de Simulación"));
        
        JScrollPane scrollResultados = new JScrollPane(areaResultados);
        scrollResultados.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panelSuperior.add(scrollResultados, BorderLayout.CENTER);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con tablas en pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña de componentes
        JScrollPane scrollComponentes = new JScrollPane(tablaComponentes);
        tabbedPane.addTab("Componentes", scrollComponentes);
        
        // Pestaña de conexiones
        JScrollPane scrollConexiones = new JScrollPane(tablaConexiones);
        tabbedPane.addTab("Conexiones", scrollConexiones);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnExportar = new JButton("Exportar");
        btnExportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportarResultados();
            }
        });
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        panelBotones.add(btnExportar);
        panelBotones.add(btnCerrar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    public void actualizarResultados(Circuito circuito) {
        if (circuito == null) {
            areaResultados.setText("No hay circuito para mostrar resultados.");
            return;
        }
        
        // Actualizar resumen
        StringBuilder resumen = new StringBuilder();
        resumen.append("=== RESULTADOS DE SIMULACIÓN ===\n\n");
        resumen.append("Circuito: ").append(circuito.getNombre()).append("\n");
        resumen.append("Componentes: ").append(circuito.getComponentes().size()).append("\n");
        resumen.append("Conexiones: ").append(circuito.getConexiones().size()).append("\n\n");
        resumen.append("Estado: Simulación completada exitosamente\n");
        resumen.append("Fecha: ").append(new java.util.Date()).append("\n");
        
        areaResultados.setText(resumen.toString());
        
        // Actualizar tabla de componentes
        modeloComponentes.setRowCount(0);
        for (Componente componente : circuito.getComponentes()) {
            Object[] fila = {
                componente.getNombre(),
                componente.getTipo(),
                String.format("%.2f", componente.getValorPrincipal()),
                componente.getUnidad(),
                "Activo"
            };
            modeloComponentes.addRow(fila);
        }
        
        // Actualizar tabla de conexiones
        modeloConexiones.setRowCount(0);
        for (Conexion conexion : circuito.getConexiones()) {
            Object[] fila = {
                String.format("%s → %s", 
                    conexion.getPuntoInicio().getNombre(),
                    conexion.getPuntoFin().getNombre()),
                String.format("%.3f", conexion.getCorriente()),
                String.format("%.3f", conexion.getVoltaje()),
                conexion.isActiva() ? "Activa" : "Inactiva"
            };
            modeloConexiones.addRow(fila);
        }
    }
    
    private void exportarResultados() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de Texto (*.txt)", "txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File archivo = fileChooser.getSelectedFile();
                if (!archivo.getName().endsWith(".txt")) {
                    archivo = new java.io.File(archivo.getAbsolutePath() + ".txt");
                }
                
                try (java.io.PrintWriter writer = new java.io.PrintWriter(archivo)) {
                    // Exportar resumen
                    writer.println(areaResultados.getText());
                    writer.println("\n=== DETALLE DE COMPONENTES ===");
                    
                    // Exportar tabla de componentes
                    for (int i = 0; i < modeloComponentes.getRowCount(); i++) {
                        for (int j = 0; j < modeloComponentes.getColumnCount(); j++) {
                            writer.print(modeloComponentes.getValueAt(i, j));
                            if (j < modeloComponentes.getColumnCount() - 1) {
                                writer.print("\t");
                            }
                        }
                        writer.println();
                    }
                    
                    writer.println("\n=== DETALLE DE CONEXIONES ===");
                    
                    // Exportar tabla de conexiones
                    for (int i = 0; i < modeloConexiones.getRowCount(); i++) {
                        for (int j = 0; j < modeloConexiones.getColumnCount(); j++) {
                            writer.print(modeloConexiones.getValueAt(i, j));
                            if (j < modeloConexiones.getColumnCount() - 1) {
                                writer.print("\t");
                            }
                        }
                        writer.println();
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Resultados exportados exitosamente a: " + archivo.getName(),
                    "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al exportar resultados: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
