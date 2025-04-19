package com.rajharit.rajharitsprings.mappers;

import com.rajharit.rajharitsprings.dao.DishDAO;
import com.rajharit.rajharitsprings.dtos.DishOrderDto;
import com.rajharit.rajharitsprings.dtos.OrderDto;
import com.rajharit.rajharitsprings.entities.DishOrder;
import com.rajharit.rajharitsprings.entities.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    private final DishDAO dishDAO;

    public OrderMapper(DishDAO dishDAO) {
        this.dishDAO = dishDAO;
    }

    public OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setReference(order.getReference());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setActualStatus(order.getActualStatus());

        Map<Integer, DishOrderDto> dishMap = new HashMap<>();

        for (DishOrder dishOrder : order.getDishOrders()) {
            DishOrderDto existing = dishMap.get(dishOrder.getDish().getId());

            if (existing == null) {
                existing = toDishOrderDto(dishOrder);
                dishMap.put(dishOrder.getDish().getId(), existing);
            } else {
                existing.setQuantity(existing.getQuantity() + dishOrder.getQuantity());
                if (dishOrder.getStatus().ordinal() > existing.getActualOrderStatus().ordinal()) {
                    existing.setActualOrderStatus(dishOrder.getStatus());
                }
            }
        }

        dto.setDishOrders(new ArrayList<>(dishMap.values()));

        double totalAmount = dishMap.values().stream()
                .mapToDouble(dish -> dish.getQuantity() * dishDAO.findById(dish.getDishId()).getUnitPrice())
                .sum();
        dto.setTotalAmount(totalAmount);

        return dto;
    }

    private DishOrderDto toDishOrderDto(DishOrder dishOrder) {
        DishOrderDto dto = new DishOrderDto();
        dto.setDishId(dishOrder.getDish().getId());
        dto.setDishName(dishOrder.getDish().getName());
        dto.setQuantity(dishOrder.getQuantity());
        dto.setActualOrderStatus(dishOrder.getStatus());
        return dto;
    }
}