package com.group13.ecopark_bicycle_parking.payment;

import com.group13.ecopark_bicycle_parking.bicycle.BikeCategory;
import com.group13.ecopark_bicycle_parking.bicycle.BikeCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ConfigService {

    @Autowired private BikeCategoryRepository categoryRepository;
    @Autowired private SystemConfigRepository configRepository;

    // Lấy cấu hình hiện tại
    public Map<String, Object> getSystemConfig() {
        BigDecimal singlePrice = categoryRepository.findByName("SINGLE")
                .map(BikeCategory::getBaseFee).orElse(new BigDecimal("50000"));
        BigDecimal doublePrice = categoryRepository.findByName("DOUBLE")
                .map(BikeCategory::getBaseFee).orElse(new BigDecimal("20000"));
        // 🔴 THÊM DÒNG NÀY: Lấy giá xe điện (Giả sử tên trong DB là "ELECTRIC")
        BigDecimal electricPrice = categoryRepository.findByName("ELECTRIC")
                .map(BikeCategory::getBaseFee).orElse(new BigDecimal("30000"));

        String openHour = configRepository.findByConfigKey("OPEN_HOUR")
                .map(SystemConfig::getConfigValue).orElse("06:00");
        String closeHour = configRepository.findByConfigKey("CLOSE_HOUR")
                .map(SystemConfig::getConfigValue).orElse("22:00");

        return Map.of(
                "singleBikePrice", singlePrice,
                "doubleBikePrice", doublePrice,
                "electricBikePrice", electricPrice, // 🔴 THÊM VÀO MAP
                "openHour", openHour,
                "closeHour", closeHour
        );
    }

    // Cập nhật cấu hình
    @Transactional
    public void updateSystemConfig(BigDecimal singlePrice, BigDecimal doublePrice, BigDecimal electricPrice, String openHour, String closeHour) { // 🔴 THÊM THAM SỐ electricPrice
        categoryRepository.findByName("SINGLE").ifPresent(cat -> {
            cat.setBaseFee(singlePrice);
            categoryRepository.save(cat);
        });

        categoryRepository.findByName("DOUBLE").ifPresent(cat -> {
            cat.setBaseFee(doublePrice);
            categoryRepository.save(cat);
        });

        // 🔴 THÊM DÒNG NÀY: Cập nhật giá xe điện
        categoryRepository.findByName("ELECTRIC").ifPresent(cat -> {
            cat.setBaseFee(electricPrice);
            categoryRepository.save(cat);
        });

        saveConfig("OPEN_HOUR", openHour);
        saveConfig("CLOSE_HOUR", closeHour);
    }



    private void saveConfig(String key, String value) {
        SystemConfig config = configRepository.findByConfigKey(key)
                .orElse(SystemConfig.builder().configKey(key).build());
        config.setConfigValue(value);
        configRepository.save(config);
    }
}