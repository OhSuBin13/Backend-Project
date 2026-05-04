package com.osb.shopapp.cart;

import com.osb.shopapp.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemMapper {

    private final ProductMapper productMapper;

    public CartItem toCartItem(CartItemRequest cartItemRequest) {
        return CartItem.builder()
                .quantity(cartItemRequest.getQuantity())
                .build();
    }

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .product(productMapper.toProductResponse(cartItem.getProduct()))
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .addedAt(cartItem.getAddedAt())
                .build();
    }
}
