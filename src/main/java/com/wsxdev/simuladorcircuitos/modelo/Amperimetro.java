package com.wsxdev.simuladorcircuitos.modelo;

/**
 * Amperímetro - Instrumento para medir corriente
 */
public class Amperimetro extends Componente {
    private double corrienteMedida; // Amperios
    private double rangoMaximo; // Rango máximo de medición
    private double resistenciaInterna; // Resistencia interna del amperímetro
    
    public Amperimetro(int x, int y, int angulo) {
        super("amperimetro", x, y, angulo);
        this.corrienteMedida = 0.0;
        this.rangoMaximo = 10.0; // 10A por defecto
        this.resistenciaInterna = 0.001; // 1mΩ (muy baja)
    }
    
    @Override
    protected void inicializarPuntosConexion() {
        // Terminal de entrada
        puntosConexion.add(new PuntoConexion("entrada", x, y + 24, this));
        // Terminal de salida
        puntosConexion.add(new PuntoConexion("salida", x + 48, y + 24, this));
    }
    
    @Override
    public double getValorPrincipal() {
        return corrienteMedida;
    }
    
    @Override
    public void setValorPrincipal(double valor) {
        this.corrienteMedida = valor;
    }
    
    @Override
    public String getUnidad() {
        return "A";
    }
    
    /**
     * Actualiza la medición de corriente
     */
    public void actualizarMedicion(double corriente) {
        this.corrienteMedida = corriente;
    }
    
    /**
     * Verifica si la corriente está dentro del rango
     */
    public boolean estaEnRango() {
        return Math.abs(corrienteMedida) <= rangoMaximo;
    }
    
    /**
     * Obtiene la lectura formateada
     */
    public String getLecturaFormateada() {
        if (Math.abs(corrienteMedida) >= 1.0) {
            return String.format("%.3f A", corrienteMedida);
        } else if (Math.abs(corrienteMedida) >= 0.001) {
            return String.format("%.1f mA", corrienteMedida * 1000);
        } else {
            return String.format("%.1f μA", corrienteMedida * 1000000);
        }
    }
    
    // Getters y setters específicos
    public double getCorrienteMedida() { return corrienteMedida; }
    public double getRangoMaximo() { return rangoMaximo; }
    public void setRangoMaximo(double rangoMaximo) { this.rangoMaximo = rangoMaximo; }
    public double getResistenciaInterna() { return resistenciaInterna; }
    public void setResistenciaInterna(double resistenciaInterna) { this.resistenciaInterna = resistenciaInterna; }
    
    public PuntoConexion getTerminalEntrada() {
        return puntosConexion.stream()
                .filter(p -> p.getNombre().equals("entrada"))
                .findFirst()
                .orElse(null);
    }
    
    public PuntoConexion getTerminalSalida() {
        return puntosConexion.stream()
                .filter(p -> p.getNombre().equals("salida"))
                .findFirst()
                .orElse(null);
    }
}
