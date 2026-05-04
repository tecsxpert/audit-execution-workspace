package com.internship.tool.scheduler;

import com.internship.tool.entity.AuditItem;
import com.internship.tool.repository.AuditItemRepository;
import com.internship.tool.service.EmailService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.scheduler.enabled", havingValue = "true", matchIfMissing = false)
public class AuditScheduler {

    private final AuditItemRepository repository;
    private final EmailService emailService;

    public AuditScheduler(AuditItemRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    // 🔹 1. Overdue Items
    @Scheduled(cron = "0 0 9 * * ?") // every 15 sec (for testing)
    public void checkOverdueItems() {

        List<AuditItem> overdue = repository.findOverdueItems(LocalDateTime.now());

        overdue.forEach(item -> {
            System.out.println("Overdue: " + item.getTitle());

            emailService.sendEmail(
                    "sujantallur@gmail.com",
                    "Overdue Audit Item",
                    "Item overdue: " + item.getTitle()
            );
        });
    }

    // 🔹 2. Upcoming Deadlines (next 7 days)
    @Scheduled(cron = "0 0 9 * * ?") // every 20 sec
    public void upcomingDeadlines() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);

        List<AuditItem> items = repository.findUpcomingItems(now, nextWeek);

        items.forEach(item -> {
            System.out.println("Upcoming: " + item.getTitle());

            emailService.sendEmail(
                    "sujantallur@gmail.com",
                    "Upcoming Deadline",
                    "Due soon: " + item.getTitle()
            );
        });
    }

    // 🔹 3. Weekly Summary
    @Scheduled(cron = "0 0 9 * * ?") // every 30 sec
    public void weeklySummary() {

        long total = repository.count();
        long completed = repository.countByStatus("COMPLETED");

        String summary = "Total: " + total + ", Completed: " + completed;

        System.out.println("Weekly Summary: " + summary);

        emailService.sendEmail(
                "sujantallur@gmail.com",
                "Weekly Audit Summary",
                summary
        );
    }
}