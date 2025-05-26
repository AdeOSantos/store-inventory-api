package io.github.adeosantos.storeinventoryapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Product name must not be blank")
    @Size(max = 100, message = "Product name must be at most 100 characters long")
    private String name;

    @Column(length = 255)
    @Size(max = 255, message = "Description must be at most 255 characters long")
    private String description;

    @Column(nullable = false)
    @Positive(message = "Price must be greater than zero")
    private double price;

    @Column(nullable = false)
    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;

    @Version
    private Integer version;
}