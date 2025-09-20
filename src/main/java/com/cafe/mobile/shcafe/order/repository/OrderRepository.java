package com.cafe.mobile.shcafe.order.repository;

import com.cafe.mobile.shcafe.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

}
