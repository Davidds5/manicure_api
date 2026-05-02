package br.com.davidds5.manicure_api.exceptions;

import org.springframework.security.access.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.core.AuthenticationException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        
        
        StandardError error = new StandardError(
        LocalDateTime.now(),
        status.value(), 
        "Recurso nao encontrado", 
        e.getMessage(),
        request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError error = new StandardError(LocalDateTime.now(), 
        status.value(),
        "Erro de negocio", 
        ex.getMessage(), 
        request.getRequestURI()
        );
    
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ValidationError error = new ValidationError(
            LocalDateTime.now(), 
            status.value(), 
            "Validacao falhou", 
            "Um ou mais campos estao invalidos", 

            request.getRequestURI()
        );
    
        for(FieldError x : ex.getBindingResult().getFieldErrors()){
            error.addError(x.getField(), x.getDefaultMessage());
        
        }

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGenericException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        StandardError error = new StandardError(
        LocalDateTime.now(), 
        status.value(),
        "Erro interno do servidor", 
        ex.getMessage(), 
        request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.FORBIDDEN; // 403 

        StandardError error = new StandardError(
            LocalDateTime.now(),
            status.value(),
            "Acesso negado",
            "Voce nao tem permissao para acessar este recurso."
            request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardError> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401 

        StandardError error = new StandardError(
            LocalDateTime.now(),
            status.value(), 
            "Falha na autenticacao", 
            "Email ou senha incorretos. Verifique suas credenciais."
            request.getRquestsURI()
        );
        return ResponseEntity.status(status).body(error);
    }
}


