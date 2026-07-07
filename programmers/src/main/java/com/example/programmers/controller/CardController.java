package com.example.programmers.controller;

import com.example.programmers.dto.response.CardResponse;
import com.example.programmers.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService service;

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> findByCustomer(
            @RequestParam UUID customerIdentification
    ) {
        return ResponseEntity.ok(
                service.findByCustomer(customerIdentification)
        );
    }
}
