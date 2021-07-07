package com.example.application.data.entity;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.example.application.data.AbstractEntity;

@Entity
public class Sector extends AbstractEntity {
	
	@NotNull(message = "Name must not be null")
	@NotEmpty(message = "Name must not be VACIO")
    private String nombre;
	@NotNull(message = "Name must not be null")
	@NotEmpty(message = "Name must not be VACIO")
    private String abreviacion;

    @OneToMany(targetEntity = Modulo.class,fetch = FetchType.EAGER)    
    @JoinColumn(name="sector_id")
    private List<Modulo> moduloList;
    
    public String getAbreviacion() {
		return abreviacion;
	}
	public void setAbreviacion(String abreviacion) {
		this.abreviacion = abreviacion;
	}
	public String getNombre() {
        return nombre;
    }
	
	
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getModulos() {    	
    	return moduloList.stream().map(m -> {return m.getId()+" "+m.getNombre();}).collect(Collectors.joining());
    }

}
