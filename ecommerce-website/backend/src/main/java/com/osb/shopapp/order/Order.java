package com.osb.shopapp.order;

import com.osb.shopapp.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "CustomerOrder")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private ZonedDateTime placedAt;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // ID of the associated Stripe Checkout session
    private String stripeCheckoutId;

    private String billingAddress;

    private String deliveryAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User buyer;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "orderId", nullable = false)
    private List<OrderItem> orderItems;

}
