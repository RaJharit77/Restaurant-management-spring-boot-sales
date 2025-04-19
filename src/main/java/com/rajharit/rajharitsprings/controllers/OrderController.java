package com.rajharit.rajharitsprings.controllers;

import com.rajharit.rajharitsprings.dtos.*;
import com.rajharit.rajharitsprings.entities.StatusType;
import com.rajharit.rajharitsprings.exceptions.BusinessException;
import com.rajharit.rajharitsprings.exceptions.ResourceNotFoundException;
import com.rajharit.rajharitsprings.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{reference}")
    public ResponseEntity<?> getOrderByReference(@PathVariable String reference) {
        try {
            OrderDto order = orderService.getOrderByReference(reference);
            return ResponseEntity.ok(order);
        } catch (ResourceNotFoundException e) {
            logger.error("Order not found: " + reference, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting order: " + reference, e);
            return ResponseEntity.internalServerError().body("Error retrieving order");
        }
    }

    @PostMapping("/{reference}")
    public ResponseEntity<?> createOrder(@PathVariable String reference) {
        try {
            OrderDto createdOrder = orderService.createOrder(reference);
            return ResponseEntity.ok(createdOrder);
        } catch (BusinessException e) {
            logger.error("Business error creating order: " + reference, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating order: " + reference, e);
            return ResponseEntity.internalServerError().body("Error creating order");
        }
    }

    @GetMapping("/{reference}/dishes")
    public ResponseEntity<?> getDishesInOrder(@PathVariable String reference) {
        try {
            List<DishOrderDto> dishes = orderService.getDishesInOrder(reference);
            return ResponseEntity.ok(dishes);
        } catch (ResourceNotFoundException e) {
            logger.error("Order not found: " + reference, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting dishes for order: " + reference, e);
            return ResponseEntity.internalServerError().body("Error retrieving dishes");
        }
    }

    @PostMapping("/{reference}/dishes")
    public ResponseEntity<?> addDishToOrder(
            @PathVariable String reference,
            @Valid @RequestBody DishOrderDto dishOrderDto) {
        try {
            OrderDto updatedOrder = orderService.addDishToOrder(reference, dishOrderDto);
            return ResponseEntity.ok(updatedOrder);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne est survenue");
        }
    }

    @PutMapping("/{reference}/dishes")
    public ResponseEntity<?> updateOrderDishes(
            @PathVariable String reference,
            @RequestBody OrderUpdateDto orderUpdate) {
        try {
            OrderDto updatedOrder = orderService.updateOrderDishes(reference, orderUpdate);
            return ResponseEntity.ok(updatedOrder);
        } catch (ResourceNotFoundException e) {
            logger.error("Order not found: " + reference, e);
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            logger.error("Business error updating order dishes: " + reference, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating order dishes: " + reference, e);
            return ResponseEntity.internalServerError().body("Error updating order dishes");
        }
    }

    @GetMapping("/{reference}/dishes/{dishId}/status")
    public ResponseEntity<?> getDishStatus(
            @PathVariable String reference,
            @PathVariable Integer dishId) {
        try {
            StatusType status = orderService.getDishStatus(reference, dishId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("dishId", dishId);
            response.put("reference", reference);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error retrieving dish status"));
        }
    }

    @PutMapping("/{reference}/dishes/{dishId}")
    public ResponseEntity<?> updateDishStatus(
            @PathVariable String reference,
            @PathVariable Integer dishId,
            @RequestBody StatusUpdateDto statusUpdate) {
        try {
            OrderDto updatedOrder = orderService.updateDishStatus(reference, dishId, statusUpdate.getStatus());
            return ResponseEntity.ok(updatedOrder);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error updating dish status"));
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteOrderId(int id) {
        orderService.deleteOrderId(id);
        return ResponseEntity.noContent().build();
    }
}