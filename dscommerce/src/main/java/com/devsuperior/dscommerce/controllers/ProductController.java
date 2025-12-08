package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Autowired
    private ProductService service;

    //Recurso é o conceito, controlador é a forma de implementar esse conceito, então o controlador implementa um recurso na minha API REST

    @GetMapping(value = "/{id}") //metodo em baixo para retornar um ProductDTO
    public ProductDTO findById(@PathVariable Long id){
        return service.findById(id);



      //Optional<Product> result = repository.findById(1L);
      //Product product = result.get(); //este result.get pega o objecto, que neste caso é o produto que está dentro do Optional
      //return product.getName(); //para retornar o nome do produto que está dentro do Id 1
    }

    @GetMapping
    public Page<ProductDTO> findAll(Pageable pageable) {
        return service.findAll(pageable);

    }

    }
