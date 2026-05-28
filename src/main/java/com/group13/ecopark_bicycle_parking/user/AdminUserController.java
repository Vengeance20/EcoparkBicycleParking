package com.group13.ecopark_bicycle_parking.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiv1/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/managers")
    public ResponseEntity<?> createOrPromoteManager(
            @RequestParam(required = false) Integer adminId,
            @RequestBody UserDTO.ManagerRequest request
    ) {
        try {
            UserDTO.ManagerResponse response = userService.createOrPromoteManager(adminId, request);
            HttpStatus status = "CREATE".equalsIgnoreCase(request.getMode()) ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(response);
        } catch (UserManagementException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
