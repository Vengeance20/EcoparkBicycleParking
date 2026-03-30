package com.group13.ecopark_bicycle_parking.bicycle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bicycles")
@CrossOrigin(origins = "*")
public class BicycleController {

    // Gọi Bếp trưởng ra làm việc
    private final BicycleService bicycleService;

    public BicycleController(BicycleService bicycleService) {
        this.bicycleService = bicycleService;
    }

    // 1. LẤY DANH SÁCH
    @GetMapping
    public ResponseEntity<List<Bicycle>> layDanhSachXe() {
        // Nhờ Bếp lấy hộ danh sách rồi bưng ra cho khách (Mã 200 OK)
        return ResponseEntity.ok(bicycleService.layDanhSachXe()); 
    }

    // 2. LẤY CHI TIẾT 1 CHIẾC
    @GetMapping("/{id}")
    public ResponseEntity<Bicycle> layChiTietXe(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bicycleService.layChiTietXe(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Báo lỗi 404 nếu Bếp kêu không thấy
        }
    }

    // 3. THÊM MỚI
    @PostMapping
    public ResponseEntity<Bicycle> themXeMoi(@Valid @RequestBody Bicycle xeMoi) {
        Bicycle xeDaLuu = bicycleService.themXeMoi(xeMoi);
        return new ResponseEntity<>(xeDaLuu, HttpStatus.CREATED); 
    }

    // 4. CẬP NHẬT
    @PutMapping("/{id}")
    public ResponseEntity<Bicycle> capNhatXe(@PathVariable Long id, @Valid @RequestBody Bicycle thongTinCapNhat) {
        try {
            Bicycle xeDaCapNhat = bicycleService.capNhatXe(id, thongTinCapNhat);
            return ResponseEntity.ok(xeDaCapNhat);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Lỗi 404
        }
    }

    // 5. XÓA
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaXe(@PathVariable Long id) {
        try {
            bicycleService.xoaXe(id);
            return ResponseEntity.noContent().build(); // Mã 204
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Lỗi 404
        }
    }
}