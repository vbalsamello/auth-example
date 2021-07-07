package com.example.application.data.service;

import com.example.application.data.entity.Modulo;
import com.example.application.data.entity.Sector;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class SectorService extends CrudService<Sector, Integer> {

    private static SectorRepository repository;
    private static SectorService instance;

    public SectorService(@Autowired SectorRepository repository) {
        this.repository = repository;
    }

    @Override
    protected SectorRepository getRepository() {
        return repository;
    }

    /**
	 * @return a reference to an example facade for Customer objects.
	 */
	public static SectorService getInstance() {
		if (instance == null) {
			instance = new SectorService(repository);
		}
		return instance;
	}
	
    public List<Sector> findAll(){
		return repository.findAll();
	}
	
	public int count() {
		return (int) repository.count();
	}
	
	public void delete(Sector sector) {
		repository.delete(sector);
	}
	
	public void save(Sector sector) {
		if(sector == null) {
			//LOGGER.log(Level.SEVERE, "el usurio es nulo");
			return;
		}else {
			repository.save(sector);
		}
	}
	
	public Sector getById(Integer id) {
		return repository.getById(id);
	}
}
