package com.wsxdev.simuladorcircuitos.modelo;

import java.io.Serializable;

public class Componente implements Serializable {
    protected int x, y, angulo;
    protected String tipo;

    public Componente(String tipo, int x, int y, int angulo) {
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.angulo = angulo;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getAngulo() { return angulo; }
    public String getTipo() { return tipo; }
}