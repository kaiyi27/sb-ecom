package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank(message = "Product name must not be blank")
    @Size(min = 5, message = "Product name must contain at least 5 characters")
    private String productName;

    @NotBlank(message = "Product description must not be blank")
    @Size(min = 10, message = "Product description must contain at least 10 characters")
    private String description;

    private String image;
    private Integer quantity;
    private Double price;
    private Double discount;
    private Double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
