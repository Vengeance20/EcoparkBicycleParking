USE ecopark_bike_1;

-- Tắt kiểm tra khóa ngoại để tránh lỗi khi reset dữ liệu nhiều lần
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE rentals;
TRUNCATE TABLE wallet_transactions;
TRUNCATE TABLE station_managers;
TRUNCATE TABLE bikes;
TRUNCATE TABLE stations;
TRUNCATE TABLE bike_categories;
TRUNCATE TABLE users;
TRUNCATE TABLE system_configs;

-- 1. CẤU HÌNH HỆ THỐNG
INSERT INTO system_configs (config_key, config_value) VALUES 
('OPEN_HOUR', '05:00'),
('CLOSE_HOUR', '23:00');

-- 2. DANH MỤC LOẠI XE
INSERT INTO bike_categories (name, base_fee, extra_fee) VALUES
('SINGLE', 10000.00, 3000.00),
('DOUBLE', 20000.00, 5000.00),
('ELECTRIC', 30000.00, 10000.00);

-- 3. TÀI KHOẢN NGƯỜI DÙNG
-- Mật khẩu cho tất cả tài khoản là: 123456 (Đã băm SHA-256)
INSERT INTO users (username, password_hash, full_name, email, phone_number, national_id, role, wallet_balance, is_resident, is_locked, is_deleted, created_at) VALUES
('admin1', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Giám Đốc Hệ Thống', 'admin@ecopark.com', '0999999999', '001099000001', 'ADMIN', 0.00, false, false, false, NOW()),
('manager1', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Trưởng Trạm Khu A', 'manager@ecopark.com', '0888888888', '001099000002', 'MANAGER', 0.00, false, false, false, NOW()),
('user1', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Cư dân Rừng Cọ', 'cudan@gmail.com', '0123456789', '001099000003', 'USER', 250000.00, true, false, false, NOW()),
('user2', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Khách Tham Quan', 'khach@gmail.com', '0987654321', '001099000004', 'USER', 50000.00, false, false, false, NOW());

-- 4. CÁC TRẠM ĐỖ XE (Tọa độ thực tế khu vực Ecopark)
INSERT INTO stations (name, latitude, longitude, capacity, status, is_deleted) VALUES
('Công viên Hồ Thiên Nga', 20.95750000, 105.93320000, 20, 'ACTIVE', false),
('Khu phố Rừng Cọ', 20.96200000, 105.93600000, 15, 'ACTIVE', false),
('Khu đô thị Aqua Bay', 20.95200000, 105.93000000, 30, 'ACTIVE', false);

-- 5. PHÂN CÔNG QUẢN LÝ TRẠM
-- Gán manager1 quản lý trạm Hồ Thiên Nga (ID=1) và Rừng Cọ (ID=2)
INSERT INTO station_managers (manager_id, station_id) VALUES 
(2, 1),
(2, 2);

-- 6. DỮ LIỆU XE ĐẠP
INSERT INTO bikes (bike_code, category_id, station_id, status, is_deleted) VALUES
('ECO-S-001', 1, 1, 'AVAILABLE', false),
('ECO-S-002', 1, 1, 'AVAILABLE', false),
('ECO-D-001', 2, 1, 'AVAILABLE', false),
('ECO-E-001', 3, 2, 'AVAILABLE', false),
('ECO-E-002', 3, 2, 'AVAILABLE', false),
('ECO-S-003', 1, 3, 'AVAILABLE', false),
('ECO-D-002', 2, 3, 'MAINTENANCE', false),
('ECO-S-004', 1, 3, 'IN_USE', false),
('ECO-E-003', 3, 3, 'RESERVED', false);

-- 7. LỊCH SỬ NẠP TIỀN VÀO VÍ
INSERT INTO wallet_transactions (user_id, rental_id, transaction_type, amount, created_at) VALUES
(3, NULL, 'TOPUP', 250000.00, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, NULL, 'TOPUP', 50000.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 8. LỊCH SỬ CHUYẾN ĐI (Rentals)
-- Chuyến đi đã hoàn thành của Khách tham quan (Không giảm giá)
INSERT INTO rentals (user_id, bike_id, start_station_id, end_station_id, start_time, end_time, rental_fee, penalty_fee, discount, total_fee, status) VALUES
(4, 1, 1, 2, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), 13000.00, 0.00, 0, 13000.00, 'COMPLETED');

-- Chuyến đi đã hoàn thành của Cư dân (Giảm giá 40%)
INSERT INTO rentals (user_id, bike_id, start_station_id, end_station_id, start_time, end_time, rental_fee, penalty_fee, discount, total_fee, status) VALUES
(3, 4, 2, 1, DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), 30000.00, 0.00, 40, 18000.00, 'COMPLETED');

-- Chuyến đi đang chạy (IN_USE)
INSERT INTO rentals (user_id, bike_id, start_station_id, end_station_id, start_time, end_time, rental_fee, penalty_fee, discount, total_fee, status) VALUES
(3, 8, 3, NULL, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NULL, 0.00, 0.00, 40, 0.00, 'ACTIVE');

-- Đơn đang đặt trước giữ xe (RESERVED)
INSERT INTO rentals (user_id, bike_id, start_station_id, end_station_id, reserved_at, start_time, end_time, rental_fee, penalty_fee, discount, total_fee, status) VALUES
(4, 9, 3, NULL, DATE_SUB(NOW(), INTERVAL 5 MINUTE), NULL, NULL, 0.00, 0.00, 0, 0.00, 'RESERVED');

-- 9. GHI NHẬN DOANH THU CHUYẾN ĐI VÀO SỔ CÁI (Wallet Transactions)
-- Lịch sử cho chuyến đi 1
INSERT INTO wallet_transactions (user_id, rental_id, transaction_type, amount, created_at) VALUES (4, 1, 'RENTAL_PAYMENT', -13000.00, DATE_SUB(NOW(), INTERVAL 1 HOUR));
-- Lịch sử cho chuyến đi 2
INSERT INTO wallet_transactions (user_id, rental_id, transaction_type, amount, created_at) VALUES (3, 2, 'RENTAL_PAYMENT', -18000.00, DATE_SUB(NOW(), INTERVAL 2 HOUR));
-- Tạm ứng cho đơn xe đang chạy
INSERT INTO wallet_transactions (user_id, rental_id, transaction_type, amount, created_at) VALUES (3, 3, 'RENTAL_DEPOSIT', -10000.00, DATE_SUB(NOW(), INTERVAL 30 MINUTE));
-- Tạm ứng cho đơn đặt trước
INSERT INTO wallet_transactions (user_id, rental_id, transaction_type, amount, created_at) VALUES (4, 4, 'RENTAL_DEPOSIT', -30000.00, DATE_SUB(NOW(), INTERVAL 5 MINUTE));

-- Bật lại kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 1;