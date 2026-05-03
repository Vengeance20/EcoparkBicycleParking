package com.group13.ecopark_bicycle_parking.rental;

import com.group13.ecopark_bicycle_parking.bicycle.BikeRepository;
import com.group13.ecopark_bicycle_parking.bicycle.Bike;
import com.group13.ecopark_bicycle_parking.user.UserRepository;
import com.group13.ecopark_bicycle_parking.user.User;
import com.group13.ecopark_bicycle_parking.user.WalletTransaction;
import com.group13.ecopark_bicycle_parking.user.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class RentalScheduler {

    @Autowired private RentalRepository rentalRepository;
    @Autowired private BikeRepository bikeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private WalletTransactionRepository walletTxRepo;

    // Chạy ngầm tự động mỗi 1 phút (60000 milliseconds)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredReservations() {
        // Tìm mốc thời gian cách đây 15 phút
        LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(1);

        // Lấy tất cả các đơn đặt xe (RESERVED) trước mốc 15 phút đó
        List<Rental> expiredRentals = rentalRepository.findByStatusAndReservedAtBefore("RESERVED", timeLimit);

        for (Rental rental : expiredRentals) {
            User user = rental.getUser();
            Bike bike = rental.getBike();
            BigDecimal deposit = bike.getCategory().getBaseFee();

            // Phạt 10 điểm bom hàng
            BigDecimal penalty = new BigDecimal("10.00");
            BigDecimal refund = deposit.subtract(penalty); // Số tiền hoàn lại

            // 1. Hủy hóa đơn, ghi nhận tiền phạt
            rental.setStatus("CANCELLED");
            rental.setPenaltyFee(penalty);
            rental.setTotalFee(penalty);
            rentalRepository.save(rental);

            // 2. Nhả xe về lại bãi để người khác thuê
            bike.setStatus("AVAILABLE");
            bikeRepository.save(bike);

            // 3. Hoàn lại tiền cọc (đã trừ phạt) vào ví
            user.setWalletBalance(user.getWalletBalance().add(refund));
            userRepository.save(user);

            // 4. Lưu biên lai Kế toán cho khoản hoàn cọc
            WalletTransaction tx = WalletTransaction.builder()
                    .user(user).rental(rental)
                    .transactionType("REFUND_AFTER_PENALTY")
                    .amount(refund) // Tiền dương (Cộng vào)
                    .build();
            walletTxRepo.save(tx);

            System.out.println("Đã hủy đơn " + rental.getRentalId() + " do quá hạn 15 phút. Khách bị phạt 10 điểm.");
        }
    }
}