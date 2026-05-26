package com.group13.ecopark_bicycle_parking.bicycle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BikeCategoryRepository extends JpaRepository<BikeCategory, Integer> {

    // Tìm danh mục loại xe theo tên (VD: "SINGLE", "DOUBLE", "ELECTRIC")
    Optional<BikeCategory> findByName(String name);

    // Giải pháp an toàn: Lấy tất cả danh mục.
    // Khi nào bạn thêm cột `isDeleted` vào BikeCategory.java thì có thể đổi lại thành câu Query lọc cờ sau.
    @Query("SELECT bc FROM BikeCategory bc")
    List<BikeCategory> findAllCategories();
}
