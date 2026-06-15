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
import java.util.Arrays;
import java.util.List;
import java.time.temporal.ChronoUnit;

@Service
public class RentalService {

    @Autowired private UserRepository userRepository;
    @Autowired private BikeRepository bikeRepository;
    @Autowired private RentalRepository rentalRepository;
    @Autowired private WalletTransactionRepository walletTransactionRepository;
    @Autowired private StationRepository stationRepository;

    @Transactional
    public RentalDTO.RentResponse rentBike(RentalDTO.RentRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng."));

        Bike bike = bikeRepository.findByBikeCodeAndIsDeletedFalse(request.getBikeCode())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy xe hoặc xe đã bị xóa."));

        if (!"AVAILABLE".equals(bike.getStatus())) {
            throw new RuntimeException("Lỗi: Xe này đang được bảo trì hoặc đã có người thuê.");
        }

        BigDecimal depositAmount = bike.getCategory().getBaseFee();
        if (user.getWalletBalance().compareTo(depositAmount) < 0) {
            throw new RuntimeException("Lỗi: Số dư ví không đủ. Cần tối thiểu " + depositAmount + " điểm để tạm ứng.");
        }

        // 🔴 KIỂM TRA CƯ DÂN: Nếu tài khoản là cư dân thì áp dụng giảm 40%, ngược lại 0%
        int discountPercent = user.isResident() ? 40 : 0;

        Rental rental = Rental.builder()
                .user(user)
                .bike(bike)
                .startStation(bike.getStation())
                .startTime(LocalDateTime.now())
                .status("ACTIVE")
                .penaltyFee(BigDecimal.ZERO)
                .discount(discountPercent) // 🔴 Đã chuyển giá trị giảm giá vào Entity để lưu xuống MySQL
                .build();
        rental = rentalRepository.save(rental);

        user.setWalletBalance(user.getWalletBalance().subtract(depositAmount));
        userRepository.save(user);

        WalletTransaction transaction = WalletTransaction.builder()
                .user(user)
                .rental(rental)
                .transactionType("RENTAL_DEPOSIT")
                .amount(depositAmount.negate())
                .build();
        walletTransactionRepository.save(transaction);

        bike.setStatus("IN_USE");
        bike.setStation(null);
        bikeRepository.save(bike);

        return new RentalDTO.RentResponse(
                rental.getRentalId(),
                bike.getBikeCode(),
                rental.getStartTime(),
                "Mở khóa xe thành công. Chúc bạn có một chuyến đi vui vẻ!",
                "ACTIVE"
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

        user.setWalletBalance(user.getWalletBalance().subtract(depositAmount));
        userRepository.save(user);

        WalletTransaction tx = WalletTransaction.builder()
                .user(user).amount(depositAmount.negate())
                .transactionType("RENTAL_DEPOSIT").build();
        walletTransactionRepository.save(tx);

        // 🔴 KIỂM TRA CƯ DÂN: Áp dụng lưu mã giảm giá ngay từ luồng đặt xe trước
        int discountPercent = user.isResident() ? 40 : 0;

        Rental rental = Rental.builder()
                .user(user).bike(bike).startStation(bike.getStation())
                .reservedAt(LocalDateTime.now())
                .status("RESERVED")
                .penaltyFee(BigDecimal.ZERO)
                .discount(discountPercent) // 🔴 Đã chuyển giá trị giảm giá vào Entity để lưu xuống MySQL
                .build();
        rental = rentalRepository.save(rental);

        bike.setStatus("RESERVED");
        bikeRepository.save(bike);

        tx.setRental(rental);
        walletTransactionRepository.save(tx);

        return new RentalDTO.RentResponse(rental.getRentalId(), bike.getBikeCode(), null,
                "Đặt xe thành công. Bạn có 15 phút để đến bãi nhận xe!",
                "RESERVED"
        );
    }

    @Transactional
    public String unlockBike(Integer userId, String bikeCode) {
        List<String> statuses = Arrays.asList("RESERVED");
        List<Rental> rentals = rentalRepository.findAllByUserUserIdAndStatusIn(userId, statuses);

        Rental rental = rentals.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lỗi: Bạn không có xe nào đang ở trạng thái đặt trước."));

        if (rental.getBike() == null || !rental.getBike().getBikeCode().equals(bikeCode)) {
            throw new RuntimeException("Lỗi: Bạn đặt trước xe khác, không phải xe này!");
        }

        Bike bike = rental.getBike();

        rental.setStartTime(LocalDateTime.now());
        rental.setStatus("ACTIVE");
        rentalRepository.save(rental);

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

    @Transactional
    public RentalDTO.ReturnResponse returnBike(RentalDTO.ReturnRequest request) {

        Rental rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy hóa đơn chuyến đi."));

        if (!"ACTIVE".equals(rental.getStatus())) {
            throw new RuntimeException("Lỗi: Chuyến đi này chưa bắt đầu hoặc đã kết thúc.");
        }

        Station endStation = stationRepository.findById(request.getEndStationId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy bãi trả xe."));

        int currentBikesInStation = bikeRepository.countByStationStationId(endStation.getStationId());
        if (currentBikesInStation >= endStation.getCapacity()) {
            throw new RuntimeException("Lỗi: Bãi đỗ '" + endStation.getName() + "' đã đạt sức chứa tối đa (" + endStation.getCapacity() + " xe). Vui lòng tìm bãi đỗ khác.");
        }

        LocalDateTime endTime = LocalDateTime.now();
        rental.setEndTime(endTime);

        long durationMinutes = ChronoUnit.MINUTES.between(rental.getStartTime(), endTime);
        if (durationMinutes < 1) durationMinutes = 1;

        BikeCategory category = rental.getBike().getCategory();
        BigDecimal baseFee = category.getBaseFee();
        BigDecimal extraFee = category.getExtraFee();
        BigDecimal calculatedFee = baseFee;

        if (durationMinutes > 60) {
            long extraMinutes = durationMinutes - 60;
            long extraBlocks = (long) Math.ceil(extraMinutes / 15.0);
            calculatedFee = baseFee.add(extraFee.multiply(new BigDecimal(extraBlocks)));
        }

        BigDecimal discountRate = BigDecimal.valueOf(100 - rental.getDiscount())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalFee = calculatedFee.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);

        rental.setRentalFee(calculatedFee);
        rental.setTotalFee(totalFee);
        rental.setEndStation(endStation);
        rental.setStatus("COMPLETED");

        User user = rental.getUser();
        BigDecimal difference = baseFee.subtract(totalFee).setScale(2, RoundingMode.HALF_UP);

        String txType = null;
        BigDecimal refundAmount = BigDecimal.ZERO;

        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            txType = "REFUND_SURPLUS";
            refundAmount = difference;
            user.setWalletBalance(user.getWalletBalance().add(refundAmount));
        } else if (difference.compareTo(BigDecimal.ZERO) < 0) {
            txType = "RENTAL_EXTRA_FEE";
            user.setWalletBalance(user.getWalletBalance().add(difference));
        }

        userRepository.save(user);

        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            WalletTransaction tx = WalletTransaction.builder()
                    .user(user).rental(rental)
                    .transactionType(txType)
                    .amount(difference)
                    .build();
            walletTransactionRepository.save(tx);
        }

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

    @Transactional(readOnly = true)
    public RentalDTO.RentResponse getMyActiveRental(Integer userId) {
        List<String> activeStatuses = Arrays.asList("ACTIVE", "RESERVED");
        List<Rental> rentals = rentalRepository.findAllByUserUserIdAndStatusIn(userId, activeStatuses);

        Rental rental = rentals.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("NO_ACTIVE_RENTAL"));

        return new RentalDTO.RentResponse(
                rental.getRentalId(),
                rental.getBike().getBikeCode(),
                rental.getStartTime(),
                "Đang có chuyến đi.",
                rental.getStatus()
        );
    }
}