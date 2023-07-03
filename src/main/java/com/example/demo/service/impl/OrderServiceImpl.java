package com.example.demo.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Order;
import com.example.demo.model.Users;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findByOrderCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode);
    }

    @Override
    public List<Order> getOrdersByUserIdAndStatusTrue(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Long countByOrderStatusTrue() {
        return orderRepository.countByOrderStatusTrue();
    }

    @Override
    public List<Order> getAllOrder() {
        List<Order> orders = orderRepository.findAll();
        Collections.reverse(orders);
        return orders;
    }

}