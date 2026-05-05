package com.osb.shopapp.wishlist;

import com.osb.shopapp.product.ProductResponse;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishListItemResponse {

    private Integer id;

    private ProductResponse product;

    private ZonedDateTime addedAt;
}
