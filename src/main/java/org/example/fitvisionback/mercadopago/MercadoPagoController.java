package org.example.fitvisionback.mercadopago;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import jdk.jfr.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("mercado-pago")
public class MercadoPagoController {

    @Autowired
    @Name("mercadoPagoServiceImpl")
    private PaymentsService paymentsService;


    @PostMapping("/create-preference/{planId}")
    public ResponseEntity<String> createPreference(
            @PathVariable UUID planId
            ) throws MPException, MPApiException {
        String preferenceId = paymentsService.createPreference(planId);
        return ResponseEntity.ok(preferenceId);
    }



    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveWebhook(@RequestBody Map<String, Object> payload) {
        paymentsService.processWebhook(payload);
        return ResponseEntity.ok().build();
    }
}
