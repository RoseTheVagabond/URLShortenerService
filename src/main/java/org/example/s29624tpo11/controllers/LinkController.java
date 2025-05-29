package org.example.s29624tpo11.controllers;

import jakarta.validation.Valid;
import org.example.s29624tpo11.DTOs.CreateLinkDTO;
import org.example.s29624tpo11.DTOs.ResponseDTO;
import org.example.s29624tpo11.DTOs.UpdateLinkDTO;
import org.example.s29624tpo11.exceptions.LinkNotFoundException;
import org.example.s29624tpo11.exceptions.WrongPasswordException;
import org.example.s29624tpo11.services.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class LinkController {

    private final LinkService linkService;

    @Autowired
    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/api/links")
    public ResponseEntity<ResponseDTO> createLink(@Valid @RequestBody CreateLinkDTO request) {
        ResponseDTO response = linkService.createLink(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/api/links/{id}")
    public ResponseEntity<ResponseDTO> getLink(@PathVariable String id) {
        try {
            ResponseDTO response = linkService.getLinkById(id);
            return ResponseEntity.ok(response);
        } catch (LinkNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/api/links/{id}")
    public ResponseEntity<Void> updateLink(@PathVariable String id, @Valid @RequestBody UpdateLinkDTO request) {
        try {
            linkService.updateLink(id, request);
            return ResponseEntity.noContent().build();
        } catch (LinkNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (WrongPasswordException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("reason", "wrong password").build();
        }
    }

    @DeleteMapping("/api/links/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable String id, @RequestHeader(value = "pass", required = false) String password) {
        try {
            linkService.deleteLink(id, password);
            return ResponseEntity.noContent().build();
        } catch (WrongPasswordException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("reason", "wrong password").build();
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }
}
