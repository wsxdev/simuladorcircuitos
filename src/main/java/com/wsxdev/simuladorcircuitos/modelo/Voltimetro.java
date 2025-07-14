package com.wsxdev.simuladorcircuitos.modelo;

/**
 * Voltímetro - Instrumento para medir voltaje
 */
public class Voltimetro extends Componente {
    private double voltajeMedido; // Voltios
    private double rangoMaximo; // Rango máximo de medición
    private double resistenciaInterna; // Resistencia interna del voltímetro
    
    public Voltimetro(int x, int y, int angulo) {
        super("voltimetro", x, y, angulo);
        this.voltajeMedido = 0.0;
        this.rangoMaximo = 50.0; // 50V por defecto
        this.resistenciaInterna = 1000000.0; // 1MΩ (muy alta)
    }
    
    @Override
    protected void inicializarPuntosConexion() {
        // Terminal positivo
        puntosConexion.add(new PuntoConexion("positivo", x + 24, y, this));
        // Terminal negativo
        puntosConexion.add(new PuntoConexion("negativo", x + 24, y + 48, this));
    }
    
    @Override
    public double getValorPrincipal() {
        return voltajeMedido;
    }
    
    @Override
    public void setValorPrincipal(double valor) {
        this.voltajeMedido = valor;
    }
    
    @Override
    public String getUnidad() {
        return "V";
    }
    
    /**
     * Actualiza la medición de voltaje
     */
    public void actualizarMedicion(double voltaje) {
        this.voltajeMedido = voltaje;
    }
    
    /**
     * Verifica si el voltaje está dentro del rango
     */
    public boolean estaEnRango() {
        return Math.abs(voltajeMedido) <= rangoMaximo;
    }
    
    /**
     * Obtiene la lectura formateada
     */
    public String getLecturaFormateada() {
        if (Math.abs(voltajeMedido) >= 1.0) {
            return String.format("%.3f V", voltajeMedido);
        } else {
            return String.format("%.1f mV", voltajeMedido * 1000);
        }
    }
    
    // Getters y setters específicos
    public double getVoltajeMedido() { return voltajeMedido; }
    public double getRangoMaximo() { return rangoMaximo; }
    public void setRangoMaximo(double rangoMaximo) { this.rangoMaximo = rangoMaximo; }
    public double getResistenciaInterna() { return resistenciaInterna; }
    public void setResistenciaInterna(double resistenciaInterna) { this.resistenciaInterna = resistenciaInterna; }
    
    public PuntoConexion getTerminalPositivo() {
        return puntosConexion.stream()
                .filter(p -> p.getNombre().equals("positivo"))
                .findFirst()
                .orElse(null);
    }
    
    public PuntoConexion getTerminalNegativo() {
        return puntosConexion.stream()
                .filter(p -> p.getNombre().equals("negativo"))
                .findFirst()
                .orElse(null);
    }
}
