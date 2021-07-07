package com.example.application.data.entity;

import javax.persistence.Entity;

import com.example.application.data.AbstractEntity;

@Entity
public class Mascota extends AbstractEntity {

    private String nombre;
    private String raza;
    private Integer edad;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getRaza() {
        return raza;
    }
    public void setRaza(String raza) {
        this.raza = raza;
    }
    public Integer getEdad() {
        return edad;
    }
    public void setEdad(Integer edad) {
        this.edad = edad;
    }

}
