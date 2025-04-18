package com.rajharit.rajharitspringpointvente.dao;

import com.rajharit.rajharitspringpointvente.entities.Order;
import com.rajharit.rajharitspringpointvente.entities.StatusType;

import java.util.List;

public interface OrderDAO {
    List<Order> getAll();
    Order findById(int id);
    Order findByReference(String reference);
    Order save(Order order);
    void updateStatus(int orderId, StatusType status);
    List<Order> findByCustomerId(int customerId);
    void delete(int id);
}