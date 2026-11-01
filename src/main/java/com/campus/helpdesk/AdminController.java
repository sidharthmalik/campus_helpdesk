package com.campus.helpdesk;

import com.campus.helpdesk.Models.Admin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AdminController {

    private final HelpdeskRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminController(HelpdeskRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/admin/login")
    public String loginForm() {
        return "admin_login";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request, Model model) {
        Optional<Admin> found = repo.findAdminByUsername(username.trim());

        if (found.isPresent() && encoder.matches(password, found.get().passwordHash())) {
            HttpSession old = request.getSession(false);
            if (old != null) old.invalidate();
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(30 * 60);
            session.setAttribute("adminId", found.get().adminId());
            session.setAttribute("adminName", found.get().username());
            return "redirect:/admin";
        }

        model.addAttribute("msg", "Invalid admin credentials.");
        model.addAttribute("msgType", "danger");
        return "admin_login";
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("complaints", repo.allComplaints());
        model.addAttribute("stats", repo.stats());
        model.addAttribute("byCategory", repo.byCategory());
        model.addAttribute("statuses", Options.STATUSES);
        model.addAttribute("priorities", Options.PRIORITIES);
        return "admin_dashboard";
    }

    @PostMapping("/admin/update/{id}")
    public String update(@PathVariable int id,
                         @RequestParam String status,
                         @RequestParam String priority,
                         RedirectAttributes ra) {
        if (Options.STATUSES.contains(status) && Options.PRIORITIES.contains(priority)) {
            repo.updateComplaint(id, status, priority);
            ra.addFlashAttribute("msg", String.format("Complaint CMP-%04d updated.", id));
            ra.addFlashAttribute("msgType", "success");
        }
        return "redirect:/admin";
    }
}
