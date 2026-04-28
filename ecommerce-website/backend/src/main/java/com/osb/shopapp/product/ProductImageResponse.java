package com.osb.shopapp.product;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponse {

    private Integer id;

    private String name;

    private String type;

    private byte[] image;
}
