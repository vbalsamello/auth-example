package com.example.application.data.service;

import com.example.application.data.entity.Sector;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Integer> {

	Sector getById(Integer id);
}