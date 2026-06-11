package com.group13.ecopark_bicycle_parking.station;

import com.group13.ecopark_bicycle_parking.user.User;
import com.group13.ecopark_bicycle_parking.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StationMonitorService {

    private final StationManagerRepository stationManagerRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository; // 🔴 THÊM DÒNG NÀY

    public StationMonitorService(
            StationManagerRepository stationManagerRepository,
            UserRepository userRepository,
            StationRepository stationRepository // 🔴 THÊM VÀO CONSTRUCTOR
    ) {
        this.stationManagerRepository = stationManagerRepository;
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public List<StationMonitorDTO> getMonitorStation(Integer managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        if (manager.getRole() == null || !"MANAGER".equalsIgnoreCase(manager.getRole())) {
            throw new IllegalArgumentException("User is not a manager");
        }

        return stationManagerRepository.findMonitorDataByManager(managerId);
    }

    // ==================== CÁC HÀM DÀNH CHO ADMIN ====================

    // 🔴 THÊM HÀM NÀY: Lấy tất cả trạm cho Admin
    @Transactional(readOnly = true)
    public List<StationMonitorDTO> getAllStationsForAdmin() {
        return stationRepository.findAllStationMonitorDataForAdmin();
    }

    // 🔴 THÊM HÀM NÀY: Bảo trì trạm
    @Transactional
    public void setStationMaintenance(Integer stationId) {
        Station station = stationRepository.findByStationIdAndIsDeletedFalse(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạm"));
        station.setStatus("MAINTENANCE");
        stationRepository.save(station);
    }

    // 🔴 THÊM HÀM NÀY: Gỡ bảo trì trạm
    @Transactional
    public void setStationActive(Integer stationId) {
        Station station = stationRepository.findByStationIdAndIsDeletedFalse(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạm"));
        station.setStatus("ACTIVE");
        stationRepository.save(station);
    }

    // 🔴 THÊM HÀM NÀY: Thêm trạm mới vào Database
    @Transactional
    public Station addStation(Station station) {
        // Kiểm tra xem tên trạm đã tồn tại chưa
        if (stationRepository.findByName(station.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên trạm đã tồn tại!");
        }
        // Thiết lập mặc định khi tạo mới
        station.setStatus("ACTIVE");
        station.setDeleted(false);

        return stationRepository.save(station);
    }
}