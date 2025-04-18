package com.rajharit.rajharitspringpointvente.services;

import com.rajharit.rajharitspringpointvente.dtos.*;
import com.rajharit.rajharitspringpointvente.entities.*;
import com.rajharit.rajharitspringpointvente.exceptions.BusinessException;
import com.rajharit.rajharitspringpointvente.exceptions.ResourceNotFoundException;
import com.rajharit.rajharitspringpointvente.mappers.OrderMapper;
import com.rajharit.rajharitspringpointvente.dao.OrderDAO;
import com.rajharit.rajharitspringpointvente.dao.DishDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderDAO orderDAO;
    private final DishDAO dishDAO;
    private final OrderMapper orderMapper;

    public OrderService(OrderDAO orderDAO, DishDAO dishDAO, OrderMapper orderMapper) {
        this.orderDAO = orderDAO;
        this.dishDAO = dishDAO;
        this.orderMapper = orderMapper;
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderByReference(String reference) {
        Order order = orderDAO.findByReference(reference);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with reference: " + reference);
        }

        order.getDishOrders().forEach(dishOrder -> {
            Dish dish = dishDAO.findById(dishOrder.getDish().getId());
            dishOrder.setDish(dish);
        });

        double totalAmount = order.getDishOrders().stream()
                .mapToDouble(dishOrder -> dishOrder.getDish().getUnitPrice() * dishOrder.getQuantity())
                .sum();

        OrderDto dto = orderMapper.toDto(order);
        dto.setTotalAmount(totalAmount);
        return dto;
    }

    @Transactional
    public OrderDto updateOrderDishes(String reference, OrderUpdateDto orderUpdate) {
        if (orderUpdate == null || orderUpdate.getDishes() == null) {
            throw new BusinessException("Order update data cannot be null");
        }

        Order order = orderDAO.findByReference(reference);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with reference: " + reference);
        }

        if (orderUpdate.getStatus() != null) {
            validateStatusTransition(order.getStatus(), StatusType.valueOf(orderUpdate.getStatus()));
            order.setStatus(StatusType.valueOf(orderUpdate.getStatus()));
        }

        List<DishOrder> dishOrders = orderUpdate.getDishes().stream()
                .map(dto -> {
                    Dish dish = dishDAO.findById(dto.getDishId());
                    if (dish == null) {
                        throw new ResourceNotFoundException("Dish not found with id: " + dto.getDishId());
                    }

                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setDish(dish);
                    dishOrder.setQuantity(dto.getQuantity());
                    dishOrder.setStatus(
                            order.getStatus() == StatusType.CONFIRMED ?
                                    StatusType.CONFIRMED :
                                    StatusType.CREATED
                    );
                    return dishOrder;
                })
                .collect(Collectors.toList());

        order.setDishOrders(dishOrders);
        Order updatedOrder = orderDAO.save(order);
        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    public void updateDishStatus(String reference, int dishId, StatusType status) {
        Order order = orderDAO.findByReference(reference);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with reference: " + reference);
        }

        DishOrder dishOrder = order.getDishOrders().stream()
                .filter(dishOrderItem -> dishOrderItem.getDish().getId() == dishId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Dish with id " + dishId + " not found in order " + reference));

        validateStatusTransition(dishOrder.getStatus(), status);

        dishOrder.setStatus(status);

        orderDAO.save(order);
    }

    private void validateStatusTransition(StatusType current, StatusType next) {
        if (current == next) {
            return;
        }

        switch (current) {
            case CREATED:
                if (next != StatusType.CONFIRMED) {
                    throw new BusinessException("Invalid status transition from CREATED to " + next);
                }
                break;
            case CONFIRMED:
                if (next != StatusType.IN_PREPARATION) {
                    throw new BusinessException("Invalid status transition from CONFIRMED to " + next);
                }
                break;
            case IN_PREPARATION:
                if (next != StatusType.FINISHED) {
                    throw new BusinessException("Invalid status transition from IN_PREPARATION to " + next);
                }
                break;
            case FINISHED:
                if (next != StatusType.COMPLETED) {
                    throw new BusinessException("Invalid status transition from FINISHED to " + next);
                }
                break;
            case COMPLETED:
                if (next != StatusType.SERVED) {
                    throw new BusinessException("Invalid status transition from COMPLETED to " + next);
                }
                break;
            case SERVED:
                throw new BusinessException("Cannot change status from SERVED");
            default:
                throw new BusinessException("Unknown status: " + current);
        }
    }
}