package com.osb.shopapp.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    private Integer id;

    @NotBlank(message = "Name must not be empty")
    private String name;

    @NotNull(message = "Parent category ID must not be empty")
    private Integer parentId;
}
