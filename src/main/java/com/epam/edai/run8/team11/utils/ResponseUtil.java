package com.epam.edai.run8.team11.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResponseUtil {

    private static final String MESSAGE = "message";

    public ResponseEntity<Map<String, Object>> buildInternalServerResponse(Map<String, Object> body){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    public ResponseEntity<Map<String, Object>> buildInternalServerResponse(String message){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(MESSAGE, message, "error", true));
    }

    public ResponseEntity<Map<String, Object>> buildBadRequestResponse(Map<String, Object> body){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    public ResponseEntity<Map<String, Object>> buildBadRequestResponse(String message){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(MESSAGE, message, "error", true));
    }

    public ResponseEntity<Map<String, Object>> buildOkResponse(Map<String, Object> body){
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    public ResponseEntity<Map<String, Object>> buildOkResponse(String message){
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(MESSAGE, message));
    }

    public ResponseEntity<Map<String, Object>> buildConflictResponse(Map<String, Object> body){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    public ResponseEntity<Map<String, Object>> buildConflictResponse(String message){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(MESSAGE, message, "error", true));
    }

    public ResponseEntity<Map<String, Object>> buildCreateResponse(Map<String, Object> body){
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    public ResponseEntity<Map<String, Object>> buildCreateResponse(String message){
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(MESSAGE, message));
    }

    public ResponseEntity<Map<String, Object>> buildNotFound(String message){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(MESSAGE, message, "error", true));
    }

    public ResponseEntity<Map<String, Object>> buildNotFound(Map<String, Object> body){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    public ResponseEntity<Map<String, Object>> buildUnauthorized(String message){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(MESSAGE,message, "error", true));
    }
}
