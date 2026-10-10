package com.aziz.product.repository;

import com.aziz.product.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = "variants")
    Optional<Product> findBySlug(String slug);

    @EntityGraph(attributePaths = "variants")
    Optional<Product> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = "variants")
    List<Product> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}