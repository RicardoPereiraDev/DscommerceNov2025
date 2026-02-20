package com.devsuperior.dscommerce.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError { //aqui foi criada uma subclasse de CustomError

    //Esta classe vai ter tudo o que o CustomError tem e mais uma lista de erros
    private List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Instant timestamp, Integer status, String error, String path) {
        super(timestamp, status, error, path);
       //this.errors = errors;
    }

    public List<FieldMessage> getErrors() {
        return errors;
    }

    //Metodo abaixo para adicionar a lista de erros, vou add um elemento nessa lista fieldMessage englobando fieldName e message;
    public void addError(String fieldName, String message){
        errors.add(new FieldMessage(fieldName, message));
    }

}
