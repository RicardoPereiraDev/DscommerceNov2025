package com.devsuperior.dscommerce.controllers.handlers;

import com.devsuperior.dscommerce.dto.CustomError;
import com.devsuperior.dscommerce.dto.ValidationError;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

//Aqui nesta class é onde vou manipular o tratamento das excepções que depois consigo ver as respostas personalizadas sobre validação
@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;//404
        CustomError err = new CustomError(Instant.now(),status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> database(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;//400 - Bad Request
        CustomError err = new CustomError(Instant.now(),status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    //A minha resposta aqui em baixo é um CustomError e como o CustomerError é um super tipo do ValidationError, eu poderei instanciar aqui um ValidationError e ele casa com o super tipo = upCasting
    //Só que agora o ValidationError ele aceita inserir elementos naquela lista de erros

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentNotValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;//422
        ValidationError err = new ValidationError(Instant.now(),status.value(), "Dados inválidos", request.getRequestURI());

        //Agora vou ter que percorrer o objecto (e) que é MethodArgumentNotValidException e, vou ter que percorrer esse objecto transformando cada erro que tiver dentro do objecto para esse meu err.addError

        // é um for que vai percorrer todos os erros que estiverem aqui nesta lista de excepção chamando e dando aqui o apelido de f
        // Para cada um desses erros que é aqui o f eu vou rodar este comando de addError
        for(FieldError f: e.getBindingResult().getFieldErrors()){
            err.addError(f.getField(),f.getDefaultMessage());
        }
        //err.addError("name", "Mensagem de teste");
        //err.addError("price", "Preço inválido");
        return ResponseEntity.status(status).body(err);
    }




}
