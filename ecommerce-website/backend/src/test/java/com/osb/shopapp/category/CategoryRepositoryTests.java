package com.osb.shopapp.category;

import com.osb.shopapp.TestDataUtils;
import com.osb.shopapp.common.AppConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Sort;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class CategoryRepositoryTests {

    @Container
    @ServiceConnection
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:9.2.0");

    private final CategoryRepository categoryRepository;

    private Category parentCategory;
    private Category childCategory;

    @Autowired
    public CategoryRepositoryTests(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @BeforeEach
    public void setUp() {
        // Save required entities (need to set IDs to null before inserting to avoid
        // errors related to MySQL's AUTO_INCREMENT counter not resetting between tests)
        parentCategory = TestDataUtils.createCategoryA();
        parentCategory.setId(null);
        parentCategory = categoryRepository.save(parentCategory);

        childCategory = TestDataUtils.createCategoryA();
        childCategory.setId(null);
        childCategory.setParentCategory(parentCategory);
        childCategory = categoryRepository.save(childCategory);
    }

    @Test
    public void shouldFindParentCategoryById() {
        Optional<Category> foundCategory = categoryRepository.findParentById(parentCategory.getId());

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getId()).isEqualTo(parentCategory.getId());
        assertThat(foundCategory.get().getName()).isEqualTo(parentCategory.getName());
    }

    @Test
    public void shouldFindAllCategoriesByKeyword() {
        Sort sort = AppConstant.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstant.SORT_CATEGORIES_BY).ascending() : Sort.by(AppConstant.SORT_CATEGORIES_BY).descending();

        List<Category> categories = categoryRepository.findAllByKeyword(sort, "test category");

        assertThat(categories.size()).isEqualTo(2);
        assertThat(categories.get(0).getId()).isEqualTo(parentCategory.getId());
        assertThat(categories.get(1).getId()).isEqualTo(childCategory.getId());
    }

    @Test
    public void shouldFindAllParentCategories() {
        List<Category> categories = categoryRepository.findAllParents();

        assertThat(categories.size()).isEqualTo(1);
        assertThat(categories.get(0).getId()).isEqualTo(parentCategory.getId());
        assertThat(categories.get(0).getName()).isEqualTo(parentCategory.getName());
    }
}
