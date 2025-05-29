package org.example.s29624tpo11.controllers;

import jakarta.validation.Valid;
import org.example.s29624tpo11.DTOs.CreateLinkDTO;
import org.example.s29624tpo11.DTOs.ResponseDTO;
import org.example.s29624tpo11.DTOs.SearchLinkDTO;
import org.example.s29624tpo11.DTOs.UpdateLinkDTO;
import org.example.s29624tpo11.exceptions.LinkNotFoundException;
import org.example.s29624tpo11.exceptions.WrongPasswordException;
import org.example.s29624tpo11.exceptions.DuplicateNameException;
import org.example.s29624tpo11.services.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
public class WebController {

    private final LinkService linkService;
    private final MessageSource messageSource;

    @Autowired
    public WebController(LinkService linkService, MessageSource messageSource) {
        this.linkService = linkService;
        this.messageSource = messageSource;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("createLinkDTO", new CreateLinkDTO());
        return "create";
    }

    @PostMapping("/create")
    public String createLink(@Valid @ModelAttribute CreateLinkDTO createLinkDTO,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        if (bindingResult.hasErrors()) {
            return "create";
        }

        try {
            ResponseDTO response = linkService.createLink(createLinkDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("create.success", null, locale));
            redirectAttributes.addFlashAttribute("createdLink", response);
            return "redirect:/create/success";
        } catch (DuplicateNameException e) {
            model.addAttribute("errorMessage",
                    messageSource.getMessage("error.duplicate.name", null, locale));
            return "create";
        } catch (Exception e) {
            model.addAttribute("errorMessage",
                    messageSource.getMessage("error.generic", null, locale));
            return "create";
        }
    }

    @GetMapping("/create/success")
    public String createSuccess() {
        return "create-success";
    }

    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("searchRequest", new SearchLinkDTO());
        return "search";
    }

    @PostMapping("/search")
    public String searchLink(@ModelAttribute SearchLinkDTO searchRequest,
                             Model model,
                             Locale locale) {
        try {
            ResponseDTO response = linkService.getLinkByName(searchRequest.getName(), searchRequest.getPassword());
            return "redirect:/link/" + response.getId();
        } catch (LinkNotFoundException e) {
            model.addAttribute("errorMessage",
                    messageSource.getMessage("error.link.not.found", null, locale));
            return "search";
        } catch (WrongPasswordException e) {
            model.addAttribute("errorMessage",
                    messageSource.getMessage("error.wrong.password", null, locale));
            return "search";
        }
    }

    @GetMapping("/link/{id}")
    public String linkDetails(@PathVariable String id, Model model, Locale locale) {
        try {
            ResponseDTO link = linkService.getLinkById(id);
            model.addAttribute("link", link);
            model.addAttribute("updateLinkDTO", new UpdateLinkDTO());
            return "link-details";
        } catch (LinkNotFoundException e) {
            model.addAttribute("errorMessage",
                    messageSource.getMessage("error.link.not.found", null, locale));
            return "error";
        }
    }

    @PostMapping("/link/{id}/edit")
    public String updateLink(@PathVariable String id,
                             @Valid @ModelAttribute UpdateLinkDTO updateLinkDTO,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        if (bindingResult.hasErrors()) {
            try {
                ResponseDTO link = linkService.getLinkById(id);
                model.addAttribute("link", link);
                return "link-details";
            } catch (LinkNotFoundException e) {
                return "redirect:/search";
            }
        }

        try {
            linkService.updateLink(id, updateLinkDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("edit.success", null, locale));
            return "redirect:/link/" + id;
        } catch (LinkNotFoundException e) {
            model.addAttribute("errorMessage",
                    messageSource.getMessage("error.link.not.found", null, locale));
            return "error";
        } catch (WrongPasswordException e) {
            try {
                ResponseDTO link = linkService.getLinkById(id);
                model.addAttribute("link", link);
                model.addAttribute("errorMessage",
                        messageSource.getMessage("error.wrong.password", null, locale));
                return "link-details";
            } catch (LinkNotFoundException ex) {
                return "redirect:/search";
            }
        }
    }

    @PostMapping("/link/{id}/delete")
    public String deleteLink(@PathVariable String id,
                             @RequestParam(required = false) String password,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        try {
            linkService.deleteLink(id, password);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("delete.success", null, locale));
            return "redirect:/";
        } catch (WrongPasswordException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("error.wrong.password", null, locale));
            return "redirect:/link/" + id;
        }
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }
}