package com.campus.helpdesk;

import java.time.LocalDateTime;

public final class Models {
    private Models() {}

    public record Student(int studentId, String name, String email,
                          String passwordHash, String department) {}

    public record Admin(int adminId, String username, String passwordHash) {}

    public record Complaint(int complaintId, int studentId, String category,
                            String description, String priority, String status,
                            LocalDateTime createdAt, String studentName,
                            String department) {
        public String displayId() {
            return String.format("CMP-%04d", complaintId);
        }
    }

    public record Stats(long total, long pending, long inProgress, long resolved) {}

    public record CategoryCount(String category, long count) {}
}
