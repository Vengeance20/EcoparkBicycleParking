package com.group13.ecopark_bicycle_parking.bicycle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Integer> {

    // 1. Tìm xe theo mã code (Quan trọng nhất để kết nối RentalController)
    Optional<Bike> findByBikeCode(String bikeCode);

    // 2. Tìm xe theo mã code và chưa bị xóa
    Optional<Bike> findByBikeCodeAndIsDeletedFalse(String bikeCode);
    
    // 3. Lấy danh sách xe theo bãi
    List<Bike> findByStationStationId(Integer stationId);

    List<Bike> findByStationStationIdAndIsDeletedFalse(Integer stationId);

    List<Bike> findByStationStationIdAndStatusAndIsDeletedFalse(Integer stationId, String status);

    // 4. Các hàm đếm (Sửa lại kiểu long để hết lỗi "lossy conversion" khi compile)
    int countByStationStationId(Integer stationId);

    int countByStationStationIdAndIsDeletedFalse(Integer stationId);

    int countByStationStationIdAndStatusAndIsDeletedFalse(Integer stationId, String status);

    // 5. Query tùy chỉnh cho Manager (Giữ nguyên của ông)
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bike b WHERE b.bikeCode = :bikeCode AND b.station.stationId = :stationId")
    boolean isBikeInStation(@Param("bikeCode") String bikeCode, @Param("stationId") Integer stationId);

    @Query("SELECT b FROM Bike b WHERE b.bikeId = :bikeId AND b.station.stationId = :stationId")
    Optional<Bike> findBikeForManager(@Param("bikeId") Integer bikeId, @Param("stationId") Integer stationId);

    // Đếm tất cả xe chưa bị xóa
    long countByIsDeletedFalse();

    // Đếm xe theo trạng thái chưa bị xóa (Dùng cho AVAILABLE, IN_USE, MAINTENANCE)
    long countByStatusAndIsDeletedFalse(String status);
}