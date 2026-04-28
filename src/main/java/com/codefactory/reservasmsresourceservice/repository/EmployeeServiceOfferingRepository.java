package com.codefactory.reservasmsresourceservice.repository;

import com.codefactory.reservasmsresourceservice.entity.EmployeeServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeServiceOfferingRepository extends JpaRepository<EmployeeServiceOffering, UUID> {

    List<EmployeeServiceOffering> findByEmployeeId(UUID employeeId);

    List<EmployeeServiceOffering> findByEmployeeIdAndActiveTrue(UUID employeeId);

    Optional<EmployeeServiceOffering> findByEmployeeIdAndServiceId(UUID employeeId, UUID serviceId);

    boolean existsByEmployeeIdAndServiceId(UUID employeeId, UUID serviceId);
}
