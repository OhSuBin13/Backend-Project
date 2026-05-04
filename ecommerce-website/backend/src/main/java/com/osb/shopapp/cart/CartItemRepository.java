package com.osb.shopapp.cart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    boolean existsByUserIdAndProductId(Integer userId, Integer productId);

    @EntityGraph(attributePaths = {"product.category.parentCategory", "product.seller.roles", "product.images", "user.roles"})
    Optional<CartItem> findWithProductDetailsByUserIdAndProductId(Integer userId, Integer productId);

    @EntityGraph(attributePaths = {"product.category.parentCategory", "product.seller.roles", "product.images", "user.roles"})
    List<CartItem> findAllWithProductDetailsByUserId(Integer userId);

    void deleteByUserIdAndProductId(Integer userId, Integer productId);

    void deleteByUserIdAndProductIdIn(Integer userId, List<Integer> productIds);
}
