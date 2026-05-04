package com.osb.shopapp.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderStatsResponseImpl implements OrderStatsResponse {
    private Long monthlyOrderCount;
    private Integer monthlyProductSales;
    private BigDecimal monthlyRevenue;
}
