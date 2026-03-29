package com.group13.ecopark_bicycle_parking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController // Đổi thành RestController: Báo cho Spring Boot biết đây là trạm phát dữ liệu JSON
@RequestMapping("/api/v1/bicycles") // Đặt tên miền API chuẩn quốc tế cho toàn bộ class này
@CrossOrigin(origins = "*") // Mở cửa cho Frontend (React/Vue/JS) gọi vào mà không bị lỗi CORS
public class BicycleController {

    @Autowired
    private BicycleRepository bicycleRepository;

    // 1. LẤY DANH SÁCH XE (Frontend gọi GET /api/v1/bicycles)
    @GetMapping
    public ResponseEntity<List<Bicycle>> layDanhSachXe() {
        List<Bicycle> danhSachXe = bicycleRepository.findAll();
        // Trả về thẳng cục dữ liệu và mã 200 (OK)
        return ResponseEntity.ok(danhSachXe); 
    }

    // 2. LẤY CHI TIẾT 1 CHIẾC XE (Để Frontend nhét vào Form sửa)
    @GetMapping("/{id}")
    public ResponseEntity<Bicycle> layChiTietXe(@PathVariable Long id) {
        return bicycleRepository.findById(id)
                .map(xe -> ResponseEntity.ok(xe)) // Nếu tìm thấy, trả về dữ liệu xe
                .orElse(ResponseEntity.notFound().build()); // Nếu không thấy, báo lỗi 404
    }

    // 3. THÊM XE MỚI (Frontend gọi POST và gửi cục JSON lên)
    @PostMapping
    public ResponseEntity<Bicycle> themXeMoi(@Valid @RequestBody Bicycle xeMoi) {
        // @RequestBody sẽ tự động dịch chuỗi JSON của Frontend thành Object Bicycle
        Bicycle xeDaLuu = bicycleRepository.save(xeMoi);
        // Trả về chiếc xe vừa tạo kèm mã 201 (Created)
        return new ResponseEntity<>(xeDaLuu, HttpStatus.CREATED); 
    }

    // 4. CẬP NHẬT XE ĐÃ CÓ (Frontend gọi PUT và gửi cục JSON lên)
    @PutMapping("/{id}")
    public ResponseEntity<Bicycle> capNhatXe(@PathVariable Long id, @Valid @RequestBody Bicycle thongTinCapNhat) {
        return bicycleRepository.findById(id)
                .map(xeHienTai -> {
                    // Cập nhật các trường dữ liệu
                    xeHienTai.setBikeCode(thongTinCapNhat.getBikeCode());
                    xeHienTai.setStatus(thongTinCapNhat.getStatus());
                    xeHienTai.setBikeType(thongTinCapNhat.getBikeType());
                    // Lưu lại xuống Database
                    Bicycle xeDaCapNhat = bicycleRepository.save(xeHienTai);
                    return ResponseEntity.ok(xeDaCapNhat);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. XÓA XE (Frontend gọi DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaXe(@PathVariable Long id) {
        if (bicycleRepository.existsById(id)) {
            bicycleRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // Mã 204: Xóa thành công, không có nội dung gì cần trả về
        }
        return ResponseEntity.notFound().build(); // Báo lỗi 404 nếu không tìm thấy xe để xóa
    }
}