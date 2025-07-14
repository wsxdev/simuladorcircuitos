package com.wsxdev.simuladorcircuitos.modelo;

/**
 * Cable - Conductor eléctrico
 */
public class Cable extends Componente {
    private double resistencia; // Resistencia del cable (muy baja)
    private double capacidadCorriente; // Capacidad máxima de corriente
    private double longitud; // Longitud del cable en metros
    private double corrienteActual; // Corriente que fluye por el cable
    
    public Cable(int x, int y, int angulo) {
        super("cable", x, y, angulo);
        this.resistencia = 0.001; // 1mΩ por defecto
        this.capacidadCorriente = 10.0; // 10A por defecto
        this.longitud = 0.1; // 10cm por defecto
        this.corrienteActual = 0.0;
    }
    
    @Override
    protected void inicializarPuntosConexion() {
        // Extremo 1
        puntosConexion.add(new PuntoConexion("extremo1", x, y + 24, this));
        // Extremo 2
        puntosConexion.add(new PuntoConexion("extremo2", x + 48, y + 24, this));
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
    
    /**
     * Actualiza la corriente que fluye por el cable
     */
    public void actualizarCorriente(double corriente) {
        this.corrienteActual = corriente;
    }
    
    /**
     * Verifica si el cable está sobrecargado
     */
    public boolean estaSobrecargado() {
        return Math.abs(corrienteActual) > capacidadCorriente;
    }
    
    /**
     * Calcula la caída de voltaje en el cable
     */
    public double getCaidaVoltaje() {
        return corrienteActual * resistencia;
    }
    
    /**
     * Calcula la potencia disipada como calor
     */
    public double getPotenciaDisipada() {
        return Math.pow(corrienteActual, 2) * resistencia;
    }
    
    // Getters y setters específicos
    public double getResistencia() { return resistencia; }
    public void setResistencia(double resistencia) { this.resistencia = resistencia; }
    public double getCapacidadCorriente() { return capacidadCorriente; }
    public void setCapacidadCorriente(double capacidadCorriente) { this.capacidadCorriente = capacidadCorriente; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public double getCorrienteActual() { return corrienteActual; }
    
    public PuntoConexion getExtremo1() {
        return puntosConexion.stream()
                .filter(p -> p.getNombre().equals("extremo1"))
                .findFirst()
                .orElse(null);
    }
    
    public PuntoConexion getExtremo2() {
        return puntosConexion.stream()
                .filter(p -> p.getNombre().equals("extremo2"))
                .findFirst()
                .orElse(null);
    }
}
