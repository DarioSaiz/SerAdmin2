package com.example.seradmin.calendario;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private DateTime inicio;
    private DateTime Fin;
    private String titulo;
    private String color;
    private String id;

    public Event(DateTime inicio, DateTime fin, String titulo) {
        this.inicio = inicio;
        Fin = fin;
        this.titulo = titulo;
    }

    public Event(DateTime inicio, DateTime fin, String titulo, String color) {
        this.inicio = inicio;
        Fin = fin;
        this.titulo = titulo;
        this.color = color;
    }

    public Event(DateTime inicio, DateTime fin, String titulo, String color, String id) {
        this.inicio = inicio;
        Fin = fin;
        this.titulo = titulo;
        this.color = color;
        this.id = id;
    }

    public DateTime getInicio() {
        return inicio;
    }

    public void setInicio(DateTime inicio) {
        this.inicio = inicio;
    }

    public DateTime getFin() {
        return Fin;
    }

    public void setFin(DateTime fin) {
        Fin = fin;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
