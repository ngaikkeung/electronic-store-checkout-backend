package io.github.kkngai.estorecheckout.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "basket_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "basket_item_id")
    private Long basketItemId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "basket_id", referencedColumnName = "basket_id")
    private Basket basket;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
}
