package com.group13.ecopark_bicycle_parking.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/apiv1/admin/config")
@CrossOrigin(origins = "*")
public class ConfigController {

    @Autowired private ConfigService configService;

    @GetMapping
    public ResponseEntity<?> getConfig() {
        return ResponseEntity.ok(configService.getSystemConfig());
    }

    @PutMapping
    public ResponseEntity<?> updateConfig(@RequestBody Map<String, Object> payload) {
        try {
            BigDecimal singlePrice = new BigDecimal(payload.get("singleBikePrice").toString());
            BigDecimal doublePrice = new BigDecimal(payload.get("doubleBikePrice").toString());
            // 🔴 THÊM DÒNG NÀY: Lấy giá xe điện từ payload
            BigDecimal electricPrice = new BigDecimal(payload.get("electricBikePrice").toString());
            String openHour = payload.get("openHour").toString();
            String closeHour = payload.get("closeHour").toString();

            // 🔴 TRUYỀN electricPrice VÀO HÀM
            configService.updateSystemConfig(singlePrice, doublePrice, electricPrice, openHour, closeHour);
            return ResponseEntity.ok("Cập nhật cấu hình thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}