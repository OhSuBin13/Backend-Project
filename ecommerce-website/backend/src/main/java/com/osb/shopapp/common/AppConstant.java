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

    // Pagination/Sorting constants
    public static final String PAGE_NUMBER = "0";
    public static final Integer PAGE_NUMBER_INT = 0;
    public static final String PAGE_SIZE = "10";
    public static final Integer PAGE_SIZE_INT = 10;
    public static final String SORT_DIR = "asc";
    public static final String SORT_CATEGORIES_BY = "id";
    public static final String SORT_PRODUCTS_BY = "id";
    public static final String SORT_ORDERS_BY = "id";
    public static final String SORT_USERS_BY = "id";
    public static final String SORT_ADDRESSES_BY = "id";

    // Thymeleaf template names
    public static final String USER_ACTIVATION_TEMPLATE = "account_activation";
}
