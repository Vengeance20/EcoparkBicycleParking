package com.group13.ecopark_bicycle_parking.bicycle;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.group13.ecopark_bicycle_parking.station.StationManager;
import com.group13.ecopark_bicycle_parking.station.StationManagerRepository;
import com.group13.ecopark_bicycle_parking.user.User;
import com.group13.ecopark_bicycle_parking.user.UserRepository;

import java.util.List;

@Service // Báo cho Spring biết đây là lớp xử lý nghiệp vụ (Đầu bếp)
public class BicycleService {

    // Không dùng @Autowired nữa, đây là cách tiêm (inject) chuẩn nhất hiện nay
	private final BicycleRepository bicycleRepository;
    private final UserRepository userRepository; // Thêm kho User
    private final StationManagerRepository stationManagerRepository; // Thêm kho Phân công

    // Cập nhật Constructor để tiêm đủ 3 kho
    public BicycleService(BicycleRepository bicycleRepository, 
                          UserRepository userRepository, 
                          StationManagerRepository stationManagerRepository) {
        this.bicycleRepository = bicycleRepository;
        this.userRepository = userRepository;
        this.stationManagerRepository = stationManagerRepository;
    }
    // 1. Lấy danh sách
    public List<Bicycle> layDanhSachXe() {
        return bicycleRepository.findAll();
    }

    // 2. Lấy chi tiết (Nếu không thấy thì ném lỗi ra ngoài)
    public Bicycle layChiTietXe(Long id) {
        return bicycleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với ID: " + id));
    }

    // 3. Thêm xe mới
    @Transactional
    public Bicycle themXeChoManager(Bicycle xeMoi, Long managerId) {
        // 1. Kiểm tra xem ông quản lý này có tồn tại không
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Quản lý với ID: " + managerId));

        // 2. Tìm xem ông này được giao quản lý bãi nào
        StationManager assignment = stationManagerRepository.findByUser_UserId(manager.getUserId())
                .orElseThrow(() -> new RuntimeException("Quản lý này chưa được phân công bãi xe nào!"));

        // 3. Ép chiếc xe mới vào đúng cái bãi đó
        xeMoi.setStation(assignment.getStation());

        // 4. Lưu vào Database
        return bicycleRepository.save(xeMoi);
    }

    // 4. Cập nhật xe
    @Transactional
    public Bicycle capNhatXe(Long id, Bicycle thongTinCapNhat) {
        // Tái sử dụng lại hàm layChiTietXe ở trên (rất tiện lợi!)
        Bicycle xeHienTai = layChiTietXe(id); 

        // Cập nhật thông tin
        xeHienTai.setBikeCode(thongTinCapNhat.getBikeCode());
        xeHienTai.setStatus(thongTinCapNhat.getStatus());
        xeHienTai.setBikeType(thongTinCapNhat.getBikeType());

        // Lưu xuống DB
        return bicycleRepository.save(xeHienTai);
    }

    // 5. Xóa xe
    @Transactional
    public void xoaXe(Long id) {
        if (!bicycleRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy xe để xóa");
        }
        bicycleRepository.deleteById(id);
    }
}