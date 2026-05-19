package com.group13.ecopark_bicycle_parking.station;

import com.group13.ecopark_bicycle_parking.user.User;
import com.group13.ecopark_bicycle_parking.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StationMonitorService {

    private final StationManagerRepository stationManagerRepository;
    private final UserRepository userRepository;

    public StationMonitorService(
            StationManagerRepository stationManagerRepository,
            UserRepository userRepository
    ) {
        this.stationManagerRepository = stationManagerRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<StationMonitorDTO> getMonitorStation(Integer managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        if (manager.getRole() == null || !"MANAGER".equalsIgnoreCase(manager.getRole())) {
            throw new IllegalArgumentException("User is not a manager");
        }

        return stationManagerRepository.findMonitorDataByManager(managerId);
    }
}
