package com.example.application.data.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.data.entity.Usuario;


public interface UsuarioRepositorio extends JpaRepository<Usuario,Integer> {
	
	Usuario getByUsername(String username);

}
