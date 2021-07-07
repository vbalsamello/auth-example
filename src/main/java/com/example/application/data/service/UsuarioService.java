package com.example.application.data.service;

import java.util.List;
import java.util.logging.Level;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import com.example.application.data.entity.Mascota;
import com.example.application.data.entity.Usuario;

@Service
public class UsuarioService extends CrudService<Usuario, Integer> {

	private static final Logger LOGGER = Logger.getLogger(UsuarioService.class.getName());
	private UsuarioRepositorio usuarioRepository;
	
	public UsuarioService(UsuarioRepositorio usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}
		
    protected UsuarioRepositorio getRepository() {
        return usuarioRepository;
    }
	public List<Usuario> findAll(){
		return usuarioRepository.findAll();
	}
	
	public int count() {
		return (int) usuarioRepository.count();
	}
	
	public void delete(Usuario usuario) {
		usuarioRepository.delete(usuario);
	}
	
	public void save(Usuario usuario) {
		if(usuario == null) {
			//LOGGER.log(Level.SEVERE, "el usurio es nulo");
			return;
		}else {
			usuarioRepository.save(usuario);
		}
	}
	
	public Usuario getUsuario(String username) {
		return usuarioRepository.getByUsername(username);
	}
	
}
