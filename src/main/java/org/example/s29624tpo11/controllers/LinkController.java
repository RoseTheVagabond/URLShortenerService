package org.example.s29624tpo11.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.s29624tpo11.DTOs.CreateLinkDTO;
import org.example.s29624tpo11.DTOs.ResponseDTO;
import org.example.s29624tpo11.DTOs.UpdateLinkDTO;
import org.example.s29624tpo11.exceptions.LinkNotFoundException;
import org.example.s29624tpo11.exceptions.WrongPasswordException;
import org.example.s29624tpo11.services.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class LinkController {

    private final LinkService linkService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    @Autowired
    public LinkController(LinkService linkService, MessageSource messageSource, LocaleResolver localeResolver) {
        this.linkService = linkService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    // ========== API ENDPOINTS WITH THYMELEAF SUPPORT ==========

    @PostMapping(value = "/api/links",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE})
    public Object createLink(@Valid @RequestBody(required = false) CreateLinkDTO request,
                             @Valid @ModelAttribute CreateLinkDTO formRequest,
                             BindingResult bindingResult,
                             HttpServletRequest httpRequest,
                             @RequestParam(value = "lang", required = false) String lang) {

        // Handle language change
        if (lang != null) {
            Locale locale = Locale.forLanguageTag(lang);
            localeResolver.setLocale(httpRequest, null, locale);
        }

        // Determine if this is a form submission or API call
        boolean isFormSubmission = httpRequest.getContentType() != null &&
                httpRequest.getContentType().contains("application/x-www-form-urlencoded");

        CreateLinkDTO actualRequest = isFormSubmission ? formRequest : request;

        if (isFormSubmission && "text/html".equals(httpRequest.getHeader("Accept"))) {
            // Return Thymeleaf view for browser requests
            ModelAndView mav = new ModelAndView();

            if (bindingResult.hasErrors()) {
                mav.setViewName("create-link");
                mav.addObject("createLinkDTO", actualRequest);
                mav.addObject("errors", bindingResult.getAllErrors());
                return mav;
            }

            try {
                // Check URL uniqueness
                if (linkService.isUrlAlreadyExists(actualRequest.getTargetUrl())) {
                    Locale locale = localeResolver.resolveLocale(httpRequest);
                    String errorMessage = messageSource.getMessage("url.unique", null, locale);
                    mav.setViewName("create-link");
                    mav.addObject("createLinkDTO", actualRequest);
                    mav.addObject("errorMessage", errorMessage);
                    return mav;
                }

                ResponseDTO response = linkService.createLink(actualRequest);
                mav.setViewName("link-created");
                mav.addObject("link", response);
                Locale locale = localeResolver.resolveLocale(httpRequest);
                String successMessage = messageSource.getMessage("link.created.success", null, locale);
                mav.addObject("successMessage", successMessage);
                return mav;
            } catch (Exception e) {
                Locale locale = localeResolver.resolveLocale(httpRequest);
                String errorMessage = messageSource.getMessage("link.created.error", null, locale);
                mav.setViewName("create-link");
                mav.addObject("createLinkDTO", actualRequest);
                mav.addObject("errorMessage", errorMessage);
                return mav;
            }
        } else {
            // Return JSON for API calls
            ResponseDTO response = linkService.createLink(actualRequest);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
            return ResponseEntity.created(location).body(response);
        }
    }

    @GetMapping(value = "/api/links/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE})
    public Object getLink(@PathVariable String id,
                          @RequestParam(value = "password", required = false) String password,
                          @RequestParam(value = "action", required = false) String action,
                          @RequestParam(value = "lang", required = false) String lang,
                          HttpServletRequest request) {

        // Handle language change
        if (lang != null) {
            Locale locale = Locale.forLanguageTag(lang);
            localeResolver.setLocale(request, null, locale);
        }

        String acceptHeader = request.getHeader("Accept");
        boolean wantsHtml = acceptHeader != null && acceptHeader.contains("text/html");

        try {
            ResponseDTO response = linkService.getLinkById(id);

            if (wantsHtml) {
                // Check if link has password and verify it
                if (linkService.linkHasPassword(id)) {
                    if (password == null || password.isEmpty()) {
                        ModelAndView mav = new ModelAndView("link-password-form");
                        mav.addObject("linkId", id);
                        Locale locale = localeResolver.resolveLocale(request);
                        String errorMessage = messageSource.getMessage("password.required", null, locale);
                        mav.addObject("errorMessage", errorMessage);
                        return mav;
                    }

                    if (!linkService.verifyPassword(id, password)) {
                        ModelAndView mav = new ModelAndView("link-password-form");
                        mav.addObject("linkId", id);
                        Locale locale = localeResolver.resolveLocale(request);
                        String errorMessage = messageSource.getMessage("password.wrong", null, locale);
                        mav.addObject("errorMessage", errorMessage);
                        return mav;
                    }
                }

                ModelAndView mav = new ModelAndView("link-details");
                mav.addObject("link", response);
                mav.addObject("updateLinkDTO", new UpdateLinkDTO());
                return mav;
            } else {
                return ResponseEntity.ok(response);
            }
        } catch (LinkNotFoundException e) {
            if (wantsHtml) {
                ModelAndView mav = new ModelAndView("error");
                Locale locale = localeResolver.resolveLocale(request);
                String errorMessage = messageSource.getMessage("link.notfound", null, locale);
                mav.addObject("errorMessage", errorMessage);
                return mav;
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @PatchMapping(value = "/api/links/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE})
    public Object updateLink(@PathVariable String id,
                             @Valid @RequestBody(required = false) UpdateLinkDTO request,
                             @Valid @ModelAttribute UpdateLinkDTO formRequest,
                             BindingResult bindingResult,
                             @RequestParam(value = "lang", required = false) String lang,
                             HttpServletRequest httpRequest) {

        // Handle language change
        if (lang != null) {
            Locale locale = Locale.forLanguageTag(lang);
            localeResolver.setLocale(httpRequest, null, locale);
        }

        boolean isFormSubmission = httpRequest.getContentType() != null &&
                httpRequest.getContentType().contains("application/x-www-form-urlencoded");

        UpdateLinkDTO actualRequest = isFormSubmission ? formRequest : request;
        String acceptHeader = httpRequest.getHeader("Accept");
        boolean wantsHtml = acceptHeader != null && acceptHeader.contains("text/html");

        try {
            if (isFormSubmission && bindingResult.hasErrors()) {
                ModelAndView mav = new ModelAndView("link-details");
                ResponseDTO link = linkService.getLinkById(id);
                mav.addObject("link", link);
                mav.addObject("updateLinkDTO", actualRequest);
                return mav;
            }

            linkService.updateLink(id, actualRequest);

            if (wantsHtml) {
                ModelAndView mav = new ModelAndView("redirect:/api/links/" + id);
                return mav;
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (LinkNotFoundException e) {
            if (wantsHtml) {
                ModelAndView mav = new ModelAndView("error");
                Locale locale = localeResolver.resolveLocale(httpRequest);
                String errorMessage = messageSource.getMessage("link.notfound", null, locale);
                mav.addObject("errorMessage", errorMessage);
                return mav;
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (WrongPasswordException e) {
            if (wantsHtml) {
                ModelAndView mav = new ModelAndView("error");
                Locale locale = localeResolver.resolveLocale(httpRequest);
                String errorMessage = messageSource.getMessage("password.wrong", null, locale);
                mav.addObject("errorMessage", errorMessage);
                return mav;
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).header("reason", "wrong password").build();
            }
        }
    }

    @DeleteMapping(value = "/api/links/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE})
    public Object deleteLink(@PathVariable String id,
                             @RequestHeader(value = "pass", required = false) String headerPassword,
                             @RequestParam(value = "password", required = false) String paramPassword,
                             @RequestParam(value = "lang", required = false) String lang,
                             HttpServletRequest request) {

        // Handle language change
        if (lang != null) {
            Locale locale = Locale.forLanguageTag(lang);
            localeResolver.setLocale(request, null, locale);
        }

        String password = headerPassword != null ? headerPassword : paramPassword;
        String acceptHeader = request.getHeader("Accept");
        boolean wantsHtml = acceptHeader != null && acceptHeader.contains("text/html");

        try {
            linkService.deleteLink(id, password);

            if (wantsHtml) {
                ModelAndView mav = new ModelAndView("link-deleted");
                Locale locale = localeResolver.resolveLocale(request);
                String successMessage = messageSource.getMessage("link.deleted.success", null, locale);
                mav.addObject("successMessage", successMessage);
                return mav;
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (WrongPasswordException e) {
            if (wantsHtml) {
                ModelAndView mav = new ModelAndView("error");
                Locale locale = localeResolver.resolveLocale(request);
                String errorMessage = messageSource.getMessage("password.wrong", null, locale);
                mav.addObject("errorMessage", errorMessage);
                return mav;
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).header("reason", "wrong password").build();
            }
        }
    }

    // ========== REDIRECT ENDPOINT ==========

    @GetMapping("/red/{id}")
    public ResponseEntity<Void> redirect(@PathVariable String id) {
        try {
            String targetUrl = linkService.redirectAndIncrement(id);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(targetUrl)).build();
        } catch (LinkNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ROOT ENDPOINT FOR FORM ==========

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView index(@RequestParam(value = "lang", required = false) String lang,
                              HttpServletRequest request) {
        // Handle language change
        if (lang != null) {
            Locale locale = Locale.forLanguageTag(lang);
            localeResolver.setLocale(request, null, locale);
        }

        ModelAndView mav = new ModelAndView("create-link");
        mav.addObject("createLinkDTO", new CreateLinkDTO());
        return mav;
    }

    // ========== EXCEPTION HANDLERS ==========

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }
}