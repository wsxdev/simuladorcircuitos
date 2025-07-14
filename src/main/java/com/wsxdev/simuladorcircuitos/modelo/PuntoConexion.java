package com.wsxdev.simuladorcircuitos.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Punto de conexión de un componente
 */
public class PuntoConexion implements Serializable {
    private String nombre; // "positivo", "negativo", "terminal1", etc.
    private int x, y; // Posición absoluta en el panel
    private int offsetX, offsetY; // Posición relativa al componente
    private Componente componentePadre;
    private List<Conexion> conexiones;
    private boolean conectado;
    
    public PuntoConexion(String nombre, int x, int y, Componente componentePadre) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.componentePadre = componentePadre;
        this.conexiones = new ArrayList<>();
        this.conectado = false;
        
        // Calcular offset relativo al componente padre
        this.offsetX = x - componentePadre.getX();
        this.offsetY = y - componentePadre.getY();
    }
    
    /**
     * Actualiza la posición del punto cuando el componente padre se mueve o rota
     */
    public void actualizarPosicion(int componenteX, int componenteY, int anguloComponente) {
        if (anguloComponente == 0) {
            this.x = componenteX + offsetX;
            this.y = componenteY + offsetY;
        } else {
            // Aplicar rotación
            double radianes = Math.toRadians(anguloComponente);
            double cos = Math.cos(radianes);
            double sin = Math.sin(radianes);
            
            // Centro del componente
            int centroX = componenteX + 24; // Asumiendo componentes de 48x48
            int centroY = componenteY + 24;
            
            // Aplicar rotación alrededor del centro
            double nuevoOffsetX = offsetX * cos - offsetY * sin;
            double nuevoOffsetY = offsetX * sin + offsetY * cos;
            
            this.x = (int) (centroX + nuevoOffsetX - 24);
            this.y = (int) (centroY + nuevoOffsetY - 24);
        }
    }
    
    /**
     * Agrega una conexión a este punto
     */
    public void agregarConexion(Conexion conexion) {
        if (!conexiones.contains(conexion)) {
            conexiones.add(conexion);
            conectado = true;
        }
    }
    
    /**
     * Remueve una conexión de este punto
     */
    public void removerConexion(Conexion conexion) {
        conexiones.remove(conexion);
        conectado = !conexiones.isEmpty();
    }
    
    /**
     * Verifica si este punto puede conectarse con otro
     */
    public boolean puedeConectarCon(PuntoConexion otro) {
        // No puede conectarse consigo mismo
        if (this == otro) return false;
        
        // No puede conectarse con otro punto del mismo componente
        if (this.componentePadre == otro.componentePadre) return false;
        
        // Verificar si ya están conectados
        for (Conexion conexion : conexiones) {
            if (conexion.conecta(this, otro)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Calcula la distancia a otro punto
     */
    public double distanciaA(PuntoConexion otro) {
        return Math.sqrt(Math.pow(this.x - otro.x, 2) + Math.pow(this.y - otro.y, 2));
    }
    
    /**
     * Verifica si el punto contiene las coordenadas dadas (para detección de mouse)
     */
    public boolean contiene(int mouseX, int mouseY, int radio) {
        double distancia = Math.sqrt(Math.pow(this.x - mouseX, 2) + Math.pow(this.y - mouseY, 2));
        return distancia <= radio;
    }
    
    // Getters y setters
    public String getNombre() { return nombre; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Componente getComponentePadre() { return componentePadre; }
    public List<Conexion> getConexiones() { return conexiones; }
    public boolean isConectado() { return conectado; }
    
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    @Override
    public String toString() {
        return String.format("PuntoConexion{%s, (%d,%d), conectado=%s}", 
                           nombre, x, y, conectado);
    }
}