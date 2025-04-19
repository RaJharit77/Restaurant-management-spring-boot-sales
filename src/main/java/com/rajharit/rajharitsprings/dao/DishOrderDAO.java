package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.DishOrder;
import com.rajharit.rajharitsprings.entities.StatusType;

import java.util.List;

public interface DishOrderDAO {
    DishOrder findById(int id);
    List<DishOrder> findByOrderId(int orderId);
    DishOrder save(DishOrder dishOrder);

    List<DishOrder> findByDishId(int dishId);

    void updateStatus(DishOrder dishOrder);
}