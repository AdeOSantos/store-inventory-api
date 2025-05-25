package io.github.adeosantos.storeinventoryapi.repository;


import io.github.adeosantos.storeinventoryapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}