package com.osb.shopapp.category;

import com.osb.shopapp.exception.APIException;
import com.osb.shopapp.exception.ForeignKeyConstraintException;
import com.osb.shopapp.exception.ResourceAlreadyExistsException;
import com.osb.shopapp.exception.ResourceNotFoundException;
import com.osb.shopapp.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse save(CategoryRequest categoryRequest) {
        categoryRequest.setId(null);

        if (categoryRepository.findByName(categoryRequest.getName()).isPresent())
            throw new ResourceAlreadyExistsException("A category with the name: '" + categoryRequest.getName() + "' already exists");

        Category parentCategory = categoryRepository.findParentById(categoryRequest.getParentId()).orElseThrow(
                () -> new ResourceNotFoundException("No parent category found with ID: " + categoryRequest.getParentId()));

        Category category = categoryMapper.toCategory(categoryRequest);
        category.setParentCategory(parentCategory);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(savedCategory);
    }

    public CategoryResponse findById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .map(categoryMapper::toCategoryResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No category found with ID: " + categoryId));
    }

    public List<CategoryResponse> findAll(String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        List<Category> categories = categoryRepository.findAll(sort);

        return categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    public List<CategoryResponse> findAllByKeyword(String sortBy, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        List<Category> categories = categoryRepository.findAllByKeyword(sort, keyword);

        return categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    public List<CategoryResponse> findAllParents() {
        List<Category> categories = categoryRepository.findAllParents();

        return categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse update(CategoryRequest categoryRequest, Integer categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("No category found with ID: " + categoryId));
        Category parentCategory = categoryRepository.findParentById(categoryRequest.getParentId()).orElseThrow(
                () -> new ResourceNotFoundException("No parent category found with ID: " + categoryRequest.getParentId()));

        if (existingCategory.getParentCategory() == null)
            throw new APIException("Modification of parent categories is not allowed");

        if (!existingCategory.getName().equals(categoryRequest.getName()) &&
                categoryRepository.findByName(categoryRequest.getName()).isPresent())
            throw new ResourceAlreadyExistsException("A category with the name: '" + categoryRequest.getName() + "' already exists");

        existingCategory.setName(categoryRequest.getName());
        existingCategory.setParentCategory(parentCategory);

        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toCategoryResponse(updatedCategory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Integer categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("No category found with ID: " + categoryId));

        if (existingCategory.getParentCategory() == null)
            throw new APIException("Deletion of parent categories is not allowed");

        if (productRepository.existsByCategoryId(categoryId))
            throw new ForeignKeyConstraintException("Cannot delete category because existing products reference it");

        categoryRepository.deleteById(categoryId);
    }
}
