package com.aziz.gateway.repository;

import com.aziz.gateway.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUserIdOrderByDefaultShippingDescCreatedAtAsc(Long userId);
    Optional<Address> findByIdAndUserId(Long id, Long userId);
    Optional<Address> findFirstByUserIdAndIdNotOrderByCreatedAtAsc(Long userId, Long id);
    long countByUserId(Long userId);

    @Modifying
    @Query("UPDATE Address a SET a.defaultShipping = false WHERE a.user.id = :userId")
    void clearDefaultShipping(@Param("userId") Long userId);

    @Modifying
    @Query("""
           UPDATE Address a SET a.defaultShipping = false
           WHERE a.user.id = :userId AND a.id <> :id AND a.defaultShipping = true
           """)
    void clearOtherDefaults(@Param("userId") Long userId, @Param("id") Long id);
}