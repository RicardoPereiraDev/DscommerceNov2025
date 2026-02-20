package com.devsuperior.dscommerce.services.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    //Aqui em baixo estou a exigir que esse constructor exiba uma mensagem na hora de instanciar o objecto
    public ResourceNotFoundException(String msg){

        super(msg);

    }
}
