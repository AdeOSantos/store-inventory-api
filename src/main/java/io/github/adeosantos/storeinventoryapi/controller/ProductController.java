package io.github.adeosantos.storeinventoryapi.controller;

import io.github.adeosantos.storeinventoryapi.model.Product;
import io.github.adeosantos.storeinventoryapi.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/product")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductController {

    private final ProductRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @Operation(
            summary = "Get all products",
            description = "Get all products and returns the saved entities."
    )
    @GetMapping
    public List<Product> all() {
        logger.info("Fetching all products");
        return repository.findAll();
    }

    @Operation(
            summary = "Get product by Id",
            description = "Get product by Id and returns the saved entity."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Product> get(
            @Parameter(description = "ID of the product to retrieve", required = true)
            @PathVariable Long id) {
        logger.info("Fetching product with id={}", id);
        return repository.findById(id)
                .map(product -> {
                    logger.info("Product found: {}", product);
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    logger.warn("Product with id={} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(
            summary = "Create just one product",
            description = "Creates only one product and returns the saved entity."
    )
    @PostMapping
    public Product create(
            @Parameter(description = "Product object to create", required = true)
            @Valid @RequestBody Product product) {
        logger.info("Creating product: {}", product);
        Product saved = repository.save(product);
        logger.info("Product created with id={}", saved.getId());
        return saved;
    }

    @Operation(
            summary = "Create multiple products",
            description = "Creates a list of new products and returns the saved entities."
    )
    @PostMapping("/list")
    public ResponseEntity<?> createProducts(
            @Parameter(description = "List of products to be created", required = true)
            @RequestBody List<Product> products) {
        logger.info("Creating list of products. Count={}", products.size());
        try {
            repository.saveAll(products);
            logger.info("Products saved successfully");
            return ResponseEntity.ok("Products saved successfully");
        } catch (ObjectOptimisticLockingFailureException ex) {
            logger.error("Optimistic locking failure: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Product update conflict. Please retry.");
        } catch (Exception e) {
            logger.error("Unexpected error while saving products: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save products.");
        }
    }

    @Operation(
            summary = "Update product by Id",
            description = "Update product by Id and returns the saved entity."
    )
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @Parameter(description = "ID of the product to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated product object", required = true)
            @Valid @RequestBody Product product) {
        logger.info("Updating product with id={}", id);
        return repository.findById(id)
                .map(existing -> {
                    logger.info("Product found. Updating fields");
                    existing.setName(product.getName());
                    existing.setDescription(product.getDescription());
                    existing.setPrice(product.getPrice());
                    existing.setQuantity(product.getQuantity());
                    Product updated = repository.save(existing);
                    logger.info("Product updated: {}", updated);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> {
                    logger.warn("Product with id={} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(
            summary = "Delete product by Id",
            description = "Delete product by Id and returns the saved entity."
    )
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID of the product to delete", required = true)
            @PathVariable Long id) {
        logger.info("Deleting product with id={}", id);
        return repository.findById(id)
                .map(p -> {
                    repository.delete(p);
                    logger.info("Product with id={} deleted", id);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> {
                    logger.warn("Product with id={} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
}