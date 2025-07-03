package com.wsxdev.simuladorcircuitos.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Circuito implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private List<Componente> componentes;

    // Constructor original
    public Circuito(String nombre) {
        this.nombre = nombre;
        this.componentes = new ArrayList<>();
    }

    // ✅ NUEVO CONSTRUCTOR: el que necesita PanelCircuito
    public Circuito(List<Componente> componentes) {
        this.nombre = "CircuitoSinNombre"; // o puedes dejarlo null
        this.componentes = componentes;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Componente> getComponentes() {
        return componentes;
    }

    public void agregarComponente(Componente c) {
        componentes.add(c);
    }
}