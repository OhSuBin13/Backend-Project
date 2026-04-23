package com.osb.shopapp.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findWithAssociationById(Integer id);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findWithAssociationsByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM User u
            WHERE u.name LIKE %:keyword%
            OR u.email LIKE %:keyword%
            """)
    Page<User> findAllByKeyword(Pageable pageable, String keyword);

    @Query("""
            SELECT new com.osb.shopapp.user.UserStatsResponse(
                COUNT(u.id)
            )
            FROM User u
            WHERE u.registeredAt >= :from AND u.registeredAt <= :to
            """)
    UserStatsResponse calculateStatistics(LocalDate from, LocalDate to);
}
