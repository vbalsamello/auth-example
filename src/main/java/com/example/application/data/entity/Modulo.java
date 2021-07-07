package com.example.application.data.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.application.data.AbstractEntity;

@Entity
public class Modulo extends AbstractEntity {

    private String nombre;
    private String descripcion;
    private Nivel nivel;
    
    @ManyToOne
    @JoinColumn(name = "sector_id")
    private Sector sector;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Sector getSector() {
        return sector;
    }
    public String getSectorNombre() {
    	if(sector == null){
    		return "S/A";
    	}else {    		
    		return sector.getNombre();
    	}
    }
    public void setSector(Sector sector) {
        this.sector = sector;
    }
    public Nivel getNivel() {
        return nivel;
    }
    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

}
