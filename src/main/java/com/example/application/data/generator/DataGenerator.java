package com.example.application.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;

import com.example.application.data.service.MascotaRepository;
import com.example.application.data.service.UsuarioRepositorio;
import com.example.application.data.entity.Mascota;
import com.example.application.data.entity.Role;
import com.example.application.data.entity.Usuario;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.vaadin.artur.exampledata.DataType;
import org.vaadin.artur.exampledata.ExampleDataGenerator;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(MascotaRepository mascotaRepository, UsuarioRepositorio usuarioRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (mascotaRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Mascota entities...");
            ExampleDataGenerator<Mascota> mascotaRepositoryGenerator = new ExampleDataGenerator<>(Mascota.class,
                    LocalDateTime.of(2021, 5, 20, 0, 0, 0));
            mascotaRepositoryGenerator.setData(Mascota::setId, DataType.ID);
            mascotaRepositoryGenerator.setData(Mascota::setNombre, DataType.FIRST_NAME);
            mascotaRepositoryGenerator.setData(Mascota::setRaza, DataType.OCCUPATION);
            mascotaRepositoryGenerator.setData(Mascota::setEdad, DataType.NUMBER_UP_TO_100);
            mascotaRepository.saveAll(mascotaRepositoryGenerator.create(100, seed));

            usuarioRepository.save(new Usuario("user","u",Role.USER));
            usuarioRepository.save(new Usuario("admin","a",Role.ADMIN));
            logger.info("Generated demo data");
        };
    }

}