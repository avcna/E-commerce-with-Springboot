package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    Double getTotalRevenue();

    @Query("SELECT o FROM Order o WHERE EXISTS " +
            "(SELECT 1 FROM OrderItem oi WHERE oi.order = o AND oi.product.user.userId = :sellerId)")
    Page<Order> findBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);
}
