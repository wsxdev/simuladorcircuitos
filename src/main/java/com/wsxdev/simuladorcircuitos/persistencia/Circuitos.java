package com.wsxdev.simuladorcircuitos.persistencia;

import com.wsxdev.simuladorcircuitos.modelo.Circuito;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Circuitos implements ICircuitos {

    private final String archivo = "circuitos.dat";

    @Override
    public void guardar(Circuito circuito) {
        List<Circuito> existentes = cargarTodos();
        existentes.add(circuito);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            out.writeObject(existentes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Circuito> cargarTodos() {
        File f = new File(archivo);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Circuito>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}