package com.rajharit.rajharitspringpointvente.controllers;

import com.rajharit.rajharitspringpointvente.dtos.OrderDto;
import com.rajharit.rajharitspringpointvente.dtos.OrderUpdateDto;
import com.rajharit.rajharitspringpointvente.entities.StatusType;
import com.rajharit.rajharitspringpointvente.exceptions.BusinessException;
import com.rajharit.rajharitspringpointvente.exceptions.ResourceNotFoundException;
import com.rajharit.rajharitspringpointvente.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{reference}")
    public ResponseEntity<OrderDto> getOrderByReference(@PathVariable String reference) {
        try {
            OrderDto order = orderService.getOrderByReference(reference);
            return ResponseEntity.ok(order);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{reference}/dishes")
    public ResponseEntity<OrderDto> updateOrderDishes(
            @PathVariable String reference,
            @RequestBody OrderUpdateDto orderUpdate) {
        try {
            OrderDto updatedOrder = orderService.updateOrderDishes(reference, orderUpdate);
            return ResponseEntity.ok(updatedOrder);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{reference}/dishes/{dishId}")
    public ResponseEntity<Void> updateDishStatus(
            @PathVariable String reference,
            @PathVariable int dishId,
            @RequestParam StatusType status) {
        try {
            orderService.updateDishStatus(reference, dishId, status);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}