package com.group13.ecopark_bicycle_parking.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {

    List<Rental> findByStatusAndReservedAtBefore(String status, LocalDateTime timeLimit);

    Optional<Rental> findByUserUserIdAndBikeBikeCodeAndStatus(Integer userId, String bikeCode, String status);

    List<Rental> findAllByUserUserIdOrderByRentalIdDesc(Integer userId);

    @Query("""
            select new com.group13.ecopark_bicycle_parking.rental.RentalAggregate(
                count(r),
                coalesce(sum(r.totalFee), 0),
                coalesce(sum(r.rentalFee), 0),
                coalesce(sum(r.penaltyFee), 0),
                coalesce(avg(r.totalFee), 0),
                count(distinct r.user.userId)
            )
            from Rental r
            where r.endTime between :startDate and :endDate
              and r.status = 'COMPLETED'
            """)
    RentalAggregate queryRentalStatistics(LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
            select count(r)
            from Rental r
            where r.startTime between :startDate and :endDate
            """)
    Long countRentalRequests(LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
            select new com.group13.ecopark_bicycle_parking.rental.DailyRevenue(
                function('date_format', r.endTime, '%Y-%m-%d'),
                count(r),
                coalesce(sum(r.totalFee), 0),
                coalesce(avg(r.totalFee), 0)
            )
            from Rental r
            where r.endTime between :startDate and :endDate
              and r.status = 'COMPLETED'
            group by function('date_format', r.endTime, '%Y-%m-%d')
            order by function('date_format', r.endTime, '%Y-%m-%d')
            """)
    List<DailyRevenue> queryDailyRevenue(LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
            select new com.group13.ecopark_bicycle_parking.rental.StationRevenue(
                r.startStation.stationId,
                r.startStation.name,
                count(r),
                coalesce(sum(r.totalFee), 0)
            )
            from Rental r
            where r.endTime between :startDate and :endDate
              and r.status = 'COMPLETED'
            group by r.startStation.stationId, r.startStation.name
            order by coalesce(sum(r.totalFee), 0) desc
            """)
    List<StationRevenue> queryTopStations(LocalDateTime startDate, LocalDateTime endDate);
}
