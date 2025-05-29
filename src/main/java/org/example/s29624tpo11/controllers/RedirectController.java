//package org.example.s29624tpo11.controllers;
//
//import org.example.s29624tpo11.exceptions.LinkNotFoundException;
//import org.example.s29624tpo11.services.LinkService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.net.URI;
//
//@RestController
//public class RedirectController {
//    private final LinkService linkService;
//
//    @Autowired
//    public RedirectController(LinkService linkService) {
//        this.linkService = linkService;
//    }
//
//    @GetMapping("/red/{id}")
//    public ResponseEntity<Void> redirect(@PathVariable String id) {
//        try {
//            String targetUrl = linkService.redirectAndIncrement(id);
//            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(targetUrl)).build();
//        } catch (LinkNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//}
//
