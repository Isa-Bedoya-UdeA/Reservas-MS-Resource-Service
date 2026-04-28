package com.codefactory.reservasmsresourceservice.repository;

import com.codefactory.reservasmsresourceservice.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {

    List<Availability> findByEmployeeId(UUID employeeId);

    List<Availability> findByEmployeeIdAndActiveTrue(UUID employeeId);

    List<Availability> findByEmployeeIdAndDayOfWeekAndActiveTrue(UUID employeeId, String dayOfWeek);

    Optional<Availability> findByIdAndEmployeeId(UUID id, UUID employeeId);

    boolean existsByIdAndEmployeeId(UUID id, UUID employeeId);
}
