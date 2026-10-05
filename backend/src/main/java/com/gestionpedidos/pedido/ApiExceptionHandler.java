package com.gestionpedidos.pedido;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

/**
 * T007 / research D1 — errores en formato ProblemDetail (RFC 7807)
 * con el mapa por campo `properties.errors` (FR-003).
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PedidoNotFoundException.class)
    public ProblemDetail noEncontrado(PedidoNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(ProblemDetail.forStatus(HttpStatus.NOT_FOUND).getType());
        pd.setTitle("Recurso no encontrado");
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacion(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validación fallida");
        pd.setType(ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).getType());
        pd.setTitle("Error de validación");
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.put(fe.getField(), fe.getDefaultMessage());
        }
        pd.setProperty("errors", errores);
        return pd;
    }
}
