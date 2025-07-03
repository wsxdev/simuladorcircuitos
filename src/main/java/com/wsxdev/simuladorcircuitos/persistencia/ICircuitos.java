package com.wsxdev.simuladorcircuitos.persistencia;

import com.wsxdev.simuladorcircuitos.modelo.Circuito;
import java.util.List;

public interface ICircuitos {
    void guardar(Circuito circuito);
    List<Circuito> cargarTodos();
}