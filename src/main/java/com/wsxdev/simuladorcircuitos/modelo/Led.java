package com.wsxdev.simuladorcircuitos.modelo;

/**
 * LED (Light Emitting Diode)
 */
public class Led extends Componente {
    private double voltajeDirecto; // Voltios (típicamente 1.8V - 3.3V)
    private double corrienteMaxima; // Amperios (típicamente 20mA)
    private String color; // Color del LED
    private boolean encendido;
    private double corrienteActual;
    
    public Led(int x, int y, int angulo) {
        super("led", x, y, angulo);
        this.voltajeDirecto = 2.0; // 2V por defecto
        this.corrienteMaxima = 0.02; // 20mA por defecto
        this.color = "rojo";
        this.encendido = false;
        this.corrienteActual = 0.0;
    }
    
    @Override
    protected void inicializarPuntosConexion() {
        // Ánodo (positivo) - izquierda
        puntosConexion.add(new PuntoConexion("anodo", x, y + 24, this));
        // Cátodo (negativo) - derecha
        puntosConexion.add(new PuntoConexion("catodo", x + 48, y + 24, this));
    }
    
    @Override
    public double getValorPrincipal() {
        return voltajeDirecto;
    }
    
    @Override
    public void setValorPrincipal(double valor) {
        this.voltajeDirecto = valor;
    }
    
    @Override
    public String getUnidad() {
        return "V";
    }
    
    /**
     * Actualiza el estado del LED basado en la corriente
     */
    public void actualizarEstado(double corriente) {
        this.corrienteActual = corriente;
        // El LED se enciende si la corriente es positiva y mayor que un umbral mínimo
        this.encendido = corriente > 0.001; // 1mA mínimo
    }
    
    /**
     * Verifica si el LED está en estado de saturación
     */
    public boolean estaEnSaturacion() {
        return corrienteActual >= corrienteMaxima;
    }
    
    /**
     * Calcula la resistencia dinámica del LED
     */
    public double getResistenciaDinamica() {
        if (corrienteActual > 0.001) {
            return voltajeDirecto / corrienteActual;
        }
        return Double.POSITIVE_INFINITY; // Resistencia infinita cuando está apagado
    }
    
    // Getters y setters específicos
    public double getVoltajeDirecto() { return voltajeDirecto; }
    public void setVoltajeDirecto(double voltajeDirecto) { this.voltajeDirecto = voltajeDirecto; }
    public double getCorrienteMaxima() { return corrienteMaxima; }
    public void setCorrienteMaxima(double corrienteMaxima) { this.corrienteMaxima = corrienteMaxima; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public boolean isEncendido() { return encendido; }
    public double getCorrienteActual() { return corrienteActual; }
    
    public PuntoConexion getAnodo() {
        return puntosConexion.stream()
                .filter(p -> p.getNombre().equals("anodo"))
                .findFirst()
                .orElse(null);
    }
    
    public PuntoConexion getCatodo() {
        return puntosConexion.stream()
                .filter(p -> p.getNombre().equals("catodo"))
                .findFirst()
                .orElse(null);
    }
}