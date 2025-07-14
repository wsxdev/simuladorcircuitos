package com.wsxdev.simuladorcircuitos.modelo;

/**
 * Fuente de voltaje DC
 */
public class FuenteVoltaje extends Componente {
    private double voltaje; // Voltios
    private boolean esAC; // false = DC, true = AC
    private double frecuencia; // Hz (solo para AC)
    
    public FuenteVoltaje(int x, int y, int angulo) {
        super("fuenteV", x, y, angulo);
        this.voltaje = 12.0; // Voltaje por defecto
        this.esAC = false;
        this.frecuencia = 60.0;
    }
    
    @Override
    protected void inicializarPuntosConexion() {
        // Terminal positivo (arriba)
        puntosConexion.add(new PuntoConexion("positivo", x + 24, y, this));
        // Terminal negativo (abajo)
        puntosConexion.add(new PuntoConexion("negativo", x + 24, y + 48, this));
    }
    
    @Override
    public double getValorPrincipal() {
        return voltaje;
    }
    
    @Override
    public void setValorPrincipal(double valor) {
        this.voltaje = valor;
    }
    
    @Override
    public String getUnidad() {
        return "V";
    }
    
    // Getters y setters específicos
    public double getVoltaje() { return voltaje; }
    public void setVoltaje(double voltaje) { this.voltaje = voltaje; }
    public boolean isEsAC() { return esAC; }
    public void setEsAC(boolean esAC) { this.esAC = esAC; }
    public double getFrecuencia() { return frecuencia; }
    public void setFrecuencia(double frecuencia) { this.frecuencia = frecuencia; }
    
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