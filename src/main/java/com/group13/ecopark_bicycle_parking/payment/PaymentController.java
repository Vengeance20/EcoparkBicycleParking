package com.group13.ecopark_bicycle_parking.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // Cho phép Frontend gọi vào
@RestController
@RequestMapping("/apiv1/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // API tương ứng với nhánh POST /payment/topup() trong Sequence Diagram
    @PostMapping("/topup")
    public ResponseEntity<?> topupWallet(@RequestBody PaymentDTO.TopupRequest request) {
        try {
            PaymentDTO.TopupResponse response = paymentService.processTopup(request.getUserId(), request.getAmountVnd());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}