package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Autowired
    private ProductService service;

    //Recurso é o conceito, controlador é a forma de implementar esse conceito, então o controlador implementa um recurso na minha API REST

    @GetMapping(value = "/{id}") //metodo em baixo para retornar um ProductDTO
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id){
        ProductDTO productDTO = service.findById(id);
        return ResponseEntity.ok(productDTO);// 200 OK

      //Optional<Product> result = repository.findById(1L);
      //Product product = result.get(); //este result.get pega o objecto, que neste caso é o produto que está dentro do Optional
      //return product.getName(); //para retornar o nome do produto que está dentro do Id 1
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable) {
        Page<ProductDTO> productDTO = service.findAll(pageable);
        return ResponseEntity.ok(productDTO);

    }


    @PostMapping
    // Com esse RequestBody, o corpo da requisição qque eu enviar vai entrar nesse parametro do RequestBody e vai instaciar o ProductDTO correspondente
    public ResponseEntity<ProductDTO> insert(@RequestBody ProductDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto); //quando vc cria um recurso, na resposta, além de dar o codigo created(201) no cabeçalho da resposta vai ter o link para o recurso criado que é esse uri


        }
    }
