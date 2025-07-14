package com.wsxdev.simuladorcircuitos.vista;

import com.wsxdev.simuladorcircuitos.modelo.*;
import com.wsxdev.simuladorcircuitos.simulacion.SimuladorCircuito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel principal para el diseño y simulación de circuitos
 */
public class PanelCircuito extends JPanel {
    
    private Circuito circuito;
    private SimuladorCircuito simulador;
    private List<ComponenteVisual> componentesVisuales;
    private List<ConexionVisual> conexionesVisuales;
    
    // Variables para interacción
    private JPopupMenu menuComponentes;
    private int lastClickX, lastClickY;
    private ComponenteVisual componenteArrastrado = null;
    private ComponenteVisual componenteSeleccionado = null;
    private int offsetX, offsetY;
    
    // Variables para conexiones dinámicas
    private boolean modoConexion = false;
    private PuntoConexion puntoInicioConexion = null;
    private Point puntoMouseActual = null;
    
    // Configuración visual
    private static final int RADIO_PUNTO_CONEXION = 6;
    private static final int TOLERANCIA_SELECCION = 10;
    private static final Color COLOR_PUNTO_LIBRE = Color.GREEN;
    private static final Color COLOR_PUNTO_CONECTADO = Color.RED;
    private static final Color COLOR_PUNTO_SELECCIONADO = Color.YELLOW;
    
    // Variables adicionales para integración con MainWindow
    private MainWindowNew mainWindow;
    private String tipoComponenteSeleccionado = null;
    
    public PanelCircuito() {
        initializePanel();
        setupEventListeners();
    }
    
    private void initializePanel() {
        setBackground(Color.WHITE);
        setLayout(null);
        setFocusable(true);
        
        // Inicializar modelo
        circuito = new Circuito("Circuito Principal");
        simulador = new SimuladorCircuito(circuito);
        
        // Inicializar listas visuales
        componentesVisuales = new ArrayList<>();
        conexionesVisuales = new ArrayList<>();
        
        // Crear menú contextual
        menuComponentes = crearMenuContextual();
    }
    
    private void setupEventListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e);
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });
    }
    
    private void handleMousePressed(MouseEvent e) {
        requestFocusInWindow();
        
        if (e.isPopupTrigger()) {
            mostrarMenuContextual(e);
            return;
        }
        
        // Si hay un tipo de componente seleccionado, agregarlo
        if (tipoComponenteSeleccionado != null) {
            agregarComponente(tipoComponenteSeleccionado, e.getX() - 24, e.getY() - 24);
            tipoComponenteSeleccionado = null; // Limpiar selección
            if (mainWindow != null) {
                mainWindow.marcarComoModificado();
            }
            return;
        }
        
        if (modoConexion) {
            handleModoConexion(e);
            return;
        }
        
        // Verificar si se hizo clic en un componente
        ComponenteVisual componenteClickeado = getComponenteEnPosicion(e.getX(), e.getY());
        if (componenteClickeado != null) {
            seleccionarComponente(componenteClickeado);
            componenteArrastrado = componenteClickeado;
            offsetX = e.getX() - componenteClickeado.getX();
            offsetY = e.getY() - componenteClickeado.getY();
        } else {
            deseleccionarTodos();
        }
        
        repaint();
    }
    
    private void handleMouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            mostrarMenuContextual(e);
            return;
        }
        
        componenteArrastrado = null;
    }
    
    private void handleMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && componenteSeleccionado != null) {
            // Doble clic para configurar componente
            mostrarDialogoConfiguracion(componenteSeleccionado);
        }
    }
    
    private void handleMouseDragged(MouseEvent e) {
        if (componenteArrastrado != null) {
            int nuevaX = e.getX() - offsetX;
            int nuevaY = e.getY() - offsetY;
            
            // Mantener dentro de los límites del panel
            nuevaX = Math.max(0, Math.min(getWidth() - 48, nuevaX));
            nuevaY = Math.max(0, Math.min(getHeight() - 48, nuevaY));
            
            componenteArrastrado.setPosition(nuevaX, nuevaY);
            actualizarConexionesVisuales();
            repaint();
        }
    }
    
    private void handleMouseMoved(MouseEvent e) {
        if (modoConexion) {
            puntoMouseActual = e.getPoint();
            repaint();
        }
    }
    
    private void handleKeyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_R:
                if (componenteSeleccionado != null) {
                    rotarComponenteSeleccionado();
                }
                break;
            case KeyEvent.VK_DELETE:
                if (componenteSeleccionado != null) {
                    eliminarComponenteSeleccionado();
                }
                break;
            case KeyEvent.VK_C:
                toggleModoConexion();
                break;
            case KeyEvent.VK_ESCAPE:
                cancelarOperacion();
                break;
            case KeyEvent.VK_SPACE:
                ejecutarSimulacion();
                break;
        }
    }
    
    private void handleModoConexion(MouseEvent e) {
        PuntoConexion puntoClickeado = getPuntoConexionEnPosicion(e.getX(), e.getY());
        
        if (puntoClickeado != null) {
            if (puntoInicioConexion == null) {
                // Primer punto seleccionado
                puntoInicioConexion = puntoClickeado;
                JOptionPane.showMessageDialog(this, 
                    "Punto seleccionado. Haz clic en otro punto para conectar.", 
                    "Modo Conexión", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Segundo punto seleccionado - crear conexión
                if (puntoInicioConexion.puedeConectarCon(puntoClickeado)) {
                    crearConexion(puntoInicioConexion, puntoClickeado);
                    puntoInicioConexion = null;
                    modoConexion = false;
                    setCursor(Cursor.getDefaultCursor());
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se puede conectar estos puntos.", 
                        "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                    puntoInicioConexion = null;
                }
            }
        }
        
        repaint();
    }
    
    private void mostrarMenuContextual(MouseEvent e) {
        lastClickX = e.getX();
        lastClickY = e.getY();
        
        // Verificar si se hizo clic en un componente o conexión
        ComponenteVisual componente = getComponenteEnPosicion(lastClickX, lastClickY);
        ConexionVisual conexion = getConexionEnPosicion(lastClickX, lastClickY);
        
        JPopupMenu menu = new JPopupMenu();
        
        if (componente != null) {
            // Menú para componente
            menu.add(createMenuItem("Configurar", e2 -> mostrarDialogoConfiguracion(componente)));
            menu.add(createMenuItem("Rotar", e2 -> rotarComponente(componente)));
            menu.addSeparator();
            menu.add(createMenuItem("Eliminar", e2 -> eliminarComponente(componente)));
        } else if (conexion != null) {
            // Menú para conexión
            menu.add(createMenuItem("Eliminar Conexión", e2 -> eliminarConexion(conexion)));
        } else {
            // Menú para espacio vacío
            agregarMenuComponentes(menu);
            menu.addSeparator();
            menu.add(createMenuItem("Modo Conexión", e2 -> toggleModoConexion()));
            menu.add(createMenuItem("Simular Circuito", e2 -> ejecutarSimulacion()));
        }
        
        menu.show(this, lastClickX, lastClickY);
    }
    
    private JMenuItem createMenuItem(String text, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        return item;
    }
    
    private JPopupMenu crearMenuContextual() {
        JPopupMenu menu = new JPopupMenu();
        agregarMenuComponentes(menu);
        return menu;
    }
    
    private void agregarMenuComponentes(JPopupMenu menu) {
        menu.add(createMenuItem("Fuente de Voltaje", e -> agregarComponente("fuenteV")));
        menu.add(createMenuItem("Resistencia", e -> agregarComponente("resistencia")));
        menu.add(createMenuItem("LED", e -> agregarComponente("led")));
        menu.addSeparator();
        menu.add(createMenuItem("Voltímetro", e -> agregarComponente("voltimetro")));
        menu.add(createMenuItem("Amperímetro", e -> agregarComponente("amperimetro")));
    }
    
    // Métodos de componentes
    private void agregarComponente(String tipo) {
        Componente nuevoComponente = crearComponente(tipo, lastClickX, lastClickY);
        if (nuevoComponente != null) {
            circuito.agregarComponente(nuevoComponente);
            ComponenteVisual nuevoVisual = new ComponenteVisual(nuevoComponente);
            componentesVisuales.add(nuevoVisual);
            repaint();
        }
    }
    
    private void agregarComponente(String tipo, int x, int y) {
        Componente nuevoComponente = crearComponente(tipo, x, y);
        if (nuevoComponente != null) {
            circuito.agregarComponente(nuevoComponente);
            ComponenteVisual nuevoVisual = new ComponenteVisual(nuevoComponente);
            componentesVisuales.add(nuevoVisual);
            repaint();
        }
    }
    
    private Componente crearComponente(String tipo, int x, int y) {
        switch (tipo) {
            case "fuenteV":
                return new FuenteVoltaje(x, y, 0);
            case "resistencia":
                return new Resistencia(x, y, 0);
            case "led":
                return new Led(x, y, 0);
            case "voltimetro":
                return new Voltimetro(x, y, 0);
            case "amperimetro":
                return new Amperimetro(x, y, 0);
            default:
                return null;
        }
    }
    
    private void seleccionarComponente(ComponenteVisual componente) {
        deseleccionarTodos();
        componenteSeleccionado = componente;
        componente.setSeleccionado(true);
    }
    
    private void deseleccionarTodos() {
        componenteSeleccionado = null;
        for (ComponenteVisual cv : componentesVisuales) {
            cv.setSeleccionado(false);
        }
    }
    
    private void rotarComponenteSeleccionado() {
        if (componenteSeleccionado != null) {
            rotarComponente(componenteSeleccionado);
        }
    }
    
    private void rotarComponente(ComponenteVisual componente) {
        componente.rotar(90);
        actualizarConexionesVisuales();
        repaint();
    }
    
    private void eliminarComponenteSeleccionado() {
        if (componenteSeleccionado != null) {
            eliminarComponente(componenteSeleccionado);
        }
    }
    
    private void eliminarComponente(ComponenteVisual componente) {
        // Eliminar conexiones asociadas
        List<ConexionVisual> conexionesAEliminar = new ArrayList<>();
        for (ConexionVisual cv : conexionesVisuales) {
            if (cv.involucraComponente(componente.getComponente())) {
                conexionesAEliminar.add(cv);
            }
        }
        
        for (ConexionVisual cv : conexionesAEliminar) {
            eliminarConexion(cv);
        }
        
        // Eliminar componente
        circuito.removerComponente(componente.getComponente());
        componentesVisuales.remove(componente);
        
        if (componenteSeleccionado == componente) {
            componenteSeleccionado = null;
        }
        
        repaint();
    }
    
    // Métodos de conexión
    
    private void crearConexion(PuntoConexion punto1, PuntoConexion punto2) {
        Conexion nuevaConexion = circuito.conectarPuntos(punto1, punto2);
        if (nuevaConexion != null) {
            ConexionVisual nuevaVisual = new ConexionVisual(nuevaConexion);
            conexionesVisuales.add(nuevaVisual);
            repaint();
        }
    }
    
    private void eliminarConexion(ConexionVisual conexion) {
        circuito.removerConexion(conexion.getConexion());
        conexionesVisuales.remove(conexion);
        repaint();
    }
    
    private void actualizarConexionesVisuales() {
        for (ConexionVisual cv : conexionesVisuales) {
            cv.actualizar();
        }
    }
    
    private void cancelarOperacion() {
        modoConexion = false;
        puntoInicioConexion = null;
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }
    
    // Métodos de simulación
    private void ejecutarSimulacion() {
        if (!circuito.validar()) {
            JOptionPane.showMessageDialog(this, 
                "El circuito no es válido. Necesita al menos una fuente de voltaje y conexiones.", 
                "Error de Simulación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SimuladorCircuito.ResultadosSimulacion resultados = simulador.simular();
        
        if (resultados.isExitoso()) {
            JOptionPane.showMessageDialog(this, 
                "Simulación exitosa: " + resultados.getMensaje(), 
                "Simulación", JOptionPane.INFORMATION_MESSAGE);
            actualizarVisualizacionSimulacion();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error en simulación: " + resultados.getMensaje(), 
                "Error de Simulación", JOptionPane.ERROR_MESSAGE);
        }
        
        repaint();
    }
    
    private void actualizarVisualizacionSimulacion() {
        // Actualizar colores de conexiones basado en la corriente
        for (ConexionVisual cv : conexionesVisuales) {
            cv.actualizar();
        }
    }
    
    // Métodos de configuración
    private void mostrarDialogoConfiguracion(ComponenteVisual componente) {
        ConfiguracionComponenteDialog dialog = new ConfiguracionComponenteDialog(
            SwingUtilities.getWindowAncestor(this), componente.getComponente());
        dialog.setVisible(true);
        
        if (dialog.isAceptado()) {
            // El diálogo ya modificó el componente
            repaint();
            if (mainWindow != null) {
                mainWindow.marcarComoModificado();
            }
        }
    }
    
    // Métodos de búsqueda
    private ComponenteVisual getComponenteEnPosicion(int x, int y) {
        for (ComponenteVisual cv : componentesVisuales) {
            if (cv.contiene(x, y)) {
                return cv;
            }
        }
        return null;
    }
    
    private ConexionVisual getConexionEnPosicion(int x, int y) {
        for (ConexionVisual cv : conexionesVisuales) {
            if (cv.contiene(x, y, TOLERANCIA_SELECCION)) {
                return cv;
            }
        }
        return null;
    }
    
    private PuntoConexion getPuntoConexionEnPosicion(int x, int y) {
        for (ComponenteVisual cv : componentesVisuales) {
            for (PuntoConexion punto : cv.getComponente().getPuntosConexion()) {
                if (punto.contiene(x, y, RADIO_PUNTO_CONEXION)) {
                    return punto;
                }
            }
        }
        return null;
    }
    
    // Métodos de acceso público
    public void limpiarCircuito() {
        circuito.limpiar();
        componentesVisuales.clear();
        conexionesVisuales.clear();
        componenteSeleccionado = null;
        componenteArrastrado = null;
        modoConexion = false;
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }
    
    public Circuito getCircuito() {
        return circuito;
    }
    
    public void setCircuito(Circuito nuevoCircuito) {
        this.circuito = nuevoCircuito;
        this.simulador = new SimuladorCircuito(circuito);
        // Reconstruir visualización
        // TODO: Implementar carga desde modelo
    }
    
    // Métodos para integración con MainWindow
    public void setMainWindow(MainWindowNew mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    public void setTipoComponenteSeleccionado(String tipo) {
        this.tipoComponenteSeleccionado = tipo;
    }
    
    public boolean isModoConexion() {
        return modoConexion;
    }
    
    public void toggleModoConexion() {
        modoConexion = !modoConexion;
        if (!modoConexion) {
            puntoInicioConexion = null;
            puntoMouseActual = null;
        }
        repaint();
    }
    
    public void actualizarVisualizacion() {
        // Actualizar colores de las conexiones basándose en la simulación
        for (ConexionVisual conexionVisual : conexionesVisuales) {
            conexionVisual.getConexion().actualizarColor();
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Habilitar antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        // Dibujar conexiones primero (para que estén debajo de los componentes)
        for (ConexionVisual cv : conexionesVisuales) {
            cv.dibujar(g2d);
        }
        
        // Dibujar componentes
        for (ComponenteVisual cv : componentesVisuales) {
            cv.dibujar(g2d, this);
        }
        
        // Dibujar puntos de conexión si está en modo conexión
        if (modoConexion) {
            dibujarPuntosConexion(g2d);
        }
        
        // Dibujar línea temporal de conexión
        if (modoConexion && puntoInicioConexion != null && puntoMouseActual != null) {
            dibujarLineaTemporalConexion(g2d);
        }
        
        g2d.dispose();
    }
    
    private void dibujarPuntosConexion(Graphics2D g2d) {
        for (ComponenteVisual cv : componentesVisuales) {
            for (PuntoConexion punto : cv.getComponente().getPuntosConexion()) {
                Color color;
                if (punto == puntoInicioConexion) {
                    color = COLOR_PUNTO_SELECCIONADO;
                } else if (punto.isConectado()) {
                    color = COLOR_PUNTO_CONECTADO;
                } else {
                    color = COLOR_PUNTO_LIBRE;
                }
                
                g2d.setColor(color);
                g2d.fillOval(punto.getX() - RADIO_PUNTO_CONEXION/2, 
                           punto.getY() - RADIO_PUNTO_CONEXION/2, 
                           RADIO_PUNTO_CONEXION, RADIO_PUNTO_CONEXION);
                
                g2d.setColor(Color.BLACK);
                g2d.drawOval(punto.getX() - RADIO_PUNTO_CONEXION/2, 
                           punto.getY() - RADIO_PUNTO_CONEXION/2, 
                           RADIO_PUNTO_CONEXION, RADIO_PUNTO_CONEXION);
            }
        }
    }
    
    private void dibujarLineaTemporalConexion(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                    0, new float[]{5, 5}, 0));
        g2d.drawLine(puntoInicioConexion.getX(), puntoInicioConexion.getY(), 
                    puntoMouseActual.x, puntoMouseActual.y);
    }
}