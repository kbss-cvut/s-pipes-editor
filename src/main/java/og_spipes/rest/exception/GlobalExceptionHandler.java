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
    public ResponseEntity<Map<String, Object>> handleModuleDependencyException(ModuleDependencyException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "BAD_REQUEST");
        errorResponse.put("module", ex.getURI());
        errorResponse.put("script", ex.getScript());
        if (ex.getSubscript() != null) {
            errorResponse.put("subscript", ex.getSubscript());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}