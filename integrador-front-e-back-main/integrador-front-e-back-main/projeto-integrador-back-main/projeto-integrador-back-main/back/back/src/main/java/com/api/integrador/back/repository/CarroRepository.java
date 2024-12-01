package com.api.integrador.back.repository;
 

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.integrador.back.dto.CarroDTO;
import com.api.integrador.back.model.Carromodel;

public interface CarroRepository extends JpaRepository<Carromodel, Integer> {

	 
 
}