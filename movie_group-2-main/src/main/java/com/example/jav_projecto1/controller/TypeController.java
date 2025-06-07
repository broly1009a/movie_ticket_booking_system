package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.entities.Type;
import com.example.jav_projecto1.respiratory.TypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/types")
public class TypeController {

    private final TypeRepository typeRepository;

    public TypeController(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    // Get all types
    @GetMapping
    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }

    // Get type by id
    @GetMapping("/{id}")
    public ResponseEntity<Type> getTypeById(@PathVariable Integer id) {
        Optional<Type> typeOpt = typeRepository.findById(id);
        return typeOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new type
    @PostMapping
    public Type createType(@RequestBody Type type) {
        return typeRepository.save(type);
    }

    // Update type
    @PutMapping("/{id}")
    public ResponseEntity<Type> updateType(@PathVariable Integer id, @RequestBody Type typeDetails) {
        Optional<Type> typeOpt = typeRepository.findById(id);
        if (typeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Type type = typeOpt.get();
        type.setTypeName(typeDetails.getTypeName());
        return ResponseEntity.ok(typeRepository.save(type));
    }

    // Delete type
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable Integer id) {
        if (!typeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        typeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}