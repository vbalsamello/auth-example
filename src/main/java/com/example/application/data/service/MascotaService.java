package com.example.application.data.service;

import com.example.application.data.entity.Mascota;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class MascotaService extends CrudService<Mascota, Integer> {

	private static final Logger LOGGER = Logger.getLogger(MascotaService.class.getName());
    private MascotaRepository mascotaRepository;

    public MascotaService(@Autowired MascotaRepository repository) {
        this.mascotaRepository = repository;
    }

    @Override
    protected MascotaRepository getRepository() {
        return mascotaRepository;
    }
		
	
	public List<Mascota> findAll(){
		return mascotaRepository.findAll();
	}
	
	public int count() {
		return (int) mascotaRepository.count();
	}
	
	public void delete(Mascota mascota) {
		mascotaRepository.delete(mascota);
	}
	
	public void save(Mascota mascota) {
		if(mascota == null) {
			//LOGGER.log(Level.SEVERE, "el usurio es nulo");
			return;
		}else {
			mascotaRepository.save(mascota);
		}
	}
	
	public Mascota getById(Integer id) {
		return mascotaRepository.getById(id);
	}
}
