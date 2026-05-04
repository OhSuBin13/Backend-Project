package com.osb.shopapp.order;

import java.math.BigDecimal;

public interface OrderStatsResponse {
    Long getMonthlyOrderCount();

    Integer getMonthlyProductSales();

    BigDecimal getMonthlyRevenue();
}
