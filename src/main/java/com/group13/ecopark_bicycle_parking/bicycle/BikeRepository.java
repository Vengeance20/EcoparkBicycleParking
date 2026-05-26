package com.group13.ecopark_bicycle_parking.bicycle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BikeRepository extends JpaRepository<Bike, Integer> {
    Optional<Bike> findByBikeCodeAndIsDeletedFalse(String bikeCode);

    List<Bike> findByStationStationIdAndIsDeletedFalse(Integer stationId);

    List<Bike> findByStationStationIdAndStatusAndIsDeletedFalse(Integer stationId, String status);

    long countByStationStationIdAndIsDeletedFalse(Integer stationId);

    long countByStationStationIdAndStatusAndIsDeletedFalse(Integer stationId, String status);
    // Thêm hàm đếm số xe đang đỗ tại bãi
    int countByStationStationId(Integer stationId);

    // Phục vụ API Thêm xe: Kiểm tra mã xe đã tồn tại trong bãi chưa
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bike b WHERE b.bikeCode = :bikeCode AND b.station.stationId = :stationId")
    boolean isBikeInStation(@Param("bikeCode") String bikeCode, @Param("stationId") Integer stationId);

    // Phục vụ API Sửa, Đổi trạng thái, Xóa xe: Bắt buộc xe phải thuộc bãi của Manager
    @Query("SELECT b FROM Bike b WHERE b.bikeId = :bikeId AND b.station.stationId = :stationId")
    Optional<Bike> findBikeForManager(@Param("bikeId") Integer bikeId, @Param("stationId") Integer stationId);
}

