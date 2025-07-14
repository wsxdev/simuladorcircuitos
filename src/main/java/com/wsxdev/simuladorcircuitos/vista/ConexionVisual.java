package com.wsxdev.simuladorcircuitos.vista;

import com.wsxdev.simuladorcircuitos.modelo.Conexion;
import com.wsxdev.simuladorcircuitos.modelo.Componente;

import java.awt.*;

/**
 * Representación visual de una conexión en el panel de circuito
 */
public class ConexionVisual {
    private Conexion conexion;
    private static final int GROSOR_LINEA_NORMAL = 3;
    private static final int GROSOR_LINEA_ACTIVA = 5;
    
    public ConexionVisual(Conexion conexion) {
        this.conexion = conexion;
    }
    
    public void dibujar(Graphics2D g2d) {
        int x1 = conexion.getPuntoInicio().getX();
        int y1 = conexion.getPuntoInicio().getY();
        int x2 = conexion.getPuntoFin().getX();
        int y2 = conexion.getPuntoFin().getY();
        
        // Configurar stroke según si la conexión está activa
        int grosor = conexion.isActiva() ? GROSOR_LINEA_ACTIVA : GROSOR_LINEA_NORMAL;
        g2d.setStroke(new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Configurar color
        g2d.setColor(conexion.getColor());
        
        // Dibujar línea principal
        g2d.drawLine(x1, y1, x2, y2);
        
        // Dibujar información si la conexión está activa
        if (conexion.isActiva() && (conexion.getCorriente() != 0 || conexion.getVoltaje() != 0)) {
            dibujarInformacion(g2d, x1, y1, x2, y2);
        }
        
        // Dibujar puntos de conexión
        dibujarPuntosConexion(g2d, x1, y1, x2, y2);
    }
    
    private void dibujarInformacion(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        // Calcular punto medio
        int puntoMedioX = (x1 + x2) / 2;
        int puntoMedioY = (y1 + y2) / 2;
        
        // Preparar texto
        String textoCorriente = String.format("%.3fA", Math.abs(conexion.getCorriente()));
        String textoVoltaje = String.format("%.2fV", conexion.getVoltaje());
        
        // Configurar fuente
        Font fuenteOriginal = g2d.getFont();
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        
        // Dibujar fondo semitransparente
        int anchoTexto = Math.max(fm.stringWidth(textoCorriente), fm.stringWidth(textoVoltaje));
        int altoTexto = fm.getHeight() * 2;
        
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRoundRect(puntoMedioX - anchoTexto/2 - 2, puntoMedioY - altoTexto/2, 
                         anchoTexto + 4, altoTexto, 5, 5);
        
        // Dibujar borde
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(puntoMedioX - anchoTexto/2 - 2, puntoMedioY - altoTexto/2, 
                         anchoTexto + 4, altoTexto, 5, 5);
        
        // Dibujar texto
        g2d.drawString(textoCorriente, puntoMedioX - fm.stringWidth(textoCorriente)/2, 
                      puntoMedioY - fm.getHeight()/4);
        g2d.drawString(textoVoltaje, puntoMedioX - fm.stringWidth(textoVoltaje)/2, 
                      puntoMedioY + fm.getHeight()/2);
        
        // Restaurar fuente
        g2d.setFont(fuenteOriginal);
    }
    
    private void dibujarPuntosConexion(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        // Dibujar pequeños círculos en los puntos de conexión
        int radio = 4;
        
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x1 - radio/2, y1 - radio/2, radio, radio);
        g2d.fillOval(x2 - radio/2, y2 - radio/2, radio, radio);
        
        // Borde blanco para mejor visibilidad
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(x1 - radio/2, y1 - radio/2, radio, radio);
        g2d.drawOval(x2 - radio/2, y2 - radio/2, radio, radio);
    }
    
    public boolean contiene(int x, int y, double tolerancia) {
        return conexion.estaCercaDe(x, y, tolerancia);
    }
    
    public boolean involucraComponente(Componente componente) {
        return conexion.getPuntoInicio().getComponentePadre() == componente ||
               conexion.getPuntoFin().getComponentePadre() == componente;
    }
    
    public void actualizar() {
        // Método para actualizar la visualización si es necesario
        // Actualmente no se necesita implementación específica
    }
    
    public Conexion getConexion() {
        return conexion;
    }
}
