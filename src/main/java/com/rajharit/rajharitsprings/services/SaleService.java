package com.rajharit.rajharitsprings.services;

import com.rajharit.rajharitsprings.dao.DishDAO;
import com.rajharit.rajharitsprings.dao.OrderDAO;
import com.rajharit.rajharitsprings.dao.OrderDAOImpl;
import com.rajharit.rajharitsprings.entities.Order;
import com.rajharit.rajharitsprings.dtos.DishSoldDto;
import com.rajharit.rajharitsprings.entities.Dish;
import com.rajharit.rajharitsprings.entities.DishOrder;
import com.rajharit.rajharitsprings.entities.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaleService {
    private final OrderDAO orderDAO;
    private final DishDAO dishDAO;
    private static final Logger logger = LoggerFactory.getLogger(SaleService.class);

    public SaleService(OrderDAO orderDAO, DishDAO dishDAO) {
        this.orderDAO = orderDAO;
        this.dishDAO = dishDAO;
    }

    @Transactional(readOnly = true)
    public List<DishSoldDto> findAllDeliveredDishes() {
        List<Order> deliveredOrders = orderDAO.findByStatus(StatusType.DELIVERED);

        logger.info("Found {} delivered orders", deliveredOrders.size());
        for (Order order : deliveredOrders) {
            logger.info("Order {} has {} dish orders", order.getReference(), order.getDishOrders().size());
        }

        Map<Integer, DishSoldDto> dishSales = new HashMap<>();

        for (Order order : deliveredOrders) {
            for (DishOrder dishOrder : order.getDishOrders()) {
                Dish dish = dishDAO.findById(dishOrder.getDish().getId());

                logger.info("Processing dish {} with quantity {}", dish.getName(), dishOrder.getQuantity());

                dishSales.compute(dish.getId(), (k, v) -> {
                    if (v == null) {
                        v = new DishSoldDto();
                        v.setDishIdentifier(dish.getId());
                        v.setDishName(dish.getName());
                        v.setQuantitySold(0);
                    }
                    v.setQuantitySold(v.getQuantitySold() + dishOrder.getQuantity());
                    return v;
                });
            }
        }

        return new ArrayList<>(dishSales.values());
    }
}