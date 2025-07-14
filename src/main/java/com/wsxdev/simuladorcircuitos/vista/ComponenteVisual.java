package com.wsxdev.simuladorcircuitos.vista;

import com.wsxdev.simuladorcircuitos.modelo.Componente;

import javax.swing.*;
import java.awt.*;

/**
 * Representación visual de un componente en el panel de circuito
 */
public class ComponenteVisual {
    private Componente componente;
    private ImageIcon icono;
    private boolean seleccionado;
    
    private static final int ANCHO_COMPONENTE = 48;
    private static final int ALTO_COMPONENTE = 48;
    
    public ComponenteVisual(Componente componente) {
        this.componente = componente;
        this.seleccionado = false;
        cargarIcono();
    }
    
    private void cargarIcono() {
        String ruta = "/img/png/componentes/" + componente.getTipo() + ".png";
        java.net.URL imgURL = getClass().getResource(ruta);
        
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage()
                    .getScaledInstance(ANCHO_COMPONENTE, ALTO_COMPONENTE, Image.SCALE_SMOOTH);
            icono = new ImageIcon(img);
        } else {
            System.err.println("No se encontró el recurso: " + ruta);
            icono = null;
        }
    }
    
    public void dibujar(Graphics2D g2d, JComponent comp) {
        Graphics2D g = (Graphics2D) g2d.create();
        
        try {
            // Aplicar rotación si es necesaria
            if (componente.getAngulo() != 0) {
                double centroX = componente.getX() + ANCHO_COMPONENTE / 2.0;
                double centroY = componente.getY() + ALTO_COMPONENTE / 2.0;
                g.rotate(Math.toRadians(componente.getAngulo()), centroX, centroY);
            }
            
            // Dibujar sombra si está seleccionado
            if (seleccionado) {
                g.setColor(new Color(0, 0, 255, 50));
                g.fillRect(componente.getX() - 2, componente.getY() - 2, 
                          ANCHO_COMPONENTE + 4, ALTO_COMPONENTE + 4);
            }
            
            // Dibujar icono del componente
            if (icono != null) {
                g.drawImage(icono.getImage(), componente.getX(), componente.getY(), comp);
            } else {
                // Dibujar rectángulo de fallback
                g.setColor(Color.RED);
                g.fillRect(componente.getX(), componente.getY(), ANCHO_COMPONENTE, ALTO_COMPONENTE);
                g.setColor(Color.WHITE);
                g.drawString("?", componente.getX() + ANCHO_COMPONENTE/2 - 4, 
                           componente.getY() + ALTO_COMPONENTE/2 + 4);
            }
            
            // Dibujar borde si está seleccionado
            if (seleccionado) {
                g.setColor(Color.BLUE);
                g.setStroke(new BasicStroke(2));
                g.drawRect(componente.getX(), componente.getY(), ANCHO_COMPONENTE, ALTO_COMPONENTE);
            }
            
            // Dibujar etiqueta con valor
            dibujarEtiquetaValor(g);
            
        } finally {
            g.dispose();
        }
    }
    
    private void dibujarEtiquetaValor(Graphics2D g) {
        String valor = formatearValor();
        if (valor != null && !valor.isEmpty()) {
            FontMetrics fm = g.getFontMetrics();
            int anchoTexto = fm.stringWidth(valor);
            int altoTexto = fm.getHeight();
            
            // Posición de la etiqueta (debajo del componente)
            int x = componente.getX() + (ANCHO_COMPONENTE - anchoTexto) / 2;
            int y = componente.getY() + ALTO_COMPONENTE + altoTexto;
            
            // Fondo semitransparente
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(x - 2, y - altoTexto + 2, anchoTexto + 4, altoTexto);
            
            // Texto
            g.setColor(Color.BLACK);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            g.drawString(valor, x, y);
        }
    }
    
    private String formatearValor() {
        double valor = componente.getValorPrincipal();
        String unidad = componente.getUnidad();
        
        // Formatear según el tipo de componente y la magnitud del valor
        if (valor == 0) return null;
        
        String formato;
        if (valor >= 1000000) {
            formato = String.format("%.1fM%s", valor / 1000000, unidad);
        } else if (valor >= 1000) {
            formato = String.format("%.1fk%s", valor / 1000, unidad);
        } else if (valor >= 1) {
            formato = String.format("%.1f%s", valor, unidad);
        } else if (valor >= 0.001) {
            formato = String.format("%.1fm%s", valor * 1000, unidad);
        } else {
            formato = String.format("%.1fμ%s", valor * 1000000, unidad);
        }
        
        return formato;
    }
    
    public boolean contiene(int x, int y) {
        return x >= componente.getX() && x <= componente.getX() + ANCHO_COMPONENTE &&
               y >= componente.getY() && y <= componente.getY() + ALTO_COMPONENTE;
    }
    
    public void setPosition(int x, int y) {
        componente.setX(x);
        componente.setY(y);
    }
    
    public void rotar(int grados) {
        int nuevoAngulo = (componente.getAngulo() + grados) % 360;
        componente.setAngulo(nuevoAngulo);
    }
    
    // Getters y setters
    public Componente getComponente() { return componente; }
    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { 
        this.seleccionado = seleccionado;
        componente.setSeleccionado(seleccionado);
    }
    public int getX() { return componente.getX(); }
    public int getY() { return componente.getY(); }
    public int getAncho() { return ANCHO_COMPONENTE; }
    public int getAlto() { return ALTO_COMPONENTE; }
}
