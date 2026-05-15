package com.group13.ecopark_bicycle_parking.payment;

import com.group13.ecopark_bicycle_parking.user.User;
import com.group13.ecopark_bicycle_parking.user.UserRepository;
import com.group13.ecopark_bicycle_parking.user.WalletTransaction;
import com.group13.ecopark_bicycle_parking.user.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PaymentService {

    @Autowired private UserRepository userRepository;
    @Autowired private WalletTransactionRepository walletTransactionRepository;

    @Transactional
    public PaymentDTO.TopupResponse processTopup(Integer userId, BigDecimal amountVnd) {

        // 1. Xác thực người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng."));

        if (amountVnd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Lỗi: Số tiền nạp phải lớn hơn 0.");
        }

        // 2. [callThirdPartyPaymentGateway] - Gọi Cổng thanh toán (VNPay / MoMo)
        boolean isPaymentSuccess = callThirdPartyPaymentGateway(amountVnd);
        if (!isPaymentSuccess) {
            throw new RuntimeException("Lỗi: Giao dịch bị từ chối bởi cổng thanh toán.");
        }

        // 3. Quy đổi tiền thật (VNĐ) sang Điểm hệ thống (Tỉ lệ 1000 VNĐ = 1 Điểm)
        BigDecimal pointsToAdd = amountVnd.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);

        // 4. [updateWallet] - Cập nhật số dư ví (Cộng tiền)
        user.setWalletBalance(user.getWalletBalance().add(pointsToAdd));
        userRepository.save(user);

        // 5. Ghi nhận Sổ cái Kiểm toán vào bảng wallet_transactions
        WalletTransaction tx = WalletTransaction.builder()
                .user(user)
                .rental(null) // Nạp tiền không gắn với chuyến đi nào
                .transactionType("TOPUP")
                .amount(pointsToAdd) // Số tiền dương (Cộng vào ví)
                .build();
        tx = walletTransactionRepository.save(tx);

        // 6. Trả biên lai (Receipt) về cho UI
        return new PaymentDTO.TopupResponse(
                tx.getTransactionId(),
                pointsToAdd,
                user.getWalletBalance(),
                "Nạp tiền thành công qua Cổng thanh toán."
        );
    }

    /**
     * Hàm giả lập (Mock) gọi sang Cổng thanh toán VNPay/MoMo
     */
    private boolean callThirdPartyPaymentGateway(BigDecimal amountVnd) {
        // Trong thực tế, bạn sẽ gửi HTTP Request gọi API của VNPay ở đây
        // Giả lập: Nếu khách nạp số tiền kỳ quặc (như 13 VNĐ) thì cho rớt mạng/báo lỗi
        if (amountVnd.compareTo(new BigDecimal("1000")) < 0) {
            System.out.println("Cổng thanh toán: Số tiền quá nhỏ, từ chối giao dịch!");
            return false;
        }

        System.out.println("Cổng thanh toán: Đã nhận thành công " + amountVnd + " VNĐ từ khách hàng.");
        return true; // Giao dịch thành công
    }

    @Transactional
    public PaymentDTO.WithdrawResponse processWithdrawal(Integer userId, BigDecimal pointsToWithdraw) {

        // 1. Xác thực và [checkBalance]
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng."));

        if (pointsToWithdraw.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Lỗi: Số điểm rút phải lớn hơn 0.");
        }

        if (user.getWalletBalance().compareTo(pointsToWithdraw) < 0) {
            throw new RuntimeException("Lỗi: Số dư ví không đủ để thực hiện giao dịch này.");
        }

        // 2. [deductBalance] - Trừ điểm KHÓA SỔ NGAY LẬP TỨC để chống double-spending
        user.setWalletBalance(user.getWalletBalance().subtract(pointsToWithdraw));
        userRepository.save(user);

        // 3. Ghi log Kiểm toán
        WalletTransaction tx = WalletTransaction.builder()
                .user(user)
                .rental(null)
                .transactionType("WITHDRAW") // Trạng thái chuyển khoản
                .amount(pointsToWithdraw.negate())   // Số âm (Trừ ra khỏi hệ thống)
                .build();
        tx = walletTransactionRepository.save(tx);

        // 4. Tính toán tiền mặt dự kiến khách nhận được (x1000)
        BigDecimal expectedVnd = pointsToWithdraw.multiply(new BigDecimal("1000"))
                .setScale(0, RoundingMode.HALF_UP);

        // 5. Trả về thông báo Processing như Sequence Diagram yêu cầu
        return new PaymentDTO.WithdrawResponse(
                tx.getTransactionId(),
                pointsToWithdraw,
                expectedVnd,
                user.getWalletBalance(),
                "Yêu cầu rút tiền đang được xử lý (Processing). Số tiền dự kiến chuyển vào tài khoản ngân hàng của bạn là: " + expectedVnd + " VNĐ."
        );
    }
}