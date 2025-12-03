package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

//Retorno do metodo vai ser um DTO porque o na camada de Service ele depois devolve para o Controller por isso ser ProductDTO
    //Service depende do Repository e o Controller depende do Service
    //É aqui no service que vou implementar aquela busca no banco de dados
    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Optional<Product> result = repository.findById(id);//Busquei no banco de dados o produto com esse Id que recebi como argumento.
        Product product = result.get();//Aqui peguei o produto
        ProductDTO dto = new ProductDTO(product); //Copio os dados do product para um novo ProductDTO e vou converter o produto para o DTO
        return dto;


        /*De forma resumida o codigo supra

        Product product = repository.findById(id).get();
        return new ProductDTO(product);
        */



    }
}
