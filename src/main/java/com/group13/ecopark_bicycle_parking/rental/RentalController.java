package com.group13.ecopark_bicycle_parking.rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiv1/rentals")
@CrossOrigin(origins = "*")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    // 🔴 API MỚI: Frontend gọi khi vừa mở trang, truyền userId lên để xem đang thuê xe gì
    @GetMapping("/my-active-rental")
    public ResponseEntity<?> getMyActiveRental(@RequestParam Integer userId) {
        try {
            return ResponseEntity.ok(rentalService.getMyActiveRental(userId));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NO_ACTIVE_RENTAL")) return ResponseEntity.ok(null);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/rent")
    public ResponseEntity<?> rentBike(@RequestBody RentalDTO.RentRequest request) {
        try {
            return ResponseEntity.ok(rentalService.rentBike(request));
        } catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveBike(@RequestBody RentalDTO.RentRequest request) {
        try {
            return ResponseEntity.ok(rentalService.reserveBike(request.getUserId(), request.getBikeCode()));
        } catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlockBike(@RequestBody RentalDTO.RentRequest request) {
        try {
            return ResponseEntity.ok(rentalService.unlockBike(request.getUserId(), request.getBikeCode()));
        } catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnBike(@RequestBody RentalDTO.ReturnRequest request) {
        try { return ResponseEntity.ok(rentalService.returnBike(request)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @GetMapping("/history/{userKey}")
    public ResponseEntity<?> getRentalHistory(@PathVariable String userKey) {
        try { return ResponseEntity.ok(rentalService.getRentalHistory(userKey)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }
}