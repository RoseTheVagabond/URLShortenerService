package org.example.s29624tpo11.controllers;

import jakarta.validation.Valid;
import org.example.s29624tpo11.DTOs.CreateLinkDTO;
import org.example.s29624tpo11.DTOs.ResponseDTO;
import org.example.s29624tpo11.DTOs.UpdateLinkDTO;
import org.example.s29624tpo11.exceptions.LinkNotFoundException;
import org.example.s29624tpo11.exceptions.WrongPasswordException;
import org.example.s29624tpo11.exceptions.DuplicateNameException;
import org.example.s29624tpo11.services.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
public class LinkController {

    private final LinkService linkService;
    private final MessageSource messageSource;

    @Autowired
    public LinkController(LinkService linkService, MessageSource messageSource) {
        this.linkService = linkService;
        this.messageSource = messageSource;
    }

    private Locale getLocaleFromHeader(String langHeader) {
        if (langHeader == null) return Locale.ENGLISH;

        return switch (langHeader.toLowerCase()) {
            case "pl" -> new Locale("pl");
            case "de" -> new Locale("de");
            default -> Locale.ENGLISH;
        };
    }

    @PostMapping("/api/links")
    public ResponseEntity<Object> createLink(
            @Valid @RequestBody CreateLinkDTO request,
            @RequestHeader(value = "lang", required = false) String langHeader) {

        try {
            ResponseDTO response = linkService.createLink(request);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
            return ResponseEntity.created(location).body(response);
        } catch (DuplicateNameException e) {
            Locale locale = getLocaleFromHeader(langHeader);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", messageSource.getMessage("error.duplicate.name", null, locale));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @GetMapping("/api/links/{id}")
    public ResponseEntity<Object> getLink(
            @PathVariable String id,
            @RequestHeader(value = "lang", required = false) String langHeader) {
        try {
            ResponseDTO response = linkService.getLinkById(id);
            return ResponseEntity.ok(response);
        } catch (LinkNotFoundException e) {
            Locale locale = getLocaleFromHeader(langHeader);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", messageSource.getMessage("error.link.not.found", null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PatchMapping("/api/links/{id}")
    public ResponseEntity<Object> updateLink(
            @PathVariable String id,
            @Valid @RequestBody UpdateLinkDTO request,
            @RequestHeader(value = "lang", required = false) String langHeader) {
        try {
            linkService.updateLink(id, request);
            return ResponseEntity.noContent().build();
        } catch (LinkNotFoundException e) {
            Locale locale = getLocaleFromHeader(langHeader);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", messageSource.getMessage("error.link.not.found", null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (WrongPasswordException e) {
            Locale locale = getLocaleFromHeader(langHeader);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", messageSource.getMessage("error.wrong.password", null, locale));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }

    @DeleteMapping("/api/links/{id}")
    public ResponseEntity<Object> deleteLink(
            @PathVariable String id,
            @RequestHeader(value = "pass", required = false) String password,
            @RequestHeader(value = "lang", required = false) String langHeader) {
        try {
            linkService.deleteLink(id, password);
            return ResponseEntity.noContent().build();
        } catch (WrongPasswordException e) {
            Locale locale = getLocaleFromHeader(langHeader);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", messageSource.getMessage("error.wrong.password", null, locale));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            @RequestHeader(value = "lang", required = false) String langHeader) {

        Locale locale = getLocaleFromHeader(langHeader);
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String localizedMessage = messageSource.getMessage(error.getDefaultMessage(), null, error.getDefaultMessage(), locale);
            errors.put(error.getField(), localizedMessage);
        }

        response.put("message", messageSource.getMessage("error.validation", null, locale));
        response.put("errors", errors);
        return response;
    }
}