package com.wsxdev.simuladorcircuitos.modelo;

/**
 * Resistencia eléctrica
 */
public class Resistencia extends Componente {
    private double resistencia; // Ohmios
    private double potencia; // Vatios (capacidad)
    private double tolerancia; // Porcentaje
    
    public Resistencia(int x, int y, int angulo) {
        super("resistencia", x, y, angulo);
        this.resistencia = 1000.0; // 1kΩ por defecto
        this.potencia = 0.25; // 1/4 W por defecto
        this.tolerancia = 5.0; // 5% por defecto
    }
    
    @Override
    protected void inicializarPuntosConexion() {
        // Terminal izquierdo
        puntosConexion.add(new PuntoConexion("terminal1", x, y + 24, this));
        // Terminal derecho
        puntosConexion.add(new PuntoConexion("terminal2", x + 48, y + 24, this));
    }
    
    @Override
    public double getValorPrincipal() {
        return resistencia;
    }
    
    @Override
    public void setValorPrincipal(double valor) {
        this.resistencia = valor;
    }
    
    @Override
    public String getUnidad() {
        return "Ω";
    }
    
    // Getters y setters específicos
    public double getResistencia() { return resistencia; }
    public void setResistencia(double resistencia) { this.resistencia = resistencia; }
    public double getPotencia() { return potencia; }
    public void setPotencia(double potencia) { this.potencia = potencia; }
    public double getTolerancia() { return tolerancia; }
    public void setTolerancia(double tolerancia) { this.tolerancia = tolerancia; }
    
    /**
     * Formatea el valor de la resistencia con unidades apropiadas
     */
    public String getResistenciaFormateada() {
        if (resistencia >= 1000000) {
            return String.format("%.1f MΩ", resistencia / 1000000);
        } else if (resistencia >= 1000) {
            return String.format("%.1f kΩ", resistencia / 1000);
        } else {
            return String.format("%.1f Ω", resistencia);
        }
    }
    
    public PuntoConexion getTerminal1() {
        return puntosConexion.stream()
                .filter(p -> p.getNombre().equals("terminal1"))
                .findFirst()
                .orElse(null);
    }
    
    public PuntoConexion getTerminal2() {
        return puntosConexion.stream()
                .filter(p -> p.getNombre().equals("terminal2"))
                .findFirst()
                .orElse(null);
    }
}