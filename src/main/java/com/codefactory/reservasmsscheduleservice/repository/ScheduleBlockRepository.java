package com.codefactory.reservasmsscheduleservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codefactory.reservasmsscheduleservice.entity.ScheduleBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleBlockRepository extends JpaRepository<ScheduleBlock, UUID> {

    List<ScheduleBlock> findByEmployeeId(UUID employeeId);

    List<ScheduleBlock> findByEmployeeIdAndActiveTrue(UUID employeeId);

    List<ScheduleBlock> findByEmployeeIdAndDateAndActiveTrue(UUID employeeId, LocalDate date);

    List<ScheduleBlock> findByEmployeeIdAndDateBetweenAndActiveTrue(UUID employeeId, LocalDate startDate, LocalDate endDate);

    List<ScheduleBlock> findByReservationId(UUID reservationId);

    Optional<ScheduleBlock> findByIdAndEmployeeId(UUID id, UUID employeeId);

    boolean existsByIdAndEmployeeId(UUID id, UUID employeeId);

    @Query("SELECT sb FROM ScheduleBlock sb " +
           "WHERE sb.employee.id = :employeeId " +
           "AND sb.date = :date " +
           "AND sb.active = true " +
           "AND sb.startTime <= :endTime " +
           "AND sb.endTime >= :startTime")
    List<ScheduleBlock> findOverlappingBlocks(
            @Param("employeeId") UUID employeeId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("SELECT sb FROM ScheduleBlock sb " +
           "WHERE sb.employee.id = :employeeId " +
           "AND sb.date >= :startDate " +
           "AND (sb.date <= :endDate OR :endDate IS NULL) " +
           "AND sb.active = true " +
           "ORDER BY sb.date, sb.startTime")
    List<ScheduleBlock> findFutureBlocksByEmployee(
            @Param("employeeId") UUID employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT sb FROM ScheduleBlock sb " +
           "WHERE sb.employee.id = :employeeId " +
           "AND sb.date >= :date " +
           "AND sb.active = true " +
           "AND sb.blockType = :blockType " +
           "ORDER BY sb.date, sb.startTime")
    List<ScheduleBlock> findBlocksByEmployeeAndType(
            @Param("employeeId") UUID employeeId,
            @Param("date") LocalDate date,
            @Param("blockType") String blockType);

    @Query("SELECT sb FROM ScheduleBlock sb " +
           "WHERE sb.reservationId = :reservationId " +
           "AND sb.active = true")
    Optional<ScheduleBlock> findActiveByReservationId(@Param("reservationId") UUID reservationId);
}
