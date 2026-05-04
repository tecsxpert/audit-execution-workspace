package com.internship.tool.repository;

import com.internship.tool.entity.AuditItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditItemRepository extends JpaRepository<AuditItem, Long> {

    @Query("SELECT a FROM AuditItem a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<AuditItem> search(@Param("keyword") String keyword, Pageable pageable);

    Page<AuditItem> findByStatus(String status, Pageable pageable);

    Page<AuditItem> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT a FROM AuditItem a WHERE a.status = :status AND a.createdAt BETWEEN :start AND :end")
    Page<AuditItem> findByStatusAndDateRange(
            @Param("status") String status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    Page<AuditItem> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT a FROM AuditItem a WHERE a.dueDate < :now AND a.status != 'COMPLETED'")
    List<AuditItem> findOverdueItems(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM AuditItem a WHERE a.dueDate BETWEEN :start AND :end")
    List<AuditItem> findUpcomingItems(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    long countByStatus(String status);
}
