package com.example.seradmin;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Gestor implements Serializable {

    // Exclude previene que no lo escribamos, el id se autogenera
    @Exclude
    public String id;

    private String Apellido;
    private String Contraseña;
    private String DNI;
    private String Nombre;
    private String Num_Telf;

    public Gestor(String id, String dni, String contraseña, String nombre, String apellido, String num_telf) {
        this.id = id;
        Apellido = apellido;
        Contraseña = contraseña;
        DNI = dni;
        Nombre = nombre;
        Num_Telf = num_telf;
    }

    public Gestor(String dni, String contraseña, String nombre, String apellido, String num_telf) {
        Apellido = apellido;
        Contraseña = contraseña;
        DNI = dni;
        Nombre = nombre;
        Num_Telf = num_telf;
    }

    public Gestor() {}

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getContraseña() {
        return Contraseña;
    }

    public void setContraseña(String contraseña) {
        Contraseña = contraseña;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String dni) {
        DNI = dni;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getNum_Telf() {
        return Num_Telf;
    }

    public void setNum_Telf(String num_telf) {
        Num_Telf = num_telf;
    }

    @Override
    public String toString() {
        return "Gestor{" +
                "Apellido='" + Apellido + '\'' +
                ", Contraseña='" + Contraseña + '\'' +
                ", DNI='" + DNI + '\'' +
                ", Nombre='" + Nombre + '\'' +
                ", Num_Telf='" + Num_Telf + '\'' +
                '}';
    }

}

