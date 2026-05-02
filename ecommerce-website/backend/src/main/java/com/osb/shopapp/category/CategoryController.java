package com.osb.shopapp.category;

import com.osb.shopapp.common.AppConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> save(
            @Valid @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryResponse savedCategory = categoryService.save(categoryRequest);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{category-id}")
    public ResponseEntity<CategoryResponse> findById(
            @PathVariable("category-id") Integer categoryId
    ) {
        CategoryResponse foundResponse = categoryService.findById(categoryId);
        return new ResponseEntity<>(foundResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll(
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = AppConstant.SORT_DIR, required = false) String sortDirection,
            @RequestParam(name = "search", required = false) String search
    ) {
        List<CategoryResponse> foundCategories = (search == null || search.isBlank()) ?
                categoryService.findAll(sortBy, sortDirection)
                : categoryService.findAllByKeyword(sortBy, sortDirection, search);
        return new ResponseEntity<>(foundCategories, HttpStatus.OK);
    }

    @GetMapping("/parents")
    public ResponseEntity<List<CategoryResponse>> findAllParents() {
        List<CategoryResponse> foundParents = categoryService.findAllParents();
        return new ResponseEntity<>(foundParents, HttpStatus.OK);
    }

    @PutMapping("/{category-id}")
    public ResponseEntity<CategoryResponse> update(
            @Valid @RequestBody CategoryRequest categoryRequest,
            @PathVariable("category-id") Integer categoryId
    ) {
        CategoryResponse updatedCategory = categoryService.update(categoryRequest, categoryId);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{category-id}")
    public ResponseEntity<?> delete(
            @PathVariable("category-id") Integer categoryId
    ) {
        categoryService.delete(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
