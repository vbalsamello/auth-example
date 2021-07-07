package com.example.application.data.service;

import com.example.application.data.entity.Mascota;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

	Mascota getById(Integer id);

}