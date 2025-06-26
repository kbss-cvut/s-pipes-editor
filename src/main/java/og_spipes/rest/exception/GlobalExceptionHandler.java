package og_spipes.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ModuleDependencyException.class)
    public ResponseEntity<Map<String, String>> handleModuleDependencyException(ModuleDependencyException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("script", ex.getScript());
        response.put("subscript", ex.getFoundDependencySubscript());
        response.put("dependee", ex.getDependeeModule());
        response.put("dependant", ex.getDependantModule());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}