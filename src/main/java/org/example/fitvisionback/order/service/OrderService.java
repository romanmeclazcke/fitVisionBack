package org.example.fitvisionback.order.service;

import org.example.fitvisionback.order.entity.Order;

import java.util.UUID;

public interface OrderService {
    Order createOrder(UUID planId, String id);
    Order findByMercadoPagoPreferenceId(String preferenceId);
    void save(Order order);
    void updatePreferenceId(UUID id, String id1);
    Order getById(String externalReference);
}
