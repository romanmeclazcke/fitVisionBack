package org.example.fitvisionback.mercadopago;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

import java.util.Map;
import java.util.UUID;

public interface PaymentsService {
    String createPreference(UUID planId) throws MPException, MPApiException;
    void processWebhook(Map<String, Object> payload);
}
