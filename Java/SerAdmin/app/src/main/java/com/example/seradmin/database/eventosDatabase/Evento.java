package com.example.seradmin.database.eventosDatabase;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.sql.Timestamp;

public class Evento implements Serializable {

    // Exclude previene que no lo escribamos, el id se autogenera
    @Exclude
    public String id;

    public String titulo;
    public String fechaInicio;
    public String horaInicio;
    public String fechaFin;
    public String horaFin;
    public String dni_cliente;
    public float latitud;
    public float longitud;
    public String descripcion;
    public Timestamp inicio;
    public Timestamp fin;

    public Evento(){
    }

    public Evento(String titulo, String fechaInicio, String fechaFin, String horaInicio,
                  String horaFin, float latitud, float longitud, String descripcion){
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;

    }

    public Evento(String id, String dni_cliente, String titulo, String fechaInicio, String fechaFin, String horaInicio,
                  String horaFin, float latitud, float longitud, String descripcion){
        this.id = id;
        this.dni_cliente = dni_cliente;
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;

    }

    public Evento(String titulo, String fechaInicio, String fechaFin){
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDni_cliente() {
        return dni_cliente;
    }

    public void setDni_cliente(String dni_cliente) {
        this.dni_cliente = dni_cliente;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
