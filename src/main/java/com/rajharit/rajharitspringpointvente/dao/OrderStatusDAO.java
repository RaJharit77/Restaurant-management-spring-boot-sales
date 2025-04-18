package com.rajharit.rajharitspringpointvente.dao;

import com.rajharit.rajharitspringpointvente.entities.OrderStatus;

import java.util.List;

public interface OrderStatusDAO {
    List<OrderStatus> findByOrderId(int orderId);
    OrderStatus save(OrderStatus orderStatus, int orderId);
}
