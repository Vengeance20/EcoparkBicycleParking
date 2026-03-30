package com.group13.ecopark_bicycle_parking.bicycle;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Báo cho Spring biết đây là lớp xử lý nghiệp vụ (Đầu bếp)
public class BicycleService {

    // Không dùng @Autowired nữa, đây là cách tiêm (inject) chuẩn nhất hiện nay
    private final BicycleRepository bicycleRepository;

    public BicycleService(BicycleRepository bicycleRepository) {
        this.bicycleRepository = bicycleRepository;
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
    @Transactional // Bảo vệ an toàn dữ liệu khi lưu
    public Bicycle themXeMoi(Bicycle xeMoi) {
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