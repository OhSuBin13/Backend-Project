package com.osb.shopapp.product;

import com.osb.shopapp.TestDataUtils;
import com.osb.shopapp.category.Category;
import com.osb.shopapp.category.CategoryRepository;
import com.osb.shopapp.common.AppConstants;
import com.osb.shopapp.role.Role;
import com.osb.shopapp.role.RoleRepository;
import com.osb.shopapp.user.User;
import com.osb.shopapp.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class ProductRepositoryTests {

    @Container
    @ServiceConnection
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:9.2.0");

    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private Product productA;
    private Product productB;

    @Autowired
    public ProductRepositoryTests(
            ProductRepository productRepository, RoleRepository roleRepository,
            UserRepository userRepository, CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @BeforeEach
    public void setUp() {
        // Save required entities (need to set IDs to null before inserting to avoid
        // errors related to MySQL's AUTO_INCREMENT counter not resetting between tests
        Category category = TestDataUtils.createCategoryA();
        category.setId(null);
        category = categoryRepository.save(category);

        Role adminRole = new Role(null, "ADMIN");
        adminRole = roleRepository.save(adminRole);

        User userA = TestDataUtils.createUserA(Set.of(adminRole));
        userA.setId(null);
        userA = userRepository.save(userA);
        User userB = TestDataUtils.createUserB(Set.of(adminRole));
        userB.setId(null);
        userB = userRepository.save(userB);

        productA = TestDataUtils.createProductA(category, userA);
        productA.setId(null);
        productA = productRepository.save(productA);
        productB = TestDataUtils.createProductB(category, userB);
        productB.setId(null);
        productB = productRepository.save(productB);
    }

    @Test
    public void shouldFindAllProductsByKeyword() {
        Sort sort = AppConstants.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstants.SORT_PRODUCTS_BY).ascending() : Sort.by(AppConstants.SORT_PRODUCTS_BY).descending();
        Pageable pageable = PageRequest.of(AppConstants.PAGE_NUMBER_INT, AppConstants.PAGE_SIZE_INT, sort);

        Page<Product> page = productRepository.findAllByKeyword(pageable, "Test product");

        assertThat(page.getContent().size()).isEqualTo(2);
        assertThat(page.getContent().get(0).getId()).isEqualTo(productA.getId());
        assertThat(page.getContent().get(0).getName()).isEqualTo(productA.getName());
        assertThat(page.getContent().get(1).getId()).isEqualTo(productB.getId());
        assertThat(page.getContent().get(1).getName()).isEqualTo(productB.getName());
    }

    @Test
    public void shouldFindAllProductsBySellerIdNot() {
        Sort sort = AppConstants.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstants.SORT_PRODUCTS_BY).ascending() : Sort.by(AppConstants.SORT_PRODUCTS_BY).descending();
        Pageable pageable = PageRequest.of(AppConstants.PAGE_NUMBER_INT, AppConstants.PAGE_SIZE_INT, sort);

        Page<Product> page = productRepository.findAllBySellerIdNot(pageable, productA.getSeller().getId());

        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(productB.getId());
        assertThat(page.getContent().get(0).getName()).isEqualTo(productB.getName());
    }

    @Test
    public void shouldFindAllProductBySellerId() {
        Sort sort = AppConstants.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstants.SORT_PRODUCTS_BY).ascending() : Sort.by(AppConstants.SORT_PRODUCTS_BY).descending();
        Pageable pageable = PageRequest.of(AppConstants.PAGE_NUMBER_INT, AppConstants.PAGE_SIZE_INT, sort);

        Page<Product> page = productRepository.findAllBySellerId(pageable, productA.getSeller().getId());

        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(productA.getId());
        assertThat(page.getContent().get(0).getName()).isEqualTo(productA.getName());
    }

    @Test
    public void shouldFindAllProductsByCategoryId() {
        Sort sort = AppConstants.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstants.SORT_PRODUCTS_BY).ascending() : Sort.by(AppConstants.SORT_PRODUCTS_BY).descending();
        Pageable pageable = PageRequest.of(AppConstants.PAGE_NUMBER_INT, AppConstants.PAGE_SIZE_INT, sort);

        Page<Product> page = productRepository.findAllByCategoryId(pageable, productA.getCategory().getId(), productA.getSeller().getId());

        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(productB.getId());
        assertThat(page.getContent().get(0).getName()).isEqualTo(productB.getName());
    }

    @Test
    public void shouldFindAllProductsExceptSellerProductsByKeyword() {
        Sort sort = AppConstants.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstants.SORT_PRODUCTS_BY).ascending() : Sort.by(AppConstants.SORT_PRODUCTS_BY).descending();
        Pageable pageable = PageRequest.of(AppConstants.PAGE_NUMBER_INT, AppConstants.PAGE_SIZE_INT, sort);

        Page<Product> page =
                productRepository.findAllBySellerIdNotAndKeyword(pageable, "Test product", productA.getSeller().getId());

        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(productB.getId());
        assertThat(page.getContent().get(0).getName()).isEqualTo(productB.getName());
    }

    @Test
    public void shouldFindAllProductsBySellerIdAndKeyword() {
        Sort sort = AppConstants.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstants.SORT_PRODUCTS_BY).ascending() : Sort.by(AppConstants.SORT_PRODUCTS_BY).descending();
        Pageable pageable = PageRequest.of(AppConstants.PAGE_NUMBER_INT, AppConstants.PAGE_SIZE_INT, sort);

        Page<Product> page = productRepository.findAllBySellerIdAndKeyword(pageable, productA.getSeller().getId(), "Test product");

        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(productA.getId());
        assertThat(page.getContent().get(0).getName()).isEqualTo(productA.getName());
    }

    @Test
    public void shouldFindAllProductsByCategoryIdAndKeyword() {
        Sort sort = AppConstants.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstants.SORT_PRODUCTS_BY).ascending() : Sort.by(AppConstants.SORT_PRODUCTS_BY).descending();
        Pageable pageable = PageRequest.of(AppConstants.PAGE_NUMBER_INT, AppConstants.PAGE_SIZE_INT, sort);

        Page<Product> page =
                productRepository.findAllByCategoryIdAndKeyword(
                        pageable, productA.getCategory().getId(), "Test product", productA.getSeller().getId());

        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(productB.getId());
        assertThat(page.getContent().get(0).getName()).isEqualTo(productB.getName());
    }

    @Test
    public void shouldCalculateProductStatistics() {
        ZonedDateTime to = ZonedDateTime.now();
        ZonedDateTime from = to.minusMonths(1);

        ProductStatsResponse statistics = productRepository.calculateStatistics(from, to);

        assertThat(statistics).isNotNull();
        assertThat(statistics.getMonthlyListedProducts()).isEqualTo(2);
    }
}
