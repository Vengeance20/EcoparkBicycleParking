package com.group13.ecopark_bicycle_parking.bicycle;

import com.group13.ecopark_bicycle_parking.station.Station;
import com.group13.ecopark_bicycle_parking.station.StationManagerRepository;
import com.group13.ecopark_bicycle_parking.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Lấy ID bãi xe mà Manager được phân công trực
    private Integer getAssignedStationId(Integer managerId) {
        return stationManagerRepository.findByManager_UserId(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản chưa được phân công quản lý bãi xe nào!"))
                .getStation().getStationId();
    }

    // ADMIN: Lấy toàn bộ danh sách xe
    @Transactional(readOnly = true)
    public java.util.List<BikeDTO.Response> getAllVehicles() {
        return bikeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    // 1. THÊM XE MỚI
    @Transactional
    public BikeDTO.Response addVehicle(BikeDTO.CreateRequest request, Integer managerId) {
        Integer stationId = getAssignedStationId(managerId);

        if (bikeRepository.isBikeInStation(request.getBikeCode().trim(), stationId)) {
            throw new IllegalStateException("Mã xe hoặc mã định danh đã tồn tại trong trạm của bạn!");
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

    // 2. CẬP NHẬT THÔNG TIN XE
    @Transactional
    public BikeDTO.Response updateVehicle(Integer vehicleId, BikeDTO.UpdateRequest request, Integer managerId) {
        Integer stationId = getAssignedStationId(managerId);

        Bike bike = bikeRepository.findBikeForManager(vehicleId, stationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe hoặc xe nằm ngoài phạm vi quản lý!"));

        BikeCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Loại xe không tồn tại!"));

        bike.setBikeCode(request.getBikeCode().trim());
        bike.setCategory(category);

        return toResponse(bikeRepository.save(bike));
    }

    // 3. CẬP NHẬT TRẠNG THÁI XE (Báo hỏng / Bảo trì)
    @Transactional
    public BikeDTO.Response updateVehicleStatus(Integer vehicleId, BikeDTO.StatusUpdateRequest request, Integer managerId) {
        Integer stationId = getAssignedStationId(managerId);

        Bike bike = bikeRepository.findBikeForManager(vehicleId, stationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe hoặc xe nằm ngoài phạm vi quản lý!"));

        String status = request.getStatus().trim().toUpperCase();
        if (!status.equals("AVAILABLE") && !status.equals("MAINTENANCE") && !status.equals("INACTIVE")) {
            throw new IllegalArgumentException("Trạng thái xe không hợp lệ!");
        }

        bike.setStatus(status);
        return toResponse(bikeRepository.save(bike));
    }

    // 4. XÓA XE (Xóa mềm)
    @Transactional
    public void deleteVehicle(Integer vehicleId, Integer managerId) {
        Integer stationId = getAssignedStationId(managerId);

        Bike bike = bikeRepository.findBikeForManager(vehicleId, stationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe hoặc xe nằm ngoài phạm vi quản lý!"));

        if ("IN_USE".equals(bike.getStatus())) {
            throw new IllegalStateException("Không thể xóa xe đang trong trạng thái được thuê di chuyển!");
        }

        bike.setDeleted(true);
        bike.setStation(null); // Giải phóng xe khỏi bãi
        bikeRepository.save(bike);
    }

    // Hàm chuyển đổi thủ công để tránh lỗi của Lombok @Builder
    private BikeDTO.Response toResponse(Bike bike) {
        BikeDTO.Response res = new BikeDTO.Response();
        res.setBikeId(bike.getBikeId());
        res.setBikeCode(bike.getBikeCode());
        res.setStatus(bike.getStatus());
        res.setStationId(bike.getStation() != null ? bike.getStation().getStationId() : null);
        res.setCategoryId(bike.getCategory() != null ? bike.getCategory().getCategoryId() : null);
        return res;
    }
}