package com.example.application.data.service;

import com.example.application.data.entity.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuloRepository extends JpaRepository<Modulo, Integer> {

	Modulo getById(Integer id);
}