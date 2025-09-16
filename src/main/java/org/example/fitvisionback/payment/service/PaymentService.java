package org.example.fitvisionback.payment.service;

import com.mercadopago.resources.payment.Payment;

public interface PaymentService {
    void handlePayment(Payment payment);
}
