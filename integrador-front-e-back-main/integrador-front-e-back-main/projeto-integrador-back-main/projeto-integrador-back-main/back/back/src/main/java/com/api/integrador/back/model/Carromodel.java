package com.api.integrador.back.model;

import com.api.integrador.back.dto.CarroDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "venda")
public class Carromodel {

	// ate hoje nao sei que merda esse Generated faz
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	// atributos
	private int id;
	private String imagemUrl;
	private String modelo;
	private String ano;
	private String preco;
	private String cor;
	private String placa;
	
 
	public Carromodel() {
	 
	}
 
	public Carromodel(int id, String modelo, String ano, String preco, String cor, String placa) {
		this.id = id;
		this.modelo = modelo;
		this.ano = ano;
		this.preco = preco;
		this.cor = cor;
		this.placa = placa;
		this.imagemUrl = imagemUrl;
	}
 
	// construtor
	public  Carromodel(CarroDTO d) {

		this.modelo = d.modelo();
		this.ano = d.ano();
		this.preco = d.preco();
		this.cor = d.cor();
		this.placa = d.placa();
		this.imagemUrl = d.imagemUrl();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getPreco() {
		return preco;
	}

	public void setPreco(String preco) {
		this.preco = preco;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getImagemUrl() {
		return imagemUrl;
	}

	public void setImagemUrl(String imagemUrl) {
		this.imagemUrl = imagemUrl;
	}

 

}