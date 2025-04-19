package com.rajharit.rajharitsprings.services;

import com.rajharit.rajharitsprings.dao.*;
import com.rajharit.rajharitsprings.dtos.*;
import com.rajharit.rajharitsprings.entities.*;
import com.rajharit.rajharitsprings.exceptions.BusinessException;
import com.rajharit.rajharitsprings.exceptions.ResourceNotFoundException;
import com.rajharit.rajharitsprings.mappers.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderDAO orderDAO;
    private final DishDAO dishDAO;
    private final DishOrderDAO dishOrderDAO;
    private final OrderStatusDAO orderStatusDAO;
    private final DishOrderStatusDAO dishOrderStatusDAO;
    private final OrderMapper orderMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderDAO orderDAO, DishDAO dishDAO, DishOrderDAO dishOrderDAO, OrderStatusDAO orderStatusDAO, DishOrderStatusDAO dishOrderStatusDAO, OrderMapper orderMapper) {
        this.orderDAO = orderDAO;
        this.dishDAO = dishDAO;
        this.dishOrderDAO = dishOrderDAO;
        this.orderStatusDAO = orderStatusDAO;
        this.dishOrderStatusDAO = dishOrderStatusDAO;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderDto createOrder(String reference) {
        try {
            Order existingOrder = orderDAO.findByReference(reference);
            if (existingOrder != null) {
                throw new BusinessException("La référence de commande existe déjà: " + reference);
            }

            Order newOrder = new Order();
            newOrder.setReference(reference);
            newOrder.setCreatedAt(LocalDateTime.now());
            newOrder.setActualStatus(StatusType.CREATED);

            Order savedOrder = orderDAO.save(newOrder);

            OrderStatus initialStatus = new OrderStatus();
            initialStatus.setStatus(StatusType.CREATED);
            initialStatus.setChangedAt(LocalDateTime.now());
            orderStatusDAO.save(initialStatus, savedOrder.getOrderId());

            return orderMapper.toDto(savedOrder);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la création de la commande", e);
            throw new BusinessException("Erreur lors de la création de la commande: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public StatusType getDishStatus(String reference, int dishId) {
        Order order = orderDAO.findByReference(reference);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with reference: " + reference);
        }

        List<DishOrder> dishOrders = dishOrderDAO.findByOrderId(order.getOrderId());

        return dishOrders.stream()
                .filter(dishOrder -> dishOrder.getDish().getId() == dishId)
                .findFirst()
                .map(DishOrder::getStatus)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dish with id " + dishId + " not found in order " + reference));
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderByReference(String reference) {
        try {
            Order order = orderDAO.findByReference(reference);
            if (order == null) {
                throw new ResourceNotFoundException("Order not found with reference: " + reference);
            }

            List<DishOrder> dishOrders = dishOrderDAO.findByOrderId(order.getOrderId());
            dishOrders.forEach(dishOrder -> {
                Dish dish = dishDAO.findById(dishOrder.getDish().getId());
                dishOrder.setDish(dish);

                List<DishOrderStatus> statusHistory = dishOrderStatusDAO.findByDishOrderId(dishOrder.getDishOrderId());
                dishOrder.setStatusHistory(statusHistory);

                if (!statusHistory.isEmpty()) {
                    dishOrder.setStatus(statusHistory.getLast().getStatus());
                }
            });
            order.setDishOrders(dishOrders);

            List<OrderStatus> orderStatusHistory = orderStatusDAO.findByOrderId(order.getOrderId());
            order.setStatusHistory(orderStatusHistory);

            if (!orderStatusHistory.isEmpty()) {
                order.setActualStatus(orderStatusHistory.getLast().getStatus());
            }
            OrderDto dto = orderMapper.toDto(order);
            dto.setTotalAmount(order.getTotalAmount());
            return dto;
        } catch (Exception e) {
            logger.error("Error getting order by reference: " + reference, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<DishOrderDto> getDishesInOrder(String reference) {
        Order order = orderDAO.findByReference(reference);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with reference: " + reference);
        }

        List<DishOrder> dishOrders = dishOrderDAO.findByOrderId(order.getOrderId());

        return dishOrders.stream()
                .map(dishOrder -> {
                    Dish dish = dishDAO.findById(dishOrder.getDish().getId());
                    DishOrderDto dto = new DishOrderDto();
                    dto.setDishId(dish.getId());
                    dto.setDishName(dish.getName());
                    dto.setQuantity(dishOrder.getQuantity());
                    dto.setActualOrderStatus(dishOrder.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto addDishToOrder(String reference, DishOrderDto dishOrderDto) {
        try {
            Order order = orderDAO.findByReference(reference);
            if (order == null) {
                throw new ResourceNotFoundException("Commande non trouvée: " + reference);
            }

            Dish dish = dishDAO.findById(dishOrderDto.getDishId());
            if (dish == null) {
                throw new ResourceNotFoundException("Plat non trouvé avec l'ID: " + dishOrderDto.getDishId());
            }

            List<DishOrder> existingDishOrders = dishOrderDAO.findByOrderId(order.getOrderId());
            Optional<DishOrder> existingDishOrder = existingDishOrders.stream()
                    .filter(d -> d.getDish().getId() == dishOrderDto.getDishId())
                    .findFirst();

            if (existingDishOrder.isPresent()) {
                DishOrder dishOrder = existingDishOrder.get();
                dishOrder.setQuantity(dishOrder.getQuantity() + dishOrderDto.getQuantity());
                dishOrderDAO.save(dishOrder);
            } else {
                DishOrder dishOrder = new DishOrder();
                dishOrder.setDish(dish);
                dishOrder.setOrder(order);
                dishOrder.setQuantity(dishOrderDto.getQuantity());
                dishOrder.setStatus(StatusType.CREATED);

                DishOrder savedDishOrder = dishOrderDAO.save(dishOrder);

                DishOrderStatus initialStatus = new DishOrderStatus();
                initialStatus.setStatus(StatusType.CREATED);
                initialStatus.setChangedAt(LocalDateTime.now());
                dishOrderStatusDAO.save(initialStatus, savedDishOrder.getDishOrderId());
            }

            return orderMapper.toDto(order);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'ajout du plat", e);
            throw new BusinessException("Erreur lors de l'ajout du plat: " + e.getMessage());
        }
    }

    @Transactional
    public OrderDto updateDishStatus(String reference, int dishId, StatusType status) {
        Order order = orderDAO.findByReference(reference);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with reference: " + reference);
        }

        List<DishOrder> dishOrders = dishOrderDAO.findByOrderId(order.getOrderId());
        order.setDishOrders(dishOrders);

        List<DishOrder> matchingDishOrders = dishOrders.stream()
                .filter(d -> d.getDish().getId() == dishId)
                .toList();

        if (matchingDishOrders.isEmpty()) {
            throw new ResourceNotFoundException("Dish with id " + dishId + " not found in order " + reference);
        }

        for (DishOrder dishOrder : matchingDishOrders) {
            validateDishStatusTransition(dishOrder.getStatus(), status);
            dishOrder.setStatus(status);
            dishOrderDAO.updateStatus(dishOrder);

            DishOrderStatus dishOrderStatus = new DishOrderStatus();
            dishOrderStatus.setStatus(status);
            dishOrderStatus.setChangedAt(LocalDateTime.now());
            dishOrderStatusDAO.save(dishOrderStatus, dishOrder.getDishOrderId());
        }

        boolean allDelivered = dishOrders.stream()
                .allMatch(d -> d.getStatus() == StatusType.DELIVERED);

        if (allDelivered) {
            order.setActualStatus(StatusType.DELIVERED);
            orderDAO.updateStatus(order.getOrderId(), StatusType.DELIVERED);

            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setStatus(StatusType.DELIVERED);
            orderStatus.setChangedAt(LocalDateTime.now());
            orderStatusDAO.save(orderStatus, order.getOrderId());
        }

        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto updateOrderDishes(String reference, OrderUpdateDto orderUpdate) {
        Order order = orderDAO.findByReference(reference);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (orderUpdate.getOrderStatus() != null) {
            validateDishStatusTransition(order.getActualStatus(), orderUpdate.getOrderStatus());
            order.setActualStatus(orderUpdate.getOrderStatus());
        }

        order.getDishOrders().clear();

        orderUpdate.getDishes().forEach(dto -> {
            Dish dish = dishDAO.findById(dto.getDishIdentifier());
            if (dish == null) {
                throw new ResourceNotFoundException("Dish not found: " + dto.getDishIdentifier());
            }

            DishOrder dishOrder = new DishOrder();
            dishOrder.setDish(dish);
            dishOrder.setOrder(order);
            dishOrder.setQuantity(dto.getQuantity());
            dishOrder.setStatus(order.getActualStatus());

            dishOrderDAO.save(dishOrder);
            order.getDishOrders().add(dishOrder);
        });

        return orderMapper.toDto(order);
    }

    private void validateDishStatusTransition(StatusType current, StatusType next) {
        if (current == next) {
            return;
        }

        switch (current) {
            case CREATED:
                if (next != StatusType.CONFIRMED && next != StatusType.IN_PROGRESS) {
                    throw new BusinessException("Invalid status transition from CREATED to " + next);
                }
                break;
            case CONFIRMED:
                if (next != StatusType.IN_PROGRESS) {
                    throw new BusinessException("Invalid status transition from CONFIRMED to " + next);
                }
                break;
            case IN_PROGRESS:
                if (next != StatusType.FINISHED && next != StatusType.DELIVERED) {
                    throw new BusinessException("Invalid status transition from IN_PROGRESS to " + next);
                }
                break;
            case FINISHED:
                if (next != StatusType.DELIVERED) {
                    throw new BusinessException("Invalid status transition from FINISHED to " + next);
                }
                break;
            case DELIVERED:
                throw new BusinessException("Cannot change status from DELIVERED");
        }
    }

    public List<ProcessingTimeDto> getProcessingTimesForDish(int dishId) {
        List<Order> allOrders = orderDAO.getAll();
        List<ProcessingTimeDto> processingTimes = new ArrayList<>();

        for (Order order : allOrders) {
            if (order.getDishOrders() == null) continue;

            for (DishOrder dishOrder : order.getDishOrders()) {
                if (dishOrder.getDish().getId() == dishId &&
                        dishOrder.getStatusHistory() != null) {

                    LocalDateTime inProgressTime = null;
                    LocalDateTime deliveredTime = null;

                    for (DishOrderStatus status : dishOrder.getStatusHistory()) {
                        if (status.getStatus() == StatusType.IN_PROGRESS) {
                            inProgressTime = status.getChangedAt();
                        } else if (status.getStatus() == StatusType.DELIVERED) {
                            deliveredTime = status.getChangedAt();
                        }
                    }

                    if (inProgressTime != null && deliveredTime != null) {
                        long durationSeconds = Duration.between(inProgressTime, deliveredTime).getSeconds();

                        ProcessingTimeDto dto = new ProcessingTimeDto();
                        dto.setDishId(dishId);
                        dto.setDishName(dishOrder.getDish().getName());
                        dto.setPreparationDuration(durationSeconds);
                        dto.setSalesPoint("PDV-" + order.getOrderId() % 2 + 1);

                        processingTimes.add(dto);
                    }
                }
            }
        }

        return processingTimes;
    }

    public void deleteOrderId(int id) {
        orderDAO.delete(id);
    }
}