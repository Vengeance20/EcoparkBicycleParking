package com.group13.ecopark_bicycle_parking.bicycle;

import com.group13.ecopark_bicycle_parking.station.Station;
import com.group13.ecopark_bicycle_parking.station.StationManagerRepository;
import com.group13.ecopark_bicycle_parking.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BikeService {

    private final BikeRepository bikeRepository;
    private final StationManagerRepository stationManagerRepository;
    private final BikeCategoryRepository categoryRepository;
    private final StationRepository stationRepository;

    public BikeService(BikeRepository bikeRepository,
                       StationManagerRepository stationManagerRepository,
                       BikeCategoryRepository categoryRepository,
                       StationRepository stationRepository) {
        this.bikeRepository = bikeRepository;
        this.stationManagerRepository = stationManagerRepository;
        this.categoryRepository = categoryRepository;
        this.stationRepository = stationRepository;
    }

    // ==========================================
    // HÀM CHO FRONTEND KHÁCH HÀNG / PUBLIC
    // ==========================================
    public List<BikeDTO.Response> getAllVehicles() {
        return bikeRepository.findAll().stream()
                .filter(bike -> !bike.isDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Integer getAssignedStationId(Integer managerId) {
        return stationManagerRepository.findByManager_UserId(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản chưa được phân công quản lý bãi xe nào!"))
                .getStation().getStationId();
    }

    // ==========================================
    // CÁC HÀM CỦA MANAGER (Giữ nguyên của bạn)
    // ==========================================
    @Transactional
    public BikeDTO.Response addVehicle(BikeDTO.CreateRequest request, Integer managerId) {
        Integer stationId = getAssignedStationId(managerId);

        if (bikeRepository.findByBikeCode(request.getBikeCode().trim()).isPresent()) {
            throw new IllegalStateException("Mã xe đã tồn tại trong hệ thống!");
        }

        BikeCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Loại xe không tồn tại!"));

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Bãi xe không tồn tại!"));

        Bike bike = Bike.builder()
                .bikeCode(request.getBikeCode().trim())
                .status("AVAILABLE")
                .isDeleted(false)
                .category(category)
                .station(station)
                .build();

        return toResponse(bikeRepository.save(bike));
    }

    @Transactional
    public BikeDTO.Response updateVehicleStatus(Integer vehicleId, BikeDTO.StatusUpdateRequest request, Integer managerId) {
        Integer stationId = getAssignedStationId(managerId);
        Bike bike = bikeRepository.findBikeForManager(vehicleId, stationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe!"));
        bike.setStatus(request.getStatus().trim().toUpperCase());
        return toResponse(bikeRepository.save(bike));
    }

    // ==========================================
    // 🔴 CÁC HÀM DÀNH CHO ADMIN (MỚI THÊM)
    // ==========================================

    // Admin thêm xe (không bị giới hạn trạm, tự chọn trạm)
    @Transactional
    public BikeDTO.Response addVehicleByAdmin(BikeDTO.CreateRequest request) {
        if (bikeRepository.findByBikeCode(request.getBikeCode().trim()).isPresent()) {
            throw new IllegalStateException("Mã xe đã tồn tại trong hệ thống!");
        }

        BikeCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Loại xe không tồn tại!"));

        // Admin có thể chọn trạm, hoặc để trống (chưa xếp trạm)
        Station station = null;
        if (request.getStationId() != null) {
            station = stationRepository.findByStationIdAndIsDeletedFalse(request.getStationId())
                    .orElseThrow(() -> new IllegalArgumentException("Bãi xe không tồn tại!"));
        }

        Bike bike = Bike.builder()
                .bikeCode(request.getBikeCode().trim())
                .status("AVAILABLE")
                .isDeleted(false)
                .category(category)
                .station(station)
                .build();

        return toResponse(bikeRepository.save(bike));
    }

    // Admin đổi trạng thái xe bất kỳ
    @Transactional
    public BikeDTO.Response updateVehicleStatusByAdmin(Integer vehicleId, BikeDTO.StatusUpdateRequest request) {
        Bike bike = bikeRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe!"));
        bike.setStatus(request.getStatus().trim().toUpperCase());
        return toResponse(bikeRepository.save(bike));
    }

    // 🔴 CẬP NHẬT HÀM MAP DỮ LIỆU: Thêm Tên Loại Xe và Tên Trạm
    private BikeDTO.Response toResponse(Bike bike) {
        BikeDTO.Response res = new BikeDTO.Response();
        res.setBikeId(bike.getBikeId());
        res.setBikeCode(bike.getBikeCode());
        res.setStatus(bike.getStatus());

        if (bike.getCategory() != null) {
            res.setCategoryId(bike.getCategory().getCategoryId());
            res.setCategoryName(bike.getCategory().getName()); // Map thêm tên loại xe
        }

        if (bike.getStation() != null) {
            res.setStationId(bike.getStation().getStationId());
            res.setStationName(bike.getStation().getName()); // Map thêm tên trạm
        }
        return res;
    }
}