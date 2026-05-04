package com.osb.shopapp.order;

import com.osb.shopapp.cart.CartItemRepository;
import com.osb.shopapp.email.EmailService;
import com.osb.shopapp.product.Product;
import com.osb.shopapp.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AsyncFulfilmentService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final EmailService emailService;

    @Async
    public void finalizeOrder(Order order, List<Product> orderProducts) {
        try {
            // Update order and products
            orderRepository.save(order);
            productRepository.saveAll(orderProducts);

            // Delete purchased products from cart
            List<Integer> productIds = orderProducts.stream()
                    .map(Product::getId)
                    .toList();
            cartItemRepository.deleteAllByUserIdAndProductIdIn(order.getBuyer().getId(), productIds);

            // Send confirmation email to buyer
            emailService.sendPurchaseConfirmationEmail(
                    order.getBuyer().getEmail(),
                    order
            );

            // Send confirmation email to each seller, referencing their respective products that were sold
            Map<String, List<OrderItem>> sellerOrderItems = new HashMap<>();
            for (OrderItem orderItem : order.getOrderItems()) {
                String sellerEmail = orderItem.getProductSeller().getEmail();
                sellerOrderItems.computeIfAbsent(sellerEmail, k -> new ArrayList<>()).add(orderItem);
            }
            for (Map.Entry<String, List<OrderItem>> entry : sellerOrderItems.entrySet()) {
                String sellerEmail = entry.getKey();
                List<OrderItem> orderItems = entry.getValue();
                BigDecimal saleTotal = orderItems.stream()
                        .map((orderItem -> orderItem.getProductPrice()
                                .multiply(BigDecimal.valueOf(orderItem.getProductQuantity()))))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP);

                emailService.sendSaleConfirmationEmail(
                        sellerEmail,
                        orderItems.get(0).getProductSeller().getRealName(),
                        order,
                        orderItems,
                        saleTotal
                );
            }
        } catch (Exception e) {
            System.out.println("Unexpected error occurred while fulfiling order: " + e);
        }
    }
}
