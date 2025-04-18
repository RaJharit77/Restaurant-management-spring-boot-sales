package com.rajharit.rajharitspringpointvente.dao;

import com.rajharit.rajharitspringpointvente.entities.DishOrder;
import com.rajharit.rajharitspringpointvente.entities.StatusType;

import java.util.List;

public interface DishOrderDAO {
    DishOrder findById(int id);
    List<DishOrder> findByOrderId(int orderId);
    DishOrder save(DishOrder dishOrder);

    List<DishOrder> findByDishId(int dishId);

    void updateStatus(int dishOrderId, StatusType status);
}