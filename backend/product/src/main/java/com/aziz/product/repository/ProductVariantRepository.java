package com.aziz.product.repository;

import com.aziz.product.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    boolean existsBySkuAndIdNot(String sku, Long id);

    @Query("SELECT v.sku FROM ProductVariant v WHERE v.sku IN :skus")
    List<String> findExistingSkus(@Param("skus") Collection<String> skus);

    @Query("select v from ProductVariant v join fetch v.product where v.id in :ids")
    List<ProductVariant> findAllWithProductByIdIn(@Param("ids") Collection<Long> ids);

    // version is bumped by hand so a vendor's concurrent edit can't overwrite a reservation with stale stock
    @Modifying
    @Query("""
       update ProductVariant v set v.stockQuantity = v.stockQuantity - :qty, v.version = v.version + 1
       where v.id = :id and v.stockQuantity >= :qty
       """)
    int decrementStock(@Param("id") Long id, @Param("qty") int qty);

    @Modifying
    @Query("update ProductVariant v set v.stockQuantity = v.stockQuantity + :qty, v.version = v.version + 1 where v.id = :id")
    int incrementStock(@Param("id") Long id, @Param("qty") int qty);
}