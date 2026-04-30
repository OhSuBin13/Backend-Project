package com.osb.shopapp;

import com.osb.shopapp.address.Address;
import com.osb.shopapp.address.AddressType;
import com.osb.shopapp.category.Category;
import com.osb.shopapp.category.CategoryRequest;
import com.osb.shopapp.category.CategoryResponse;
import com.osb.shopapp.order.Order;
import com.osb.shopapp.order.OrderItem;
import com.osb.shopapp.order.OrderItemStatus;
import com.osb.shopapp.order.OrderStatus;
import com.osb.shopapp.product.Product;
import com.osb.shopapp.product.ProductCondition;
import com.osb.shopapp.product.ProductRequest;
import com.osb.shopapp.product.ProductResponse;
import com.osb.shopapp.role.Role;
import com.osb.shopapp.user.User;
import com.osb.shopapp.user.UserRequest;
import com.osb.shopapp.user.UserResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestDataUtils {

    private static final ZonedDateTime CURRENT_TIME = ZonedDateTime.now();

    private TestDataUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static User createUserA(Set<Role> roles) {
        return User.builder()
                .id(1)
                .name("Test user A")
                .password("password")
                .email("testA@test.com")
                .homeCountry("UK")
                .registeredAt(LocalDate.now())
                .isEnabled(true)
                .isMfaEnabled(false)
                .roles(roles)
                .build();
    }

    public static UserRequest createUserRequestA() {
        return UserRequest.builder()
                .name("Test User A")
                .homeCountry("UK")
                .isMfaEnabled(false)
                .build();
    }

    public static UserResponse createUserResponseA(Set<Role> roles) {
        return UserResponse.builder()
                .id(1)
                .name("Test user A")
                .email("test@test.com")
                .homeCountry("UK")
                .registeredAt(LocalDate.now())
                .isMfaEnabled(false)
                .roles(roles)
                .build();
    }

    public static User createUserB(Set<Role> roles) {
        return User.builder()
                .id(2)
                .name("Test user B")
                .password("password")
                .email("testB@test.com")
                .homeCountry("Greece")
                .registeredAt(LocalDate.now())
                .isEnabled(true)
                .isMfaEnabled(false)
                .roles(roles)
                .build();
    }

    public static UserResponse createUserResponseB(Set<Role> roles) {
        return UserResponse.builder()
                .id(2)
                .name("Test user B")
                .email("testB@test.com")
                .homeCountry("Greece")
                .registeredAt(LocalDate.now())
                .isMfaEnabled(false)
                .roles(roles)
                .build();
    }

    public static Category createCategoryA() {
        return Category.builder()
                .id(1)
                .name("Test category A")
                .build();
    }

    public static CategoryRequest createCategoryRequestA() {
        return CategoryRequest.builder()
                .id(1)
                .name("Test category A")
                .build();
    }

    public static CategoryResponse createCategoryResponseA() {
        return CategoryResponse.builder()
                .id(1)
                .name("Test Category A")
                .build();
    }

    public static Category createCategoryB() {
        return Category.builder()
                .id(2)
                .name("Test category B")
                .build();
    }

    public static CategoryRequest createCategoryRequestB() {
        return CategoryRequest.builder()
                .id(2)
                .name("Test category B")
                .build();
    }

    public static CategoryResponse createCategoryResponseB() {
        return CategoryResponse.builder()
                .id(2)
                .name("Test Category B")
                .build();
    }

    public static Product createProductA(Category category, User seller) {
        return Product.builder()
                .id(1)
                .name("Test product A")
                .description("Description A")
                .price(BigDecimal.valueOf(10.00))
                .previousPrice(BigDecimal.valueOf(5.0))
                .condition(ProductCondition.NEW)
                .availableQuantity(2)
                .listedAt(CURRENT_TIME)
                .isDeleted(false)
                .category(category)
                .seller(seller)
                .images(new ArrayList<>())
                .build();
    }

    public static ProductRequest createProductRequestA(Integer categoryId) {
        return ProductRequest.builder()
                .id(1)
                .name("Test product A")
                .description("Description A")
                .price(BigDecimal.valueOf(10.00))
                .condition(ProductCondition.NEW)
                .availableQuantity(2)
                .categoryId(categoryId)
                .build();
    }

    public static ProductResponse createProductResponseA(CategoryResponse categoryResponse, UserResponse userResponse) {
        return ProductResponse.builder()
                .id(1)
                .name("Test product A")
                .description("Description A")
                .price(BigDecimal.valueOf(10.00))
                .previousPrice(BigDecimal.valueOf(5.0))
                .condition(ProductCondition.NEW)
                .availableQuantity(2)
                .listedAt(CURRENT_TIME)
                .isDeleted(false)
                .category(categoryResponse)
                .seller(userResponse)
                .images(new ArrayList<>())
                .build();
    }

    public static Product createProductB(Category category, User seller) {
        return Product.builder()
                .id(1)
                .name("Test product B")
                .description("Description B")
                .price(BigDecimal.valueOf(20.0))
                .previousPrice(BigDecimal.valueOf(10.0))
                .condition(ProductCondition.LINK_NEW)
                .availableQuantity(3)
                .listedAt(CURRENT_TIME)
                .isDeleted(false)
                .category(category)
                .seller(seller)
                .images(new ArrayList<>())
                .build();
    }

    public static ProductResponse createProductResponseB(CategoryResponse categoryResponse, UserResponse userResponse) {
        return ProductResponse.builder()
                .id(1)
                .name("Test product B")
                .description("Description B")
                .price(BigDecimal.valueOf(20.00))
                .previousPrice(BigDecimal.valueOf(10.0))
                .condition(ProductCondition.LINK_NEW)
                .availableQuantity(3)
                .listedAt(CURRENT_TIME)
                .isDeleted(false)
                .category(categoryResponse)
                .seller(userResponse)
                .images(new ArrayList<>())
                .build();
    }

    public static Address createAddressA(User user) {
        return Address.builder()
                .id(1)
                .name("Test address A")
                .country("UK")
                .street("Mappin Street")
                .state("South Yorkshire")
                .city("Sheffield")
                .postalCode("S1 4DT")
                .phoneNumber("+441234567890")
                .isMain(false)
                .addressType(AddressType.HOME)
                .user(user)
                .build();
    }

    public static Address createAddressB(User user) {
        return Address.builder()
                .id(2)
                .name("Test address B")
                .country("Greece")
                .street("Ermou Street")
                .state("Attica")
                .city("Athens")
                .postalCode("10563")
                .phoneNumber("+302112345678")
                .isMain(false)
                .addressType(AddressType.WORK)
                .user(user)
                .build();
    }

    public static Order createOrderA(User buyer, Address address, Product orderProduct) {
        OrderItem orderItem = OrderItem.builder()
                .id(1)
                .status(OrderItemStatus.PENDING_SHIPMENT)
                .product(orderProduct)
                .productQuantity(orderProduct.getAvailableQuantity())
                .productName(orderProduct.getName())
                .productPrice(orderProduct.getPrice())
                .productCondition(orderProduct.getCondition())
                .productSeller(orderProduct.getSeller())
                .build();

        return Order.builder()
                .id(1)
                .placedAt(CURRENT_TIME)
                .paymentMethod("cash")
                .status(OrderStatus.PAID)
                .stripeCheckoutId("stripe-id")
                .billingAddress(address.getFullAddress())
                .deliveryAddress(address.getFullAddress())
                .buyer(buyer)
                .orderItems(List.of(orderItem))
                .build();
    }

    public static Order createOrderB(User buyer, Address address, Product orderProduct) {
        OrderItem orderItem = OrderItem.builder()
                .id(2)
                .status(OrderItemStatus.PENDING_SHIPMENT)
                .product(orderProduct)
                .productQuantity(orderProduct.getAvailableQuantity())
                .productName(orderProduct.getName())
                .productPrice(orderProduct.getPrice())
                .productCondition(orderProduct.getCondition())
                .productSeller(orderProduct.getSeller())
                .build();

        return Order.builder()
                .id(2)
                .placedAt(CURRENT_TIME)
                .paymentMethod("cash")
                .status(OrderStatus.PAID)
                .stripeCheckoutId("stripe-id-2")
                .billingAddress(address.getFullAddress())
                .deliveryAddress(address.getFullAddress())
                .buyer(buyer)
                .orderItems(List.of(orderItem))
                .build();
    }
}
