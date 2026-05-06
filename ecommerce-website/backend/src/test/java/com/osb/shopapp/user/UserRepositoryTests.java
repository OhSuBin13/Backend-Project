package com.osb.shopapp.user;

import com.osb.shopapp.TestDataUtils;
import com.osb.shopapp.common.AppConstants;
import com.osb.shopapp.role.Role;
import com.osb.shopapp.role.RoleRepository;
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

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class UserRepositoryTests {

    @Container
    @ServiceConnection
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:9.2.0");

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private User userA;
    private User userB;

    @Autowired
    public UserRepositoryTests(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        // Save required entities (need to set IDs to null before inserting to avoid
        // errors related to MySQL's AUTO_INCREMENT counter not resetting between tests)
        Role adminRole = new Role(null, "ADMIN");
        adminRole = roleRepository.save(adminRole);

        userA = TestDataUtils.createUserA(Set.of(adminRole));
        userA.setId(null);
        userA = userRepository.save(userA);
        userB = TestDataUtils.createUserB(Set.of(adminRole));
        userB.setId(null);
        userB = userRepository.save(userB);
    }

    @Test
    public void shouldFindAllUsersByKeyword() {
        Sort sort = AppConstants.SORT_DIR.equals("asc") ?
                Sort.by(AppConstants.SORT_USERS_BY).ascending() : Sort.by(AppConstants.SORT_USERS_BY).descending();
        Pageable pageable = PageRequest.of(AppConstants.PAGE_NUMBER_INT, AppConstants.PAGE_SIZE_INT, sort);

        Page<User> page = userRepository.findAllByKeyword(pageable, "test user");

        assertThat(page).isNotNull();
        assertThat(page.getContent().size()).isEqualTo(2);
        assertThat(page.getContent().get(0).getId()).isEqualTo(userA.getId());
        assertThat(page.getContent().get(1).getId()).isEqualTo(userB.getId());
    }

    @Test
    public void shouldCalculateUserStatistics() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusMonths(1);

        UserStatsResponse statistics = userRepository.calculateStatistics(from, to);

        assertThat(statistics).isNotNull();
        assertThat(statistics.getMonthlyRegisteredUsers()).isEqualTo(2);
    }
}
