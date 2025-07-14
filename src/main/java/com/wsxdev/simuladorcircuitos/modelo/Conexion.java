package com.wsxdev.simuladorcircuitos.modelo;

import java.io.Serializable;
import java.awt.Color;
import java.util.UUID;

/**
 * Representa una conexión (cable) entre dos puntos de conexión
 */
public class Conexion implements Serializable {
    private String id;
    private PuntoConexion puntoInicio;
    private PuntoConexion puntoFin;
    private double corriente; // Amperios
    private double voltaje; // Voltios
    private Color color;
    private boolean seleccionada;
    private boolean activa; // Para indicar si hay flujo de corriente
    
    public Conexion(PuntoConexion puntoInicio, PuntoConexion puntoFin) {
        this.id = UUID.randomUUID().toString();
        this.puntoInicio = puntoInicio;
        this.puntoFin = puntoFin;
        this.corriente = 0.0;
        this.voltaje = 0.0;
        this.color = Color.BLACK;
        this.seleccionada = false;
        this.activa = false;
        
        // Agregar esta conexión a los puntos
        puntoInicio.agregarConexion(this);
        puntoFin.agregarConexion(this);
    }
    
    /**
     * Verifica si esta conexión conecta los dos puntos dados
     */
    public boolean conecta(PuntoConexion punto1, PuntoConexion punto2) {
        return (puntoInicio == punto1 && puntoFin == punto2) ||
               (puntoInicio == punto2 && puntoFin == punto1);
    }
    
    /**
     * Obtiene el otro punto de la conexión
     */
    public PuntoConexion getOtroPunto(PuntoConexion punto) {
        if (puntoInicio == punto) {
            return puntoFin;
        } else if (puntoFin == punto) {
            return puntoInicio;
        }
        return null;
    }
    
    /**
     * Calcula la longitud de la conexión
     */
    public double getLongitud() {
        return Math.sqrt(Math.pow(puntoFin.getX() - puntoInicio.getX(), 2) +
                        Math.pow(puntoFin.getY() - puntoInicio.getY(), 2));
    }
    
    /**
     * Verifica si la conexión está cerca de un punto (para selección con mouse)
     */
    public boolean estaCercaDe(int mouseX, int mouseY, double tolerancia) {
        // Calcular distancia del punto a la línea
        double x1 = puntoInicio.getX();
        double y1 = puntoInicio.getY();
        double x2 = puntoFin.getX();
        double y2 = puntoFin.getY();
        
        // Fórmula de distancia punto-línea
        double A = mouseX - x1;
        double B = mouseY - y1;
        double C = x2 - x1;
        double D = y2 - y1;
        
        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = (lenSq != 0) ? dot / lenSq : -1;
        
        double xx, yy;
        
        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }
        
        double dx = mouseX - xx;
        double dy = mouseY - yy;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        
        return distancia <= tolerancia;
    }
    
    /**
     * Desconecta esta conexión de sus puntos
     */
    public void desconectar() {
        puntoInicio.removerConexion(this);
        puntoFin.removerConexion(this);
    }
    
    /**
     * Actualiza el color según el estado de la corriente
     */
    public void actualizarColor() {
        if (!activa || corriente == 0) {
            color = Color.BLACK;
        } else if (corriente > 0) {
            // Corriente positiva - rojo
            int intensidad = Math.min(255, (int)(Math.abs(corriente) * 50 + 100));
            color = new Color(intensidad, 0, 0);
        } else {
            // Corriente negativa - azul
            int intensidad = Math.min(255, (int)(Math.abs(corriente) * 50 + 100));
            color = new Color(0, 0, intensidad);
        }
    }
    
    // Getters y setters
    public String getId() { return id; }
    public PuntoConexion getPuntoInicio() { return puntoInicio; }
    public PuntoConexion getPuntoFin() { return puntoFin; }
    public double getCorriente() { return corriente; }
    public double getVoltaje() { return voltaje; }
    public Color getColor() { return color; }
    public boolean isSeleccionada() { return seleccionada; }
    public boolean isActiva() { return activa; }
    
    public void setCorriente(double corriente) { 
        this.corriente = corriente;
        actualizarColor();
    }
    
    public void setVoltaje(double voltaje) { this.voltaje = voltaje; }
    public void setColor(Color color) { this.color = color; }
    public void setSeleccionada(boolean seleccionada) { this.seleccionada = seleccionada; }
    public void setActiva(boolean activa) { 
        this.activa = activa;
        actualizarColor();
    }
    
    @Override
    public String toString() {
        return String.format("Conexion{%s -> %s, I=%.3fA, V=%.3fV}", 
                           puntoInicio.getNombre(), puntoFin.getNombre(), corriente, voltaje);
    }
}