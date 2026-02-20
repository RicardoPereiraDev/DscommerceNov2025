package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.Optional;

@Service //service
public class ProductService {

//Retorno do metodo vai ser um DTO porque o na camada de Service ele depois devolve para o Controller por isso ser ProductDTO
    //Service depende do Repository e o Controller depende do Service
    //É aqui no service que vou implementar aquela busca no banco de dados
    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
       // Optional<Product> result = repository.findById(id);//Busquei no banco de dados o produto com esse Id que recebi como argumento.
        //Product product = result.get();//Aqui peguei o produto
        //ProductDTO dto = new ProductDTO(product); //Copio os dados do product para um novo ProductDTO e vou converter o produto para o DTO
        //return dto;

        Product product = repository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Recurso não encontrado"));// este orElseThrow vai tentar acessar a um objecto e caso não encontre vou lançar uma excepção
        return new ProductDTO(product);


        /*De forma resumida o codigo supra

        Product product = repository.findById(id).get();
        return new ProductDTO(product);
        */
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(String name, Pageable pageable) {
        Page<Product> result = repository.searchByName(name,pageable);
        //Agora na linha de baixo, vamos ter que converter a minha lista de Produtos para uma lista de ProductDTO e para isso usamos o lambda e explico o metodo abaixo
        //Para cada registo da minha lista original eu vou chamar o meu new ProductDTO recebendo x
        return result.map(x ->new ProductDTO(x));
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {

        //Copiar para a entidade do Product e depois salvar os dados que vieram no ProductDTO dto, basicamente estou preparando a minha entidade

        //Preparar o objecto
        Product entity = new Product();
        //Depois copiamos os dados aqui do dto
        copyDtoToEntity(dto, entity);
        //salvamos os dados que copiamos do dto
        entity= repository.save(entity);//salvo a entidade no banco e obtenho uma nova referencia aqui para ela e salvo na mesma variavel
        //Depois por fim retornamos o objecto salvo atualizado
        return new ProductDTO(entity); //retornar um novo ProductDTO a apartir desta entity, para reconverter para DTO e retornar aqui no meu metodo "insert".

    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {

        try{
            //Só vou instaciar o produto com a referencia do Id que eu passar como argumento, ela não vai no banco de dados
            Product entity = repository.getReferenceById(id);
            //Depois copiamos os dados aqui do dto para entity
            copyDtoToEntity(dto, entity);
            //salvamos os dados que copiamos do dto
            entity= repository.save(entity);//salvo a entidade no banco e obtenho uma nova referencia aqui para ela e salvo na mesma variavel
            //Depois por fim retornamos o objecto salvo atualizado
            return new ProductDTO(entity); //retornar um novo ProductDTO a apartir desta entity, para reconverter para DTO e retornar aqui no meu metodo "update".
        }
        catch(EntityNotFoundException e){
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try{
            repository.deleteById(id);
        }
        catch(DataIntegrityViolationException e){
            throw new DatabaseException("Falha de integridade referencial");
        }

    }

    //Metdo abaixo é private pk é um metodo interno e não preciso de expor para fora
    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
    }
}
