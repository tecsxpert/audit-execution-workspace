package com.internship.tool.service;

import com.internship.tool.entity.AuditItem;
import com.internship.tool.entity.AuditLog;
import com.internship.tool.repository.AuditItemRepository;
import com.internship.tool.repository.AuditLogRepository;

import jakarta.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditItemService {

    private final AuditItemRepository repository;
    private final AuditLogRepository auditLogRepository;

    public AuditItemService(AuditItemRepository repository, AuditLogRepository auditLogRepository) {
        this.repository = repository;
        this.auditLogRepository = auditLogRepository;
    }

    // 🔍 GET BY ID (CACHE)
    @Cacheable(value = "audit", key = "#id")
    public AuditItem getById(Long id) {
        AuditItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (Boolean.TRUE.equals(item.getIsDeleted())) {
            throw new RuntimeException("Item is deleted");
        }

        return item;
    }

    // 🔍 GET ALL (CACHE)
    @Cacheable("auditList")
    public List<AuditItem> getAll() {
        return repository.findAll()
                .stream()
                .filter(item -> !Boolean.TRUE.equals(item.getIsDeleted()))
                .toList();
    }

    // ➕ CREATE
    @Transactional
    @CacheEvict(value = {"audit", "auditList"}, allEntries = true)
    public AuditItem create(AuditItem item) {

        LocalDateTime now = LocalDateTime.now();

        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        item.setIsDeleted(false);

        AuditItem saved = repository.save(item);

        logChange(saved.getId(), "CREATE", null, saved.getTitle());

        return saved;
    }

    // 🔄 UPDATE
    @Transactional
    @CacheEvict(value = {"audit", "auditList"}, allEntries = true)
    public AuditItem update(Long id, AuditItem updated) {

        AuditItem existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (Boolean.TRUE.equals(existing.getIsDeleted())) {
            throw new RuntimeException("Cannot update deleted item");
        }

        String oldTitle = existing.getTitle();

        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        if (updated.getPriority() != null) existing.setPriority(updated.getPriority());
        if (updated.getScore() != null) existing.setScore(updated.getScore());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        if (updated.getAssignedTo() != null) existing.setAssignedTo(updated.getAssignedTo());

        existing.setUpdatedAt(LocalDateTime.now());

        AuditItem saved = repository.save(existing);

        logChange(id, "UPDATE", oldTitle, saved.getTitle());

        return saved;
    }

    // ❌ SOFT DELETE
    @Transactional
    @CacheEvict(value = {"audit", "auditList"}, allEntries = true)
    public void softDelete(Long id) {

        AuditItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (Boolean.TRUE.equals(item.getIsDeleted())) {
            throw new RuntimeException("Item already deleted");
        }

        item.setIsDeleted(true);
        item.setUpdatedAt(LocalDateTime.now());

        repository.save(item);

        logChange(id, "DELETE", "ACTIVE", "DELETED");
    }

    // 🔥 COMMON AUDIT LOG METHOD
    private void logChange(Long itemId, String action, String oldVal, String newVal) {

        AuditLog log = new AuditLog();
        log.setAuditItemId(itemId);
        log.setAction(action);
        log.setOldValue(oldVal);
        log.setNewValue(newVal);
        log.setChangedBy("system");
        log.setChangedAt(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}