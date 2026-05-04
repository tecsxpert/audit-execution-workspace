package com.internship.tool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.internship.tool.service.AuditItemService;
import com.internship.tool.entity.AuditItem;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuditIntegrationTest {

    @Autowired
    private AuditItemService service;

    @Test
    void testCreate() {
        AuditItem item = new AuditItem();
        item.setTitle("Test");
        item.setStatus("OPEN");

        AuditItem saved = service.create(item);

        assertNotNull(saved.getId());
    }
}