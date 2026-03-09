package com.group13.ecopark_bicycle_parking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller // Báo cho Spring Boot: "Đây là Lễ tân tiếp nhận yêu cầu từ trình duyệt"
public class BicycleController {

    @Autowired // "Phép thuật" tự động tiêm (Inject) người thủ kho vào đây để xài
    private BicycleRepository bicycleRepository;

    // Khi khách hàng truy cập vào link http://localhost:8080/xe-dap, hàm này sẽ chạy
    @GetMapping("/xe-dap")
    public String hienThiDanhSachXe(Model model) {
        
        // 1. Nhờ thủ kho lấy toàn bộ danh sách xe từ Cơ sở dữ liệu MySQL
        List<Bicycle> danhSachXeThucTe = bicycleRepository.findAll();

        // 2. Đóng gói danh sách đó, dán cho nó cái nhãn tên là "danhSachXe"
        model.addAttribute("danhSachXe", danhSachXeThucTe);

        // 3. Trả về tên của file HTML sẽ làm nhiệm vụ hiển thị dữ liệu này
        // (Chúng ta sẽ tạo file "danh-sach-xe.html" ở Bước 4)
        return "danh-sach-xe"; 
    }
    
 // 1. Mở trang Form để nhập thông tin xe mới
    @GetMapping("/xe-dap/them")
    public String hienThiFormThem(Model model) {
        // Tạo một chiếc xe rỗng đưa ra giao diện để người dùng điền vào
        model.addAttribute("xe", new Bicycle()); 
        return "form-xe";
    }

    // 2. Nhận dữ liệu từ Form gửi về và Lưu vào Database (Dùng chung cho cả Thêm và Sửa)
    @PostMapping("/xe-dap/luu")
    public String luuXeDap(@ModelAttribute("xe") Bicycle xe) {
        // Thủ kho gọi hàm save(). Nếu xe có ID rồi thì là cập nhật, chưa có thì là thêm mới.
        bicycleRepository.save(xe); 
        // Lưu xong thì tự động chuyển hướng (redirect) về lại trang danh sách
        return "redirect:/xe-dap"; 
    }

    // 3. Mở trang Form và điền sẵn thông tin của xe cần Sửa
    @GetMapping("/xe-dap/sua/{id}")
    public String hienThiFormSua(@PathVariable("id") Long id, Model model) {
        // Tìm chiếc xe theo ID, nếu không thấy thì trả về null
        Bicycle xeCanSua = bicycleRepository.findById(id).orElse(null);
        model.addAttribute("xe", xeCanSua);
        return "form-xe"; // Dùng chung giao diện với trang Thêm mới
    }

    // 4. Xóa một chiếc xe theo ID
    @GetMapping("/xe-dap/xoa/{id}")
    public String xoaXeDap(@PathVariable("id") Long id) {
        bicycleRepository.deleteById(id);
        return "redirect:/xe-dap";
    }
}
