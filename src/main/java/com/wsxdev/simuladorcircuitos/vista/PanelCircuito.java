package com.wsxdev.simuladorcircuitos.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PanelCircuito extends JPanel {

    private java.util.List<ComponenteVisual> componentes;
    private JPopupMenu menuComponentes;
    private int lastClickX, lastClickY;
    private ComponenteVisual componenteArrastrado = null;
    private int offsetX, offsetY;
    private ComponenteVisual componenteSeleccionado = null;


    public PanelCircuito() {
        setBackground(Color.WHITE);
        setLayout(null); // Posicionamiento absoluto
        componentes = new ArrayList<>();
        menuComponentes = crearMenuContextual();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    prepararMenu(e);
                } else {
                    for (ComponenteVisual c : componentes) {
                        if (c.contiene(e.getX(), e.getY())) {
                            componenteArrastrado = c;
                            componenteSeleccionado = c; // NUEVO
                            offsetX = e.getX() - c.x;
                            offsetY = e.getY() - c.y;
                            requestFocusInWindow();
                            break;
                        }
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    prepararMenu(e);
                }
                componenteArrastrado = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (componenteArrastrado != null) {
                    componenteArrastrado.x = e.getX() - offsetX;
                    componenteArrastrado.y = e.getY() - offsetY;
                    repaint();
                }
            }
        });

        setFocusable(true); // Necesario para eventos de teclado
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R && componenteArrastrado != null) {
                    componenteArrastrado.angulo = (componenteArrastrado.angulo + 90) % 360;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_DELETE && componenteSeleccionado != null) {
                    componentes.remove(componenteSeleccionado);
                    componenteSeleccionado = null;
                    repaint();
                }
            }
        });
    }

    private void prepararMenu(MouseEvent e) {
        lastClickX = e.getX();
        lastClickY = e.getY();
        menuComponentes.show(e.getComponent(), lastClickX, lastClickY);
        requestFocusInWindow(); // Para que pueda detectar tecla R
    }

    private JPopupMenu crearMenuContextual() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem eliminar = new JMenuItem("Eliminar componente");
        eliminar.addActionListener(e -> {
            if (componenteSeleccionado != null) {
                componentes.remove(componenteSeleccionado);
                componenteSeleccionado = null;
                repaint();
            }
        });
        menu.addSeparator(); // Separador visual
        menu.add(eliminar);

        JMenuItem fuente = new JMenuItem("Fuente de Voltaje");
        fuente.addActionListener(e -> agregarComponente("fuenteV", lastClickX, lastClickY));
        menu.add(fuente);

        JMenuItem resistencia = new JMenuItem("Resistencia");
        resistencia.addActionListener(e -> agregarComponente("resistencia", lastClickX, lastClickY));
        menu.add(resistencia);

        JMenuItem cable = new JMenuItem("Cable");
        cable.addActionListener(e -> agregarComponente("cable", lastClickX, lastClickY));
        menu.add(cable);

        JMenuItem amperimetro = new JMenuItem("Amperímetro");
        amperimetro.addActionListener(e -> agregarComponente("amperimetro", lastClickX, lastClickY));
        menu.add(amperimetro);

        JMenuItem voltimetro = new JMenuItem("Voltímetro");
        voltimetro.addActionListener(e -> agregarComponente("voltimetro", lastClickX, lastClickY));
        menu.add(voltimetro);

        return menu;
    }

    public void agregarComponente(String tipo, int x, int y) {
        componentes.add(new ComponenteVisual(tipo, x, y, 48, 48));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (ComponenteVisual c : componentes) {
            c.dibujar(g2, this);
        }
    }

    // Clase interna para representar visualmente un componente
    static class ComponenteVisual {
        String tipo;
        int x, y, w, h;
        int angulo = 0; // Grados de rotación
        ImageIcon icono;

        public ComponenteVisual(String tipo, int x, int y, int w, int h) {
            this.tipo = tipo;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;

            String ruta = "/img/png/componentes/" + tipo + ".png";
            java.net.URL imgURL = getClass().getResource(ruta);
            if (imgURL != null) {
                Image img = new ImageIcon(imgURL).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                icono = new ImageIcon(img);
            } else {
                System.err.println("No se encontró el recurso: " + ruta);
                icono = null;
            }
        }

        public void dibujar(Graphics2D g2, JComponent comp) {
            if (icono != null) {
                Graphics2D g = (Graphics2D) g2.create();
                g.rotate(Math.toRadians(angulo), x + w / 2.0, y + h / 2.0);
                g.drawImage(icono.getImage(), x, y, comp);
                g.dispose();
            } else {
                g2.setColor(Color.RED);
                g2.fillRect(x, y, w, h);
                g2.setColor(Color.WHITE);
                g2.drawString("?", x + w / 2 - 4, y + h / 2 + 4);
            }
        }

        public boolean contiene(int mx, int my) {
            return mx >= x && mx <= x + w && my >= y && my <= y + h;
        }

    }

    public com.wsxdev.simuladorcircuitos.modelo.Circuito obtenerCircuitoComoModelo() {
        java.util.List<com.wsxdev.simuladorcircuitos.modelo.Componente> lista = new ArrayList<>();
        for (ComponenteVisual cv : componentes) {
            lista.add(new com.wsxdev.simuladorcircuitos.modelo.Componente(cv.tipo, cv.x, cv.y, cv.angulo));
        }
        return new com.wsxdev.simuladorcircuitos.modelo.Circuito(lista);
    }

    public void cargarComponentesDesdeModelo(com.wsxdev.simuladorcircuitos.modelo.Circuito circuito) {
        componentes.clear();
        for (com.wsxdev.simuladorcircuitos.modelo.Componente c : circuito.getComponentes()) {
            // Asumiendo que Componente tiene getTipo(), getX(), getY(), getAngulo()
            ComponenteVisual cv = new ComponenteVisual(c.getTipo(), c.getX(), c.getY(), 48, 48);
            cv.angulo = c.getAngulo();  // Si tienes ese getter
            componentes.add(cv);
        }
        repaint();
    }



}