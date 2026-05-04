package com.internship.tool.controller;

import com.internship.tool.dto.AuditItemDTO;
import com.internship.tool.entity.AuditItem;
import com.internship.tool.service.AuditItemService;

import jakarta.servlet.http.HttpServletRequest;
import com.internship.tool.dto.AuditItemDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditItemController {

    private final AuditItemService service;

    public AuditItemController(AuditItemService service) {
        this.service = service;
    }

    // ✅ GET ALL
    @GetMapping
    public List<AuditItem> getAll() {
        return service.getAll();
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public AuditItem getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // 🔄 UPDATE
    @PutMapping("/{id}")
    public AuditItem update(@PathVariable Long id, @RequestBody AuditItem item) {
        return service.update(id, item);
    }

    // ❌ DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");

        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access Denied");
        }

        service.softDelete(id);
        return "Deleted successfully";
    }
    @PostMapping
public AuditItem create(@RequestBody AuditItemDTO dto) {

    AuditItem item = new AuditItem();
    item.setTitle(dto.title);
    item.setStatus(dto.status);
    item.setPriority(dto.priority);

    return service.create(item);
}
}