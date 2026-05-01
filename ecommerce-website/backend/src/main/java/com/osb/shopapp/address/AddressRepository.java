package com.osb.shopapp.address;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    @EntityGraph(attributePaths = {"user"})
    Optional<Address> findWithAssociationById(Integer id);

    List<Address> findAllByUserId(Integer userId);

    @EntityGraph(attributePaths = {"user"})
    List<Address> findAllWithAssociationsByUserId(Integer userId);
}
