package com.osb.shopapp.common;

public final class AppConstant {

    private AppConstant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Product Constants
    public static final int MAX_PRODUCT_NAME_LENGTH = 100;
    public static final int MAX_PRODUCT_DESCRIPTION_LENGTH = 5000;
    public static final int MAX_PRODUCT_QUANTITY = 1000;
    public static final int MAX_PRODUCT_PRICE = 10000;

    // Thymeleaf template names
    public static final String USER_ACTIVATION_TEMPLATE = "account_activation";
}
