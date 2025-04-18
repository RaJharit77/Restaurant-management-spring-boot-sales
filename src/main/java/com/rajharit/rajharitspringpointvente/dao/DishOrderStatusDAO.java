package com.rajharit.rajharitspringpointvente.dao;

import com.rajharit.rajharitspringpointvente.entities.DishOrderStatus;

import java.util.List;

public interface DishOrderStatusDAO {
    DishOrderStatus save(DishOrderStatus dishOrderStatus, int dishOrderId);
    List<DishOrderStatus> findByDishOrderId(int dishOrderId);
}
