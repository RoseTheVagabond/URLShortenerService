package org.example.s29624tpo11.services;

import org.example.s29624tpo11.DTOs.CreateLinkDTO;
import org.example.s29624tpo11.DTOs.ResponseDTO;
import org.example.s29624tpo11.DTOs.UpdateLinkDTO;
import org.example.s29624tpo11.exceptions.LinkNotFoundException;
import org.example.s29624tpo11.exceptions.WrongPasswordException;
import org.example.s29624tpo11.models.Link;
import org.example.s29624tpo11.repositories.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class LinkService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ID_LENGTH = 10;
    private final Random random = new Random();

    private final LinkRepository linkRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Autowired
    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    private String generateId() {
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public ResponseDTO createLink(CreateLinkDTO request) {
        String id;
        do {
            id = generateId();
        } while (linkRepository.existsById(id));

        Link link = new Link(id, request.getName(), request.getTargetUrl(), request.getPassword());
        Link savedLink = linkRepository.save(link);

        return new ResponseDTO(savedLink, baseUrl);
    }

    public ResponseDTO getLinkById(String id) {
        Link link = linkRepository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Link not found"));

        return new ResponseDTO(link, baseUrl);
    }

    @Transactional
    public String redirectAndIncrement(String id) {
        Link link = linkRepository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Link not found"));

        link.incrementVisits();
        linkRepository.save(link);

        return link.getTargetUrl();
    }

    public void updateLink(String id, UpdateLinkDTO request) {
        Link link = linkRepository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Link not found"));

        if (link.hasPassword()) {
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new WrongPasswordException("Password required for protected link");
            }

            if (!request.getPassword().equals(link.getPassword())) {
                throw new WrongPasswordException("Wrong password");
            }
        }

        if (request.getName() != null && !request.getName().isEmpty()) {
            link.setName(request.getName());
        }

        if (request.getTargetUrl() != null && !request.getTargetUrl().isEmpty()) {
            link.setTargetUrl(request.getTargetUrl());
        }

        linkRepository.save(link);
    }

    public void deleteLink(String id, String password) {
        Link link = linkRepository.findById(id).orElse(null);

        if (link == null) {
            return;
        }

        if (link.hasPassword()) {
            if (password == null || password.isEmpty()) {
                throw new WrongPasswordException("Password required for protected link");
            }

            if (!password.equals(link.getPassword())) {
                throw new WrongPasswordException("Wrong password");
            }
        }

        linkRepository.delete(link);
    }
}
