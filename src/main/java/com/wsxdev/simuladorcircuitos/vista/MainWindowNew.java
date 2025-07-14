package com.wsxdev.simuladorcircuitos.vista;

import com.wsxdev.simuladorcircuitos.controlador.CircuitoControlador;
import com.wsxdev.simuladorcircuitos.modelo.Circuito;
import com.wsxdev.simuladorcircuitos.persistencia.Circuitos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Ventana principal del simulador de circuitos con interfaz moderna
 */
public class MainWindowNew extends JFrame {
    
    // Componentes principales
    private PanelCircuito panelCircuito;
    private CircuitoControlador controlador;
    private PanelResultados panelResultados;
    
    // Componentes de la interfaz
    private JToolBar toolBar;
    private JPanel panelLateral;
    private JMenuBar menuBar;
    private JLabel statusLabel;
    
    // Botones de la barra de herramientas
    private JButton btnNuevo, btnAbrir, btnGuardar;
    private JButton btnConectar, btnSimular, btnDetener;
    private JButton btnLimpiar;
    
    // Variables de estado
    private File archivoActual = null;
    private boolean circuitoModificado = false;
    
    public MainWindowNew() {
        initializeFrame();
        createMenuBar();
        createToolBar();
        createMainContent();
        createStatusBar();
        setupKeyBindings();
        
        // Configuración final
        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Inicializar controlador
        controlador = new CircuitoControlador();
        nuevoCircuito();
    }
    
    private void initializeFrame() {
        setTitle("Simulador de Circuitos Eléctricos v2.0");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        
        // Configurar icono de la aplicación
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/png/iconMenus/home.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono de la aplicación");
        }
        
        // Manejar cierre de ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                salirAplicacion();
            }
        });
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic(KeyEvent.VK_A);
        
        menuArchivo.add(createMenuItem("Nuevo", "ctrl N", this::nuevoCircuito));
        menuArchivo.add(createMenuItem("Abrir...", "ctrl O", this::abrirCircuito));
        menuArchivo.addSeparator();
        menuArchivo.add(createMenuItem("Guardar", "ctrl S", this::guardarCircuito));
        menuArchivo.add(createMenuItem("Guardar como...", "ctrl shift S", this::guardarCircuitoComo));
        menuArchivo.addSeparator();
        menuArchivo.add(createMenuItem("Salir", "ctrl Q", this::salirAplicacion));
        
        // Menú Editar
        JMenu menuEditar = new JMenu("Editar");
        menuEditar.setMnemonic(KeyEvent.VK_E);
        
        menuEditar.add(createMenuItem("Limpiar Circuito", "ctrl L", this::limpiarCircuito));
        menuEditar.addSeparator();
        menuEditar.add(createMenuItem("Modo Conexión", "ctrl C", this::toggleModoConexion));
        
        // Menú Simulación
        JMenu menuSimulacion = new JMenu("Simulación");
        menuSimulacion.setMnemonic(KeyEvent.VK_S);
        
        menuSimulacion.add(createMenuItem("Ejecutar Simulación", "F5", this::ejecutarSimulacion));
        menuSimulacion.add(createMenuItem("Detener Simulación", "F6", this::detenerSimulacion));
        menuSimulacion.addSeparator();
        menuSimulacion.add(createMenuItem("Mostrar Resultados", "F7", this::mostrarResultados));
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic(KeyEvent.VK_Y);
        
        menuAyuda.add(createMenuItem("Atajos de Teclado", "F1", this::mostrarAyuda));
        menuAyuda.add(createMenuItem("Acerca de...", null, this::mostrarAcercaDe));
        
        menuBar.add(menuArchivo);
        menuBar.add(menuEditar);
        menuBar.add(menuSimulacion);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);
    }
    
    private JMenuItem createMenuItem(String text, String accelerator, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        if (accelerator != null) {
            item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
        }
        item.addActionListener(e -> action.run());
        return item;
    }
    
    private void createToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        
        // Botones de archivo
        btnNuevo = createToolBarButton("Nuevo", "/img/png/iconMenus/file-edit.png", this::nuevoCircuito);
        btnAbrir = createToolBarButton("Abrir", "/img/png/iconMenus/folder.png", this::abrirCircuito);
        btnGuardar = createToolBarButton("Guardar", "/img/png/iconMenus/file-edit.png", this::guardarCircuito);
        
        toolBar.add(btnNuevo);
        toolBar.add(btnAbrir);
        toolBar.add(btnGuardar);
        toolBar.addSeparator();
        
        // Botones de edición
        btnConectar = createToolBarButton("Conectar (C)", "/img/png/componentes/cable.png", this::toggleModoConexion);
        btnLimpiar = createToolBarButton("Limpiar", "/img/png/componentes/trash.png", this::limpiarCircuito);
        
        toolBar.add(btnConectar);
        toolBar.add(btnLimpiar);
        toolBar.addSeparator();
        
        // Botones de simulación
        btnSimular = createToolBarButton("Simular (F5)", "/img/png/iconMenus/home.png", this::ejecutarSimulacion);
        btnDetener = createToolBarButton("Detener (F6)", "/img/png/componentes/trash.png", this::detenerSimulacion);
        
        toolBar.add(btnSimular);
        toolBar.add(btnDetener);
        
        btnDetener.setEnabled(false);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private JButton createToolBarButton(String tooltip, String iconPath, Runnable action) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setFocusable(false);
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            button.setText(tooltip.split(" ")[0]); // Usar texto si no hay icono
        }
        
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private void createMainContent() {
        // Panel principal con split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.0);
        
        // Panel lateral izquierdo
        createLateralPanel();
        splitPane.setLeftComponent(panelLateral);
        
        // Panel central con el circuito
        panelCircuito = new PanelCircuito();
        panelCircuito.setBorder(BorderFactory.createLoweredBevelBorder());
        panelCircuito.setMainWindow(this); // Para notificar cambios
        
        JScrollPane scrollPane = new JScrollPane(panelCircuito);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        splitPane.setRightComponent(scrollPane);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void createLateralPanel() {
        panelLateral = new JPanel(new BorderLayout());
        panelLateral.setPreferredSize(new Dimension(250, 0));
        panelLateral.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titulo = new JLabel("Componentes");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelLateral.add(titulo, BorderLayout.NORTH);
        
        // Panel de componentes
        JPanel panelComponentes = new JPanel(new GridLayout(0, 1, 5, 5));
        panelComponentes.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Botones de componentes
        panelComponentes.add(createComponentButton("Fuente de Voltaje", "fuenteV"));
        panelComponentes.add(createComponentButton("Resistencia", "resistencia"));
        panelComponentes.add(createComponentButton("LED", "led"));
        panelComponentes.add(createComponentButton("Voltímetro", "voltimetro"));
        panelComponentes.add(createComponentButton("Amperímetro", "amperimetro"));
        
        JScrollPane scrollComponentes = new JScrollPane(panelComponentes);
        scrollComponentes.setBorder(null);
        panelLateral.add(scrollComponentes, BorderLayout.CENTER);
        
        // Panel de información
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información"));
        
        JTextArea areaInfo = new JTextArea(5, 20);
        areaInfo.setEditable(false);
        areaInfo.setOpaque(false);
        areaInfo.setText("Haz clic en un componente y luego clic en el panel para agregarlo.\n\n" +
                        "Atajos:\n" +
                        "• C - Modo conexión\n" +
                        "• R - Rotar componente\n" +
                        "• Del - Eliminar\n" +
                        "• F5 - Simular");
        
        panelInfo.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        panelLateral.add(panelInfo, BorderLayout.SOUTH);
    }
    
    private JButton createComponentButton(String nombre, String tipo) {
        JButton button = new JButton(nombre);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/png/componentes/" + tipo + ".png"));
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Sin icono si no se encuentra
        }
        
        button.addActionListener(e -> {
            panelCircuito.setTipoComponenteSeleccionado(tipo);
            actualizarStatus("Selecciona dónde colocar " + nombre + " - Haz clic en el panel");
        });
        
        return button;
    }
    
    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        
        statusLabel = new JLabel("Listo");
        statusLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupKeyBindings() {
        JRootPane rootPane = getRootPane();
        
        // F5 para simular
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "simular");
        rootPane.getActionMap().put("simular", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarSimulacion();
            }
        });
        
        // C para modo conexión
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "conectar");
        rootPane.getActionMap().put("conectar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleModoConexion();
            }
        });
    }
    
    // Métodos de acción
    private void nuevoCircuito() {
        if (confirmarDescartarCambios()) {
            panelCircuito.limpiarCircuito();
            archivoActual = null;
            circuitoModificado = false;
            actualizarTitulo();
            actualizarStatus("Nuevo circuito creado");
        }
    }
    
    private void abrirCircuito() {
        if (!confirmarDescartarCambios()) return;
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de Circuito (*.cir)", "cir"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = fileChooser.getSelectedFile();
                Circuito circuito = controlador.cargarCircuito(archivo.getAbsolutePath());
                panelCircuito.setCircuito(circuito);
                archivoActual = archivo;
                circuitoModificado = false;
                actualizarTitulo();
                actualizarStatus("Circuito cargado: " + archivo.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al cargar el circuito: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void guardarCircuito() {
        if (archivoActual == null) {
            guardarCircuitoComo();
        } else {
            try {
                controlador.guardarCircuito(panelCircuito.getCircuito(), archivoActual.getAbsolutePath());
                circuitoModificado = false;
                actualizarTitulo();
                actualizarStatus("Circuito guardado");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar el circuito: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void guardarCircuitoComo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de Circuito (*.cir)", "cir"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = fileChooser.getSelectedFile();
                if (!archivo.getName().endsWith(".cir")) {
                    archivo = new File(archivo.getAbsolutePath() + ".cir");
                }
                
                controlador.guardarCircuito(panelCircuito.getCircuito(), archivo.getAbsolutePath());
                archivoActual = archivo;
                circuitoModificado = false;
                actualizarTitulo();
                actualizarStatus("Circuito guardado como: " + archivo.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar el circuito: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarCircuito() {
        if (confirmarAccion("¿Está seguro de que desea limpiar el circuito?")) {
            panelCircuito.limpiarCircuito();
            circuitoModificado = true;
            actualizarTitulo();
            actualizarStatus("Circuito limpiado");
        }
    }
    
    private void toggleModoConexion() {
        panelCircuito.toggleModoConexion();
        boolean modoConexion = panelCircuito.isModoConexion();
        btnConectar.setSelected(modoConexion);
        actualizarStatus(modoConexion ? "Modo conexión activado" : "Modo conexión desactivado");
    }
    
    private void ejecutarSimulacion() {
        try {
            btnSimular.setEnabled(false);
            btnDetener.setEnabled(true);
            actualizarStatus("Ejecutando simulación...");
            
            // Ejecutar simulación en hilo separado
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controlador.simularCircuito(panelCircuito.getCircuito());
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get(); // Obtener resultado y verificar excepciones
                        panelCircuito.actualizarVisualizacion();
                        actualizarStatus("Simulación completada");
                        
                        // Mostrar resultados automáticamente
                        mostrarResultados();
                        
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(MainWindowNew.this, 
                            "Error en la simulación: " + e.getMessage(),
                            "Error de Simulación", JOptionPane.ERROR_MESSAGE);
                        actualizarStatus("Error en la simulación");
                    } finally {
                        btnSimular.setEnabled(true);
                        btnDetener.setEnabled(false);
                    }
                }
            };
            
            worker.execute();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al iniciar la simulación: " + e.getMessage(),
                "Error de Simulación", JOptionPane.ERROR_MESSAGE);
            btnSimular.setEnabled(true);
            btnDetener.setEnabled(false);
        }
    }
    
    private void detenerSimulacion() {
        btnSimular.setEnabled(true);
        btnDetener.setEnabled(false);
        actualizarStatus("Simulación detenida");
    }
    
    private void mostrarResultados() {
        if (panelResultados == null) {
            panelResultados = new PanelResultados();
        }
        panelResultados.actualizarResultados(panelCircuito.getCircuito());
        panelResultados.setVisible(true);
    }
    
    private void mostrarAyuda() {
        String ayuda = "Atajos de Teclado:\n\n" +
                      "Ctrl+N - Nuevo circuito\n" +
                      "Ctrl+O - Abrir circuito\n" +
                      "Ctrl+S - Guardar circuito\n" +
                      "Ctrl+L - Limpiar circuito\n" +
                      "C - Modo conexión\n" +
                      "R - Rotar componente seleccionado\n" +
                      "Del - Eliminar componente seleccionado\n" +
                      "F5 - Ejecutar simulación\n" +
                      "F6 - Detener simulación\n" +
                      "F7 - Mostrar resultados\n" +
                      "Esc - Cancelar operación actual\n\n" +
                      "Ratón:\n" +
                      "Clic derecho - Menú contextual\n" +
                      "Doble clic - Configurar componente\n" +
                      "Arrastrar - Mover componente";
        
        JOptionPane.showMessageDialog(this, ayuda, "Ayuda - Atajos de Teclado", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarAcercaDe() {
        String acercaDe = "Simulador de Circuitos Eléctricos v2.0\n\n" +
                         "Un simulador interactivo para diseño y análisis\n" +
                         "de circuitos eléctricos básicos.\n\n" +
                         "Características:\n" +
                         "• Diseño visual de circuitos\n" +
                         "• Conexiones dinámicas\n" +
                         "• Simulación en tiempo real\n" +
                         "• Análisis de resultados\n" +
                         "• Interfaz moderna con FlatLaf\n\n" +
                         "Desarrollado con Java Swing y Apache Commons Math";
        
        JOptionPane.showMessageDialog(this, acercaDe, "Acerca de", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void salirAplicacion() {
        if (confirmarDescartarCambios()) {
            System.exit(0);
        }
    }
    
    // Métodos auxiliares
    private boolean confirmarDescartarCambios() {
        if (circuitoModificado) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea guardar los cambios antes de continuar?",
                "Cambios sin guardar",
                JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                guardarCircuito();
                return !circuitoModificado; // Solo continuar si se guardó exitosamente
            } else if (respuesta == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }
    
    private boolean confirmarAccion(String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    private void actualizarTitulo() {
        StringBuilder titulo = new StringBuilder("Simulador de Circuitos Eléctricos v2.0");
        
        if (archivoActual != null) {
            titulo.append(" - ").append(archivoActual.getName());
        } else {
            titulo.append(" - Nuevo Circuito");
        }
        
        if (circuitoModificado) {
            titulo.append(" *");
        }
        
        setTitle(titulo.toString());
    }
    
    private void actualizarStatus(String mensaje) {
        statusLabel.setText(mensaje);
    }
    
    public void marcarComoModificado() {
        circuitoModificado = true;
        actualizarTitulo();
    }
}
