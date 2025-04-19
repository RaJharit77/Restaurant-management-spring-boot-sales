package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.DishOrderStatus;

import java.util.List;

public interface DishOrderStatusDAO {
    DishOrderStatus save(DishOrderStatus dishOrderStatus, int dishOrderId);
    List<DishOrderStatus> findByDishOrderId(int dishOrderId);
}
