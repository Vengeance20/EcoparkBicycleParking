package com.group13.ecopark_bicycle_parking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Đánh dấu đây là "Người thủ kho" quản lý dữ liệu
public interface BicycleRepository extends JpaRepository<Bicycle, Long> {
    
    // ĐỂ TRỐNG! Bạn không cần viết thêm bất kỳ dòng code nào ở đây cả.
    
}