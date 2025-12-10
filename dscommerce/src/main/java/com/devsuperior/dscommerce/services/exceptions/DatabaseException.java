package com.devsuperior.dscommerce.services.exceptions;

public class DatabaseException extends RuntimeException{

    //Aqui em baixo estou a exigir que esse metodo exiba uma mensagem na hora de instanciar o objecto
    public DatabaseException(String msg){

        super(msg);

    }
}
