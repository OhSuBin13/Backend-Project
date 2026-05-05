package com.osb.shopapp.wishlist;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListItemRepository extends JpaRepository<WishListItem, Integer> {

    boolean existsByUserIdAndProductId(Integer userId, Integer productId);

    @EntityGraph(attributePaths = {"product.category.parentCategory", "product.seller.roles", "product.images"})
    List<WishListItem> findAllWithProductDetailsByUserId(Integer userId);

    void deleteByUserIdAndProductId(Integer userId, Integer productId);
}
