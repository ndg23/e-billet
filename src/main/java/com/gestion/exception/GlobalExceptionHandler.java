package com.gestion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import com.gestion.exception.EventNotFoundException;
import com.gestion.dto.ValidationErrorResponse;
import com.gestion.dto.ValidationErrorResponse.FieldError;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        BindingResult result = ex.getBindingResult();
        
        result.getFieldErrors().forEach(error -> {
            response.addError(error.getField(), error.getDefaultMessage());
        });
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(EventNotFoundException.class)
    public String handleEventNotFound(
            EventNotFoundException ex, 
            RedirectAttributes redirectAttributes) {
//        log.error("Event not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/events";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGenericError(
            Exception ex, 
            RedirectAttributes redirectAttributes) {
//        log.error("Unexpected error occurred", ex);
        redirectAttributes.addFlashAttribute("error", "Une erreur est survenue: " + ex.getMessage());
        return "redirect:/events";
    }
}