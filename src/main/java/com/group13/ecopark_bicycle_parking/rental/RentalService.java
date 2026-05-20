package com.group13.ecopark_bicycle_parking.rental;

import com.group13.ecopark_bicycle_parking.bicycle.Bike;
import com.group13.ecopark_bicycle_parking.bicycle.BikeCategory;
import com.group13.ecopark_bicycle_parking.bicycle.BikeRepository;
import com.group13.ecopark_bicycle_parking.station.Station;
import com.group13.ecopark_bicycle_parking.station.StationRepository;
import com.group13.ecopark_bicycle_parking.user.User;
import com.group13.ecopark_bicycle_parking.user.UserRepository;
import com.group13.ecopark_bicycle_parking.user.WalletTransaction;
import com.group13.ecopark_bicycle_parking.user.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.time.temporal.ChronoUnit;

@Service
public class RentalService {

    @Autowired private UserRepository userRepository;
    @Autowired private BikeRepository bikeRepository;
    @Autowired private RentalRepository rentalRepository;
    @Autowired private WalletTransactionRepository walletTransactionRepository;
    @Autowired private StationRepository stationRepository;

    @Transactional // Đảm bảo All-or-Nothing (Thành công tất cả hoặc Rollback toàn bộ)
    public RentalDTO.RentResponse rentBike(RentalDTO.RentRequest request) {

        // 1. Xác thực người dùng
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng."));

        // 2. Xác thực xe (quét bằng mã QR)
        Bike bike = bikeRepository.findByBikeCodeAndIsDeletedFalse(request.getBikeCode())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy xe hoặc xe đã bị xóa."));

        // 3. Kiểm tra trạng thái xe có sẵn sàng không
        if (!"AVAILABLE".equals(bike.getStatus())) {
            throw new RuntimeException("Lỗi: Xe này đang được bảo trì hoặc đã có người thuê.");
        }

        // 4. Kiểm tra số dư ví (Tạm ứng trước một khoản bằng Base Fee của xe)
        BigDecimal depositAmount = bike.getCategory().getBaseFee();
        if (user.getWalletBalance().compareTo(depositAmount) < 0) {
            throw new RuntimeException("Lỗi: Số dư ví không đủ. Cần tối thiểu " + depositAmount + " điểm để tạm ứng.");
        }

        // 5. Tạo chuyến đi (Rental) lưu vào DB
        Rental rental = Rental.builder()
                .user(user)
                .bike(bike)
                .startStation(bike.getStation()) // Lấy bãi hiện tại của xe làm bãi xuất phát
                .startTime(LocalDateTime.now())
                .status("ACTIVE")
                .penaltyFee(BigDecimal.ZERO)
                .discount(0) // Mặc định 0, nếu là cư dân có thể set 40 ở đây
                .build();
        rental = rentalRepository.save(rental);

        // 6. Trừ tiền cọc trong ví User
        user.setWalletBalance(user.getWalletBalance().subtract(depositAmount));
        userRepository.save(user);

        // 7. Ghi Log vào Sổ cái Kế toán (WalletTransaction)
        WalletTransaction transaction = WalletTransaction.builder()
                .user(user)
                .rental(rental) // Liên kết giao dịch này với chuyến đi vừa tạo
                .transactionType("RENTAL_DEPOSIT")
                .amount(depositAmount.negate()) // Số tiền âm (bị trừ)
                .build();
        walletTransactionRepository.save(transaction);

        // 8. Cập nhật lại chiếc xe
        bike.setStatus("IN_USE");
        bike.setStation(null); // Rời bãi nên station_id thành NULL
        bikeRepository.save(bike);

        // 9. Trả kết quả về cho Frontend
        return new RentalDTO.RentResponse(
                rental.getRentalId(),
                bike.getBikeCode(),
                rental.getStartTime(),
                "Mở khóa xe thành công. Chúc bạn có một chuyến đi vui vẻ!"
        );
    }

    @Transactional
    public RentalDTO.RentResponse reserveBike(Integer userId, String bikeCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Người dùng không tồn tại."));

        Bike bike = bikeRepository.findByBikeCodeAndIsDeletedFalse(bikeCode)
                .orElseThrow(() -> new RuntimeException("Lỗi: Xe không tồn tại."));

        if (!"AVAILABLE".equals(bike.getStatus())) {
            throw new RuntimeException("Lỗi: Xe này không sẵn sàng để đặt.");
        }

        BigDecimal depositAmount = bike.getCategory().getBaseFee();
        if (user.getWalletBalance().compareTo(depositAmount) < 0) {
            throw new RuntimeException("Lỗi: Số dư không đủ để cọc " + depositAmount + " điểm.");
        }

        // Trừ cọc
        user.setWalletBalance(user.getWalletBalance().subtract(depositAmount));
        userRepository.save(user);

        // Lưu giao dịch trừ cọc
        WalletTransaction tx = WalletTransaction.builder()
                .user(user).amount(depositAmount.negate())
                .transactionType("RENTAL_DEPOSIT").build();
        walletTransactionRepository.save(tx);

        // Tạo hóa đơn CHỜ (RESERVED)
        Rental rental = Rental.builder()
                .user(user).bike(bike).startStation(bike.getStation())
                .reservedAt(LocalDateTime.now()) // Ghi nhận giờ đặt
                .status("RESERVED")              // Trạng thái là ĐÃ ĐẶT
                .penaltyFee(BigDecimal.ZERO).discount(0).build();
        rental = rentalRepository.save(rental);

        // Khóa xe lại không cho người khác thuê
        bike.setStatus("RESERVED");
        bikeRepository.save(bike);

        // Cập nhật rentalId vào giao dịch
        tx.setRental(rental);
        walletTransactionRepository.save(tx);

        return new RentalDTO.RentResponse(rental.getRentalId(), bike.getBikeCode(), null,
                "Đặt xe thành công. Bạn có 15 phút để đến bãi nhận xe!");
    }

    @Transactional
    public String unlockBike(Integer userId, String bikeCode) {
        // Tìm chuyến đi đang ở trạng thái RESERVED của user và chiếc xe này
        Rental rental = rentalRepository.findByUserUserIdAndBikeBikeCodeAndStatus(userId, bikeCode, "RESERVED")
                .orElseThrow(() -> new RuntimeException("Lỗi: Bạn chưa đặt trước chiếc xe này hoặc đơn đã hết hạn."));

        Bike bike = rental.getBike();

        // Chuyển hóa đơn sang trạng thái ĐANG CHẠY (ACTIVE)
        rental.setStartTime(LocalDateTime.now());
        rental.setStatus("ACTIVE");
        rentalRepository.save(rental);

        // Mở khóa xe, cho xe xuất bến
        bike.setStatus("IN_USE");
        bike.setStation(null);
        bikeRepository.save(bike);

        return "Mở khóa thành công. Bắt đầu tính tiền thuê từ lúc này!";
    }

    @Transactional(readOnly = true)
    public List<RentalDTO.RentalHistoryResponse> getRentalHistory(String userKey) {
        User user = findUserForHistory(userKey);

        return rentalRepository.findAllByUserUserIdOrderByRentalIdDesc(user.getUserId())
                .stream()
                .map(this::toRentalHistoryResponse)
                .toList();
    }

    private User findUserForHistory(String userKey) {
        if (userKey == null || userKey.trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Không tìm thấy người dùng.");
        }

        String trimmedUserKey = userKey.trim();
        if (trimmedUserKey.matches("\\d+")) {
            try {
                return userRepository.findById(Integer.parseInt(trimmedUserKey))
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng."));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Lỗi: Không tìm thấy người dùng.");
            }
        }

        return userRepository.findByUsername(trimmedUserKey)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng."));
    }

    private RentalDTO.RentalHistoryResponse toRentalHistoryResponse(Rental rental) {
        return new RentalDTO.RentalHistoryResponse(
                rental.getRentalId(),
                rental.getBike().getBikeCode(),
                rental.getStartStation() == null ? null : rental.getStartStation().getName(),
                rental.getEndStation() == null ? null : rental.getEndStation().getName(),
                rental.getStartTime(),
                rental.getEndTime(),
                rental.getRentalFee(),
                rental.getPenaltyFee(),
                rental.getDiscount(),
                rental.getTotalFee(),
                rental.getStatus()
        );
    }
}
    @Transactional
    public RentalDTO.ReturnResponse returnBike(RentalDTO.ReturnRequest request) {

        // 1. Lấy thông tin chuyến đi
        Rental rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy hóa đơn chuyến đi."));

        if (!"ACTIVE".equals(rental.getStatus())) {
            throw new RuntimeException("Lỗi: Chuyến đi này chưa bắt đầu hoặc đã kết thúc.");
        }

        // 2. Lấy thông tin bãi trả xe và KIỂM TRA SỨC CHỨA (Capacity Check)
        Station endStation = stationRepository.findById(request.getEndStationId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy bãi trả xe."));

        int currentBikesInStation = bikeRepository.countByStationStationId(endStation.getStationId());
        if (currentBikesInStation >= endStation.getCapacity()) {
            throw new RuntimeException("Lỗi: Bãi đỗ '" + endStation.getName() + "' đã đạt sức chứa tối đa (" + endStation.getCapacity() + " xe). Vui lòng tìm bãi đỗ khác.");
        }

        // 3. Chốt thời gian và tính tiền
        LocalDateTime endTime = LocalDateTime.now();
        rental.setEndTime(endTime);

        long durationMinutes = ChronoUnit.MINUTES.between(rental.getStartTime(), endTime);
        if (durationMinutes < 1) durationMinutes = 1;

        BikeCategory category = rental.getBike().getCategory();
        BigDecimal baseFee = category.getBaseFee();
        BigDecimal extraFee = category.getExtraFee();
        BigDecimal calculatedFee = baseFee;

        // Tính phí phụ trội nếu đi quá 60 phút
        if (durationMinutes > 60) {
            long extraMinutes = durationMinutes - 60;
            long extraBlocks = (long) Math.ceil(extraMinutes / 15.0);
            calculatedFee = baseFee.add(extraFee.multiply(new BigDecimal(extraBlocks)));
        }

        // 4. Áp dụng giảm giá an toàn (Scale 2, RoundingMode HALF_UP cho DECIMAL(10,2))
        BigDecimal discountRate = BigDecimal.valueOf(100 - rental.getDiscount())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalFee = calculatedFee.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);

        rental.setRentalFee(calculatedFee);
        rental.setTotalFee(totalFee);
        rental.setEndStation(endStation);
        rental.setStatus("COMPLETED");

        // 5. Cơ chế quyết toán Ví Điểm (Deposit Refund Mechanism)
        User user = rental.getUser();
        BigDecimal difference = baseFee.subtract(totalFee).setScale(2, RoundingMode.HALF_UP);

        String txType = null;
        BigDecimal refundAmount = BigDecimal.ZERO;

        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            // Cọc dư -> Hoàn lại tiền thừa
            txType = "REFUND_SURPLUS";
            refundAmount = difference;
            user.setWalletBalance(user.getWalletBalance().add(refundAmount));
        } else if (difference.compareTo(BigDecimal.ZERO) < 0) {
            // Cọc thiếu -> Trừ thêm tiền phạt trễ giờ (Difference đang là số âm)
            txType = "RENTAL_EXTRA_FEE";
            user.setWalletBalance(user.getWalletBalance().add(difference));
        }

        userRepository.save(user);

        // Ghi sổ cái giao dịch nếu có biến động tiền
        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            WalletTransaction tx = WalletTransaction.builder()
                    .user(user).rental(rental)
                    .transactionType(txType)
                    .amount(difference)
                    .build();
            walletTransactionRepository.save(tx);
        }

        // 6. Cất xe vào bãi
        Bike bike = rental.getBike();
        bike.setStatus("AVAILABLE");
        bike.setStation(endStation);
        bikeRepository.save(bike);

        rentalRepository.save(rental);

        return new RentalDTO.ReturnResponse(
                rental.getRentalId(),
                durationMinutes,
                totalFee,
                refundAmount,
                "Trả xe thành công! Cảm ơn bạn đã sử dụng Ecopark Bikes."
        );
    }
}
