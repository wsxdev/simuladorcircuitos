package com.wsxdev.simuladorcircuitos.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Clase base para todos los componentes del circuito
 */
public abstract class Componente implements Serializable {
    protected String id;
    protected int x, y, angulo;
    protected String tipo;
    protected List<PuntoConexion> puntosConexion;
    protected String nombre;
    protected boolean seleccionado;
    
    public Componente(String tipo, int x, int y, int angulo) {
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.angulo = angulo;
        this.puntosConexion = new ArrayList<>();
        this.seleccionado = false;
        this.nombre = tipo + "_" + id.substring(0, 8);
        inicializarPuntosConexion();
    }
    
    /**
     * Inicializa los puntos de conexión específicos de cada componente
     */
    protected abstract void inicializarPuntosConexion();
    
    /**
     * Obtiene el valor de la resistencia, voltaje, etc. según el tipo de componente
     */
    public abstract double getValorPrincipal();
    
    /**
     * Establece el valor principal del componente
     */
    public abstract void setValorPrincipal(double valor);
    
    /**
     * Obtiene la unidad del valor principal (V, Ω, A, etc.)
     */
    public abstract String getUnidad();
    
    // Getters y setters
    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAngulo() { return angulo; }
    public String getTipo() { return tipo; }
    public List<PuntoConexion> getPuntosConexion() { return puntosConexion; }
    public String getNombre() { return nombre; }
    public boolean isSeleccionado() { return seleccionado; }
    
    public void setX(int x) { 
        this.x = x; 
        actualizarPosicionesPuntosConexion();
    }
    
    public void setY(int y) { 
        this.y = y; 
        actualizarPosicionesPuntosConexion();
    }
    
    public void setAngulo(int angulo) { 
        this.angulo = angulo; 
        actualizarPosicionesPuntosConexion();
    }
    
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }
    
    /**
     * Actualiza las posiciones de los puntos de conexión cuando el componente se mueve o rota
     */
    protected void actualizarPosicionesPuntosConexion() {
        // Implementación por defecto - las subclases pueden sobreescribir si necesitan lógica específica
        for (PuntoConexion punto : puntosConexion) {
            punto.actualizarPosicion(x, y, angulo);
        }
    }
    
    /**
     * Encuentra el punto de conexión más cercano a las coordenadas dadas
     */
    public PuntoConexion getPuntoConexionMasCercano(int mouseX, int mouseY) {
        PuntoConexion masCercano = null;
        double distanciaMinima = Double.MAX_VALUE;
        
        for (PuntoConexion punto : puntosConexion) {
            double distancia = Math.sqrt(Math.pow(punto.getX() - mouseX, 2) + Math.pow(punto.getY() - mouseY, 2));
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                masCercano = punto;
            }
        }
        
        return masCercano;
    }
    
    /**
     * Verifica si el componente contiene el punto dado
     */
    public boolean contiene(int mouseX, int mouseY) {
        // Área aproximada del componente (48x48 pixels por defecto)
        int ancho = 48;
        int alto = 48;
        return mouseX >= x && mouseX <= x + ancho && mouseY >= y && mouseY <= y + alto;
    }
}