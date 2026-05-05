package com.osb.shopapp.category;

import com.osb.shopapp.TestDataUtils;
import com.osb.shopapp.common.AppConstants;
import com.osb.shopapp.exception.APIException;
import com.osb.shopapp.exception.ForeignKeyConstraintException;
import com.osb.shopapp.exception.ResourceAlreadyExistsException;
import com.osb.shopapp.exception.ResourceNotFoundException;
import com.osb.shopapp.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category categoryA;
    private Category categoryB;
    private CategoryRequest categoryRequestA;
    private CategoryRequest categoryRequestB;
    private CategoryResponse categoryResponseA;
    private CategoryResponse categoryResponseB;

    @BeforeEach
    void setUp() {
        // Initialize test objects
        categoryA = TestDataUtils.createCategoryA();
        categoryB = TestDataUtils.createCategoryB();
        categoryB.setParentCategory(categoryA);

        categoryRequestA = TestDataUtils.createCategoryRequestA();
        categoryRequestB = TestDataUtils.createCategoryRequestB();
        categoryRequestB.setParentId(categoryRequestA.getId());

        categoryResponseA = TestDataUtils.createCategoryResponseA();
        categoryResponseB = TestDataUtils.createCategoryResponseB();
        categoryResponseB.setParentId(categoryResponseA.getId());
    }

    @Test
    public void shouldSaveCategoryWhenValidRequest() {
        when(categoryRepository.findByName(categoryRequestB.getName())).thenReturn(Optional.empty());
        when(categoryRepository.findParentById(categoryRequestB.getParentId())).thenReturn(Optional.of(categoryA));
        when(categoryMapper.toCategory(categoryRequestB)).thenReturn(categoryB);
        when(categoryRepository.save(categoryB)).thenReturn(categoryB);
        when(categoryMapper.toCategoryResponse(categoryB)).thenReturn(categoryResponseB);

        CategoryResponse categoryResponse = categoryService.save(categoryRequestB);

        assertThat(categoryResponse).isEqualTo(categoryResponseB);
    }

    @Test
    public void shouldNotSaveCategoryWhenDuplicateCategoryName() {
        when(categoryRepository.findByName(categoryRequestB.getName())).thenReturn(Optional.of(categoryB));

        assertThatThrownBy(() -> categoryService.save(categoryRequestB))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("A category with the name: '" + categoryRequestB.getName() + "' already exists");
    }

    @Test
    public void shouldNotSaveCategoryWhenInvalidParentId() {
        categoryRequestB.setParentId(99);
        when(categoryRepository.findByName(categoryRequestB.getName())).thenReturn(Optional.empty());
        when(categoryRepository.findParentById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.save(categoryRequestB))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No parent category found with ID: 99");
    }

    @Test
    public void shouldFindCategoryByIdWhenValidRequest() {
        when(categoryRepository.findById(categoryA.getId())).thenReturn(Optional.of(categoryA));
        when(categoryMapper.toCategoryResponse(categoryA)).thenReturn(categoryResponseA);

        CategoryResponse categoryResponse = categoryService.findById(categoryA.getId());

        assertThat(categoryResponse).isEqualTo(categoryResponseA);
    }

    @Test
    public void shouldNotFindCategoryByIdWhenInvalidCategoryId() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No category found with ID: 99");
    }

    @Test
    public void shouldFindAllCategories() {
        Sort sort = AppConstants.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstants.SORT_CATEGORIES_BY).ascending() : Sort.by(AppConstants.SORT_CATEGORIES_BY).descending();

        when(categoryRepository.findAll(sort)).thenReturn(List.of(categoryA, categoryB));
        when(categoryMapper.toCategoryResponse(categoryA)).thenReturn(categoryResponseA);
        when(categoryMapper.toCategoryResponse(categoryB)).thenReturn(categoryResponseB);

        List<CategoryResponse> categoryResponses =
                categoryService.findAll(AppConstants.SORT_CATEGORIES_BY, AppConstants.SORT_DIR);

        assertThat(categoryResponses.size()).isEqualTo(2);
        assertThat(categoryResponses.get(0)).isEqualTo(categoryResponseA);
        assertThat(categoryResponses.get(1)).isEqualTo(categoryResponseB);
    }

    @Test
    public void shouldFindAllCategoriesByKeyword() {
        Sort sort = AppConstants.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstants.SORT_CATEGORIES_BY).ascending() : Sort.by(AppConstants.SORT_CATEGORIES_BY).descending();

        when(categoryRepository.findAllByKeyword(sort, "test category")).thenReturn(List.of(categoryA, categoryB));
        when(categoryMapper.toCategoryResponse(categoryA)).thenReturn(categoryResponseA);
        when(categoryMapper.toCategoryResponse(categoryB)).thenReturn(categoryResponseB);

        List<CategoryResponse> categoryResponses =
                categoryService.findAllByKeyword(AppConstants.SORT_CATEGORIES_BY, AppConstants.SORT_DIR, "test category");

        assertThat(categoryResponses.size()).isEqualTo(2);
        assertThat(categoryResponses.get(0)).isEqualTo(categoryResponseA);
        assertThat(categoryResponses.get(1)).isEqualTo(categoryResponseB);
    }

    @Test
    public void shouldFindAllParentCategories() {
        when(categoryRepository.findAllParents()).thenReturn(List.of(categoryA));
        when(categoryMapper.toCategoryResponse(categoryA)).thenReturn(categoryResponseA);

        List<CategoryResponse> categoryResponses = categoryService.findAllParents();

        assertThat(categoryResponses.size()).isEqualTo(1);
        assertThat(categoryResponses.get(0)).isEqualTo(categoryResponseA);
    }

    @Test
    public void shouldUpdateCategoryWhenValidRequest() {
        categoryRequestB.setName("Updated test category A");
        categoryResponseB.setName("Updated test category A");

        when(categoryRepository.findById(categoryRequestB.getId())).thenReturn(Optional.of(categoryB));
        when(categoryRepository.findParentById(categoryResponseB.getParentId())).thenReturn(Optional.of(categoryA));
        when(categoryRepository.save(categoryB)).thenReturn(categoryB);
        when(categoryMapper.toCategoryResponse(categoryB)).thenReturn(categoryResponseB);

        CategoryResponse categoryResponse = categoryService.update(categoryRequestB, categoryB.getId());

        assertThat(categoryResponse).isEqualTo(categoryResponseB);
        assertThat(categoryResponse.getName()).isEqualTo("Updated test category A");
    }

    @Test
    public void shouldNotUpdateCategoryWhenInvalidCategoryId() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(categoryRequestA, 99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No category found with ID: 99");
    }

    @Test
    public void shouldNotUpdateCategoryWhenInvalidParentId() {
        categoryRequestB.setParentId(99);
        when(categoryRepository.findById(categoryB.getId())).thenReturn(Optional.of(categoryB));
        when(categoryRepository.findParentById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(categoryRequestB, categoryB.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No parent category found with ID: 99");
    }

    @Test
    public void shouldNotUpdateCategoryWhenParent() {
        categoryRequestA.setParentId(categoryB.getId());

        when(categoryRepository.findById(categoryA.getId())).thenReturn(Optional.of(categoryA));
        when(categoryRepository.findParentById(categoryRequestA.getParentId())).thenReturn(Optional.of(categoryB));

        assertThatThrownBy(() -> categoryService.update(categoryRequestA, categoryA.getId()))
                .isInstanceOf(APIException.class)
                .hasMessage("Modification of parent categories is not allowed");
    }

    @Test
    public void shouldNotUpdateCategoryWhenDuplicateCategoryName() {
        categoryRequestB.setName(categoryA.getName());

        when(categoryRepository.findById(categoryB.getId())).thenReturn(Optional.of(categoryB));
        when(categoryRepository.findParentById(categoryRequestB.getParentId())).thenReturn(Optional.of(categoryA));
        when(categoryRepository.findByName(categoryRequestB.getName())).thenReturn(Optional.of(categoryA));

        assertThatThrownBy(() -> categoryService.update(categoryRequestB, categoryB.getId()))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("A category with the name: '" + categoryRequestB.getName() + "' already exists");
    }

    @Test
    public void shouldDeleteCategoryWhenValidRequest() {
        when(categoryRepository.findById(categoryB.getId())).thenReturn(Optional.of(categoryB));
        when(productRepository.existsByCategoryId(categoryB.getId())).thenReturn(false);

        categoryService.delete(categoryB.getId());

        verify(categoryRepository, times(1)).deleteById(categoryB.getId());
    }

    @Test
    public void shouldNotDeleteCategoryWhenInvalidCategoryId() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No category found with ID: 99");
    }

    @Test
    public void shouldNotDeleteCategoryWhenParentCategory() {
        when(categoryRepository.findById(categoryA.getId())).thenReturn(Optional.of(categoryA));

        assertThatThrownBy(() -> categoryService.delete(categoryA.getId()))
                .isInstanceOf(APIException.class)
                .hasMessage("Deletion of parent categories is not allowed");
    }

    @Test
    public void shouldNotDeleteCategoryWhenForeignKeyConstraint() {
        when(categoryRepository.findById(categoryB.getId())).thenReturn(Optional.of(categoryB));
        when(productRepository.existsByCategoryId(categoryB.getId())).thenReturn(true);

        assertThatThrownBy(() -> categoryService.delete(categoryB.getId()))
                .isInstanceOf(ForeignKeyConstraintException.class)
                .hasMessage("Cannot delete category because existing products reference it");
    }
}
