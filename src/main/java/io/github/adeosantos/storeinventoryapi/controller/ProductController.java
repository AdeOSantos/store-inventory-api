package io.github.adeosantos.storeinventoryapi.controller;

import io.github.adeosantos.storeinventoryapi.model.Product;
import io.github.adeosantos.storeinventoryapi.repository.ProductRepository;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("api/products")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductController {

    private final ProductRepository repository;

    public ProductController (ProductRepository repository ){
        this.repository = repository;
    }

@GetMapping
public List<Product> all(){
        return repository.findAll();
}


@GetMapping("/{id}")
public ResponseEntity<Product> get ( @PathVariable Long id){

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

}

@PostMapping
public Product create (@Valid @RequestBody Product product){

            return repository.save(product);
}


@PutMapping("/{id}")
public ResponseEntity<Product> update (@PathVariable Long id, @Valid @RequestBody Product product){

        return repository.findById(id)
                .map(existing -> {
                    existing.setName(product.getName());
                    existing.setDescription(product.getDescription());
                    existing.setPrice(product.getPrice());
                    existing.setQuantity(product.getQuantity());
                    return ResponseEntity.ok(repository.save(existing));

                })
                .orElse(ResponseEntity.notFound().build());
}

@DeleteMapping("{id}")
public ResponseEntity<?> delete (@PathVariable Long id){
    return repository.findById(id)
            .map(p -> {
                repository.delete(p);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
}






}