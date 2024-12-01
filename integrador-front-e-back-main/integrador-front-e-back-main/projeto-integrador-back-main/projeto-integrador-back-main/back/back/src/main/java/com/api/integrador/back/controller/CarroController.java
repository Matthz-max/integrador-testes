package com.api.integrador.back.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.integrador.back.dto.CarroDTO;
import com.api.integrador.back.model.Carromodel;
import com.api.integrador.back.repository.CarroRepository;

@CrossOrigin(origins = "http://127.0.0.1:5500", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("carro")  
public class CarroController {
 
	private static String caminhoImagens = "\\Users\\Paula\\Downloads\\integrador-front-e-back-main\\integrador-front-e-back-main\\projeto-integrador-back-main\\projeto-integrador-back-main\\back\\back\\src\\main\\resources\\static";
	
    @Autowired
    private CarroRepository repo; 

    @GetMapping("/listar")
    public ResponseEntity<?> mostrar(){
    	List<Carromodel> lista = repo.findAll();
    	return ResponseEntity.ok(lista);
    }
    // Endpoint para criar o carro
    @PostMapping("/criar")
    public ResponseEntity<Carromodel> criarcarro(@RequestBody CarroDTO carro ,Carromodel teste, BindingResult result,
			@RequestParam("file") MultipartFile arquivo) {
    	 Carromodel savedCarro = new Carromodel(carro); 
    	
    	try {
    		if (!arquivo.isEmpty()) {
    			byte[] bytes = arquivo.getBytes();
    			Path caminho = Paths
    					.get(caminhoImagens + String.valueOf(savedCarro.getId()) + arquivo.getOriginalFilename());
    			Files.write(caminho, bytes);
    			
    			savedCarro.setImagemUrl(String.valueOf(savedCarro.getId()) + arquivo.getOriginalFilename());
    			repo.saveAndFlush(savedCarro);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	repo.save(savedCarro);
    	return ResponseEntity.status(HttpStatus.CREATED).body(savedCarro);  
    }
    	 
    	  
    // Endpoint para atualizar o carro
    @PutMapping("/atualizar/{id}")  
    public ResponseEntity<Carromodel> atualizarcarro(@PathVariable int id, @RequestBody Carromodel carro) {
        Optional<Carromodel> existingCarro = repo.findById(id);

        if (!existingCarro.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
        }

        Carromodel carroToUpdate = existingCarro.get();
      
        carroToUpdate.setModelo(carro.getModelo());
        carroToUpdate.setAno(carro.getAno());
        carroToUpdate.setPreco(carro.getPreco());
        carroToUpdate.setCor(carro.getCor());
        carroToUpdate.setPlaca(carro.getPlaca());
 
        Carromodel updatedCarro = repo.save(carroToUpdate);  
        
        return ResponseEntity.ok(updatedCarro); 
    }



    // Endpoint para excluir o carro
    @DeleteMapping("/delete/{id}")
    public ResponseEntity  <Carromodel> deletarCarro(@PathVariable int id) {
        Optional<Carromodel> existingCarro = repo.findById(id);
        
        if (!existingCarro.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();   
        }	

        repo.deleteById(id);   
        return ResponseEntity.noContent().build();  
    }
}
 