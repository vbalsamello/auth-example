package com.example.application.data.service;

import com.example.application.data.entity.Modulo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class ModuloService extends CrudService<Modulo, Integer> {

    private ModuloRepository repository;

    public ModuloService(@Autowired ModuloRepository repository) {
        this.repository = repository;
    }

    @Override
    protected ModuloRepository getRepository() {
        return repository;
    }

    public List<Modulo> findAll(){
		return repository.findAll();
	}
	
	public int count() {
		return (int) repository.count();
	}
	
	public void delete(Modulo mascota) {
		repository.delete(mascota);
	}
	
	public void save(Modulo modulo) {
		if(modulo == null) {
			//LOGGER.log(Level.SEVERE, "el usurio es nulo");
			return;
		}else {
			repository.save(modulo);
		}
	}
	
	public Modulo getById(Integer id) {
		return repository.getById(id);
	}
}
