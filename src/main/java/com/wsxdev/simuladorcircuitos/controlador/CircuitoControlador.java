package com.wsxdev.simuladorcircuitos.controlador;

import com.wsxdev.simuladorcircuitos.modelo.*;
import com.wsxdev.simuladorcircuitos.persistencia.*;
import com.wsxdev.simuladorcircuitos.simulacion.SimuladorCircuito;

import java.io.*;
import java.util.List;

/**
 * Controlador principal para la gestión de circuitos
 */
public class CircuitoControlador {

    private final ICircuitos dao;
    private final SimuladorCircuito simulador;

    public CircuitoControlador() {
        dao = new Circuitos();
        simulador = new SimuladorCircuito();
    }

    /**
     * Guarda un circuito usando el DAO
     */
    public void guardarCircuito(Circuito circuito) {
        dao.guardar(circuito);
    }
    
    /**
     * Guarda un circuito en un archivo específico
     */
    public void guardarCircuito(Circuito circuito, String rutaArchivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(circuito);
        }
    }

    /**
     * Carga todos los circuitos guardados
     */
    public List<Circuito> cargarCircuitos() {
        return dao.cargarTodos();
    }
    
    /**
     * Carga un circuito desde un archivo específico
     */
    public Circuito cargarCircuito(String rutaArchivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            return (Circuito) ois.readObject();
        }
    }
    
    /**
     * Ejecuta la simulación del circuito
     */
    public void simularCircuito(Circuito circuito) throws Exception {
        simulador.simular(circuito);
    }
    
    /**
     * Valida que el circuito sea válido para simulación
     */
    public boolean validarCircuito(Circuito circuito) {
        return simulador.validarCircuito(circuito);
    }
    
    /**
     * Obtiene los resultados de la última simulación
     */
    public String obtenerResultadosSimulacion() {
        return simulador.obtenerResultados();
    }
}