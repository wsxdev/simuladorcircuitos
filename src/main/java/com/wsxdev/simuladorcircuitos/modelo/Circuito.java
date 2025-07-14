package com.wsxdev.simuladorcircuitos.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un circuito eléctrico completo
 */
public class Circuito implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private List<Componente> componentes;
    private List<Conexion> conexiones;
    private String descripcion;
    
    public Circuito() {
        this("Nuevo Circuito");
    }
    
    public Circuito(String nombre) {
        this.nombre = nombre;
        this.componentes = new ArrayList<>();
        this.conexiones = new ArrayList<>();
        this.descripcion = "";
    }
    
    public Circuito(List<Componente> componentes) {
        this("Circuito Importado");
        this.componentes = new ArrayList<>(componentes);
        this.conexiones = new ArrayList<>();
    }
    
    /**
     * Agrega un componente al circuito
     */
    public void agregarComponente(Componente componente) {
        if (componente != null && !componentes.contains(componente)) {
            componentes.add(componente);
        }
    }
    
    /**
     * Remueve un componente del circuito y todas sus conexiones
     */
    public void removerComponente(Componente componente) {
        if (componente != null) {
            // Remover todas las conexiones asociadas al componente
            List<Conexion> conexionesARemover = new ArrayList<>();
            for (Conexion conexion : conexiones) {
                if (conexion.getPuntoInicio().getComponentePadre() == componente ||
                    conexion.getPuntoFin().getComponentePadre() == componente) {
                    conexionesARemover.add(conexion);
                }
            }
            
            for (Conexion conexion : conexionesARemover) {
                removerConexion(conexion);
            }
            
            componentes.remove(componente);
        }
    }
    
    /**
     * Agrega una conexión al circuito
     */
    public void agregarConexion(Conexion conexion) {
        if (conexion != null && !conexiones.contains(conexion)) {
            conexiones.add(conexion);
        }
    }
    
    /**
     * Remueve una conexión del circuito
     */
    public void removerConexion(Conexion conexion) {
        if (conexion != null) {
            conexion.desconectar();
            conexiones.remove(conexion);
        }
    }
    
    /**
     * Crea una conexión entre dos puntos si es posible
     */
    public Conexion conectarPuntos(PuntoConexion punto1, PuntoConexion punto2) {
        if (punto1 != null && punto2 != null && punto1.puedeConectarCon(punto2)) {
            Conexion nuevaConexion = new Conexion(punto1, punto2);
            agregarConexion(nuevaConexion);
            return nuevaConexion;
        }
        return null;
    }
    
    /**
     * Encuentra el componente en una posición específica
     */
    public Componente getComponenteEnPosicion(int x, int y) {
        for (Componente componente : componentes) {
            if (componente.contiene(x, y)) {
                return componente;
            }
        }
        return null;
    }
    
    /**
     * Encuentra el punto de conexión más cercano a una posición
     */
    public PuntoConexion getPuntoConexionMasCercano(int x, int y, double distanciaMaxima) {
        PuntoConexion masCercano = null;
        double distanciaMinima = distanciaMaxima;
        
        for (Componente componente : componentes) {
            for (PuntoConexion punto : componente.getPuntosConexion()) {
                double distancia = Math.sqrt(Math.pow(punto.getX() - x, 2) + Math.pow(punto.getY() - y, 2));
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    masCercano = punto;
                }
            }
        }
        
        return masCercano;
    }
    
    /**
     * Encuentra la conexión más cercana a una posición
     */
    public Conexion getConexionMasCercana(int x, int y, double tolerancia) {
        for (Conexion conexion : conexiones) {
            if (conexion.estaCercaDe(x, y, tolerancia)) {
                return conexion;
            }
        }
        return null;
    }
    
    /**
     * Valida que el circuito sea consistente
     */
    public boolean validar() {
        // Verificar que hay al menos una fuente de voltaje
        boolean tieneFuente = componentes.stream()
                .anyMatch(c -> c instanceof FuenteVoltaje);
        
        if (!tieneFuente) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Limpia el circuito removiendo todos los componentes y conexiones
     */
    public void limpiar() {
        // Desconectar todas las conexiones primero
        for (Conexion conexion : new ArrayList<>(conexiones)) {
            removerConexion(conexion);
        }
        
        componentes.clear();
        conexiones.clear();
    }
    
    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Componente> getComponentes() { return new ArrayList<>(componentes); }
    public List<Conexion> getConexiones() { return new ArrayList<>(conexiones); }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    @Override
    public String toString() {
        return String.format("Circuito{%s, %d componentes, %d conexiones}", 
                           nombre, componentes.size(), conexiones.size());
    }
}