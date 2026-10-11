package com.aziz.order.repository;

import com.aziz.order.model.Cart;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @EntityGraph(attributePaths = "items")
    Optional<Cart> findByUserId(Long userId);

    @EntityGraph(attributePaths = "items")
    Optional<Cart> findByGuestId(String guestId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cart c where c.userId = :userId")
    Optional<Cart> findByUserIdForUpdate(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Cart c where c.guestId is not null and c.updatedAt < :cutoff")
    int deleteStaleGuestCarts(@Param("cutoff") Instant cutoff);   // items go via ON DELETE CASCADE
}