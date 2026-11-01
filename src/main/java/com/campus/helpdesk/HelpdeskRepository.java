package com.campus.helpdesk;

import com.campus.helpdesk.Models.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class HelpdeskRepository {

    private final JdbcTemplate jdbc;

    public HelpdeskRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String COMPLAINT_SELECT =
            "SELECT c.complaint_id, c.student_id, c.category, c.description, c.priority, "
          + "c.status, c.created_at, s.name, s.department "
          + "FROM complaints c JOIN students s ON c.student_id = s.student_id ";

    private static final RowMapper<Complaint> COMPLAINT_MAPPER = (rs, i) -> new Complaint(
            rs.getInt("complaint_id"),
            rs.getInt("student_id"),
            rs.getString("category"),
            rs.getString("description"),
            rs.getString("priority"),
            rs.getString("status"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getString("name"),
            rs.getString("department"));

    // ---------- Students ----------
    public Optional<Student> findStudentByEmail(String email) {
        List<Student> list = jdbc.query(
                "SELECT student_id, name, email, password_hash, department "
              + "FROM students WHERE email = ?",
                (rs, i) -> new Student(rs.getInt(1), rs.getString(2), rs.getString(3),
                                       rs.getString(4), rs.getString(5)),
                email);
        return list.stream().findFirst();
    }

    public void createStudent(String name, String email, String passwordHash, String department) {
        jdbc.update("INSERT INTO students (name, email, password_hash, department) VALUES (?,?,?,?)",
                name, email, passwordHash, department);
    }

    // ---------- Admin ----------
    public Optional<Admin> findAdminByUsername(String username) {
        List<Admin> list = jdbc.query(
                "SELECT admin_id, username, password_hash FROM admin WHERE username = ?",
                (rs, i) -> new Admin(rs.getInt(1), rs.getString(2), rs.getString(3)),
                username);
        return list.stream().findFirst();
    }

    public int countAdmins() {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM admin", Integer.class);
        return n == null ? 0 : n;
    }

    public void createAdmin(String username, String passwordHash) {
        jdbc.update("INSERT INTO admin (username, password_hash) VALUES (?,?)",
                username, passwordHash);
    }

    // ---------- Complaints ----------
    public int createComplaint(int studentId, String category, String description) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO complaints (student_id, category, description) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, studentId);
            ps.setString(2, category);
            ps.setString(3, description);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public List<Complaint> complaintsByStudent(int studentId) {
        return jdbc.query(COMPLAINT_SELECT + "WHERE c.student_id = ? ORDER BY c.created_at DESC",
                COMPLAINT_MAPPER, studentId);
    }

    public List<Complaint> allComplaints() {
        return jdbc.query(COMPLAINT_SELECT + "ORDER BY c.created_at DESC", COMPLAINT_MAPPER);
    }

    public void updateComplaint(int complaintId, String status, String priority) {
        jdbc.update("UPDATE complaints SET status = ?, priority = ? WHERE complaint_id = ?",
                status, priority, complaintId);
    }

    // ---------- Analytics ----------
    public Stats stats() {
        return jdbc.queryForObject(
                "SELECT COUNT(*), "
              + "COALESCE(SUM(status='Pending'),0), "
              + "COALESCE(SUM(status='In Progress'),0), "
              + "COALESCE(SUM(status='Resolved'),0) FROM complaints",
                (rs, i) -> new Stats(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getLong(4)));
    }

    public List<CategoryCount> byCategory() {
        return jdbc.query(
                "SELECT category, COUNT(*) AS cnt FROM complaints GROUP BY category ORDER BY cnt DESC",
                (rs, i) -> new CategoryCount(rs.getString(1), rs.getLong(2)));
    }
}
