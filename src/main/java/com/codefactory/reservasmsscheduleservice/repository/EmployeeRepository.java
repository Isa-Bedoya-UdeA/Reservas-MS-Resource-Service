package com.codefactory.reservasmsscheduleservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codefactory.reservasmsscheduleservice.entity.Employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    List<Employee> findByProviderId(UUID providerId);

    List<Employee> findByProviderIdAndActiveTrue(UUID providerId);

    List<Employee> findByActiveTrue();

    Optional<Employee> findByIdAndProviderId(UUID id, UUID providerId);

    boolean existsByIdAndProviderId(UUID id, UUID providerId);
}
