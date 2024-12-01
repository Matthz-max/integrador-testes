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
import org.springframework.http.HttpHeaders;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("carro")  
public class CarroController {
 
	private static final String caminhoImagens = "src/main/resources/static/imagens";
	
    @Autowired
    private CarroRepository repo; 
    
    
    
    @CrossOrigin(origins = "*")
    @GetMapping("/imagens/{imagemNome}")
    public ResponseEntity<byte[]> getImagem(@PathVariable String imagemNome) throws IOException {
        Path caminho = Paths.get("src/main/resources/static/imagens/" + imagemNome);
        byte[] imageBytes = Files.readAllBytes(caminho);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");  // Ou o tipo adequado para sua imagem

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

     
    @GetMapping("/listar")
    public ResponseEntity<?> mostrar(){
    	List<Carromodel> lista = repo.findAll();
    	return ResponseEntity.ok(lista);
    }
    
    // Endpoint para criar o carro
    @PostMapping("/criar")
    public ResponseEntity<Carromodel> criarCarro(@RequestParam("modelo") String modelo,
            @RequestParam("ano") String ano,
            @RequestParam("preco") String preco,
            @RequestParam("cor") String cor,
            @RequestParam("placa") String placa,
            @RequestParam(value = "imagemUrl", required = false) String imagemUrl,
            @RequestParam(value = "file", required = false) MultipartFile arquivo){

        CarroDTO carroDTO = new CarroDTO(modelo, ano, preco, cor, placa, imagemUrl); 
        Carromodel savedCarro = new Carromodel(carroDTO);

        try {
            if (!arquivo.isEmpty()) {
                byte[] bytes = arquivo.getBytes();
         
                String caminhoImagem = "/imagens/" + savedCarro.getId() + arquivo.getOriginalFilename();
                Path caminho = Paths.get("src/main/resources/static" + caminhoImagem);

                Files.write(caminho, bytes);
                savedCarro.setImagemUrl(caminhoImagem);
 
            }

            savedCarro = repo.saveAndFlush(savedCarro);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCarro);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
  
    // Endpoint para atualizar o carro
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Carromodel> atualizarCarro(@PathVariable int id,
                                                     @RequestParam("modelo") String modelo,
                                                     @RequestParam("ano") String ano,
                                                     @RequestParam("preco") String preco,
                                                     @RequestParam("cor") String cor,
                                                     @RequestParam("placa") String placa,
                                                     @RequestParam("imagemUrl") String imagemUrl,
                                                     @RequestParam(value = "file", required = false) MultipartFile arquivo) {
        Optional<Carromodel> existingCarro = repo.findById(id);

        if (!existingCarro.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Carromodel carroToUpdate = existingCarro.get();

        carroToUpdate.setModelo(modelo);
        carroToUpdate.setAno(ano);
        carroToUpdate.setPreco(preco);
        carroToUpdate.setCor(cor);
        carroToUpdate.setPlaca(placa);

        // Se houver uma imagem nova, salva a imagem
        if (arquivo != null && !arquivo.isEmpty()) {
            try {
                byte[] bytes = arquivo.getBytes();
                Path caminho = Paths.get(caminhoImagens + "\\" + carroToUpdate.getId() + arquivo.getOriginalFilename());
                Files.write(caminho, bytes);

                carroToUpdate.setImagemUrl(carroToUpdate.getId() + arquivo.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

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
 