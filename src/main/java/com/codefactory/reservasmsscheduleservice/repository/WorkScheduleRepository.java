package com.codefactory.reservasmsscheduleservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codefactory.reservasmsscheduleservice.entity.WorkSchedule;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, UUID> {

    List<WorkSchedule> findByEmployeeId(UUID employeeId);

    List<WorkSchedule> findByEmployeeIdAndActiveTrue(UUID employeeId);

    List<WorkSchedule> findByEmployeeIdAndDayOfWeekAndActiveTrue(UUID employeeId, String dayOfWeek);

    Optional<WorkSchedule> findByIdAndEmployeeId(UUID id, UUID employeeId);

    boolean existsByIdAndEmployeeId(UUID id, UUID employeeId);

    @Query("SELECT ws FROM WorkSchedule ws " +
           "WHERE ws.employee.id = :employeeId " +
           "AND ws.dayOfWeek = :dayOfWeek " +
           "AND ws.active = true " +
           "AND ws.startTime <= :endTime " +
           "AND ws.endTime >= :startTime")
    List<WorkSchedule> findOverlappingSchedules(
            @Param("employeeId") UUID employeeId,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("SELECT ws FROM WorkSchedule ws " +
           "WHERE ws.employee.id = :employeeId " +
           "AND ws.active = true " +
           "ORDER BY CASE ws.dayOfWeek " +
           "WHEN 'LUNES' THEN 1 " +
           "WHEN 'MARTES' THEN 2 " +
           "WHEN 'MIERCOLES' THEN 3 " +
           "WHEN 'JUEVES' THEN 4 " +
           "WHEN 'VIERNES' THEN 5 " +
           "WHEN 'SABADO' THEN 6 " +
           "WHEN 'DOMINGO' THEN 7 " +
           "END, ws.startTime")
    List<WorkSchedule> findActiveSchedulesByEmployeeOrdered(@Param("employeeId") UUID employeeId);
}
