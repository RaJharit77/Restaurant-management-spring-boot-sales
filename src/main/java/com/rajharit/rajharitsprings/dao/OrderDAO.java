package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.Order;
import com.rajharit.rajharitsprings.entities.StatusType;

import java.util.List;

public interface OrderDAO {
    List<Order> getAll();
    Order findById(int id);

    List<Order> findByStatus(StatusType status);

    Order findByReference(String reference);
    Order save(Order order);
    void updateStatus(int orderId, StatusType status);
    List<Order> findByCustomerId(int customerId);
    void delete(int id);
}