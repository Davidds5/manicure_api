package br.com.davidds5.manicure_api.util;

import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    public static <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(201).body(body);
    }

    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static ResponseEntity<Void> notFound() {
        return ResponseEntity.notFound().build();
    }
}