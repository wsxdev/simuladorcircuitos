package com.wsxdev.simuladorcircuitos.controlador;

import com.wsxdev.simuladorcircuitos.modelo.*;
import com.wsxdev.simuladorcircuitos.persistencia.*;

import java.util.List;

public class CircuitoControlador {

    private final ICircuitos dao;

    public CircuitoControlador() {
        dao = new Circuitos();
    }

    public void guardarCircuito(Circuito circuito) {
        dao.guardar(circuito);
    }

    public List<Circuito> cargarCircuitos() {
        return dao.cargarTodos();
    }
}