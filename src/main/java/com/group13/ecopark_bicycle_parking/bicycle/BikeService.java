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
    // HÀM MỚI THÊM: DÙNG CHO FRONTEND KHÁCH HÀNG
    // ==========================================
    public List<BikeDTO.Response> getAllVehicles() {
        // Lấy tất cả xe chưa bị xóa (isDeleted = false)
        return bikeRepository.findAll().stream()
                .filter(bike -> !bike.isDeleted()) 
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Lấy ID bãi xe mà Manager được phân công trực
    private Integer getAssignedStationId(Integer managerId) {
        return stationManagerRepository.findByManager_UserId(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản chưa được phân công quản lý bãi xe nào!"))
                .getStation().getStationId();
    }

    // 1. THÊM XE MỚI
    @Transactional
    public BikeDTO.Response addVehicle(BikeDTO.CreateRequest request, Integer managerId) {
        Integer stationId = getAssignedStationId(managerId);

        // Giả sử ông đã thêm method này vào Repository
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

    // 2. CẬP NHẬT THÔNG TIN XE
    @Transactional
    public BikeDTO.Response updateVehicle(Integer vehicleId, BikeDTO.UpdateRequest request, Integer managerId) {
        Integer stationId = getAssignedStationId(managerId);

        Bike bike = bikeRepository.findBikeForManager(vehicleId, stationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe!"));

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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe!"));

        String status = request.getStatus().trim().toUpperCase();
        bike.setStatus(status);
        return toResponse(bikeRepository.save(bike));
    }

    // 4. XÓA XE (Xóa mềm)
    @Transactional
    public void deleteVehicle(Integer vehicleId, Integer managerId) {
        Integer stationId = getAssignedStationId(managerId);

        Bike bike = bikeRepository.findBikeForManager(vehicleId, stationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe!"));

        if ("IN_USE".equals(bike.getStatus())) {
            throw new IllegalStateException("Xe đang được thuê!");
        }

        bike.setDeleted(true);
        bike.setStation(null); 
        bikeRepository.save(bike);
    }

    // Hàm map dữ liệu chuẩn để Frontend không bị báo lỗi undefined
    private BikeDTO.Response toResponse(Bike bike) {
        BikeDTO.Response res = new BikeDTO.Response();
        res.setBikeId(bike.getBikeId());
        res.setBikeCode(bike.getBikeCode());
        res.setStatus(bike.getStatus());
        
        // Trả về tên loại xe để Frontend hiển thị cho đẹp
        if (bike.getCategory() != null) {
            res.setCategoryId(bike.getCategory().getCategoryId());
            // Nếu DTO của ông có trường bikeType, hãy set nó ở đây
            // res.setBikeType(bike.getCategory().getCategoryName()); 
        }
        
        if (bike.getStation() != null) {
            res.setStationId(bike.getStation().getStationId());
        }
        return res;
    }
}