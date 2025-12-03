package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    //Recurso é o conceito, controlador é a forma de implementar esse conceito, então o controlador implementa um recurso na minha API REST

    @Autowired
    private ProductRepository repository;
    @GetMapping
    public String teste(){
      Optional<Product> result = repository.findById(1L);
      Product product = result.get(); //este result.get pega o objecto, que neste caso é o produto que está dentro do Optional
      return product.getName(); //para retornar o nome do produto que está dentro do Id 1
    }
}
