package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.OrderStatus;

import java.util.List;

public interface OrderStatusDAO {
    List<OrderStatus> findByOrderId(int orderId);
    OrderStatus save(OrderStatus orderStatus, int orderId);
}
