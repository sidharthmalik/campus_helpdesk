package com.campus.helpdesk;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentController {

    private final HelpdeskRepository repo;

    public StudentController(HelpdeskRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        int studentId = (int) session.getAttribute("studentId");
        model.addAttribute("complaints", repo.complaintsByStudent(studentId));
        return "student_dashboard";
    }

    @GetMapping("/complaint/new")
    public String complaintForm(Model model) {
        model.addAttribute("categories", Options.CATEGORIES);
        return "submit_complaint";
    }

    @PostMapping("/complaint/new")
    public String submitComplaint(@RequestParam String category,
                                  @RequestParam String description,
                                  HttpSession session, Model model, RedirectAttributes ra) {
        description = description.trim();
        if (!Options.CATEGORIES.contains(category) || description.isEmpty()) {
            model.addAttribute("categories", Options.CATEGORIES);
            model.addAttribute("msg", "Choose a category and enter a description.");
            model.addAttribute("msgType", "danger");
            return "submit_complaint";
        }

        int studentId = (int) session.getAttribute("studentId");
        int id = repo.createComplaint(studentId, category, description);
        ra.addFlashAttribute("msg", String.format("Complaint submitted. Your Complaint ID is CMP-%04d.", id));
        ra.addFlashAttribute("msgType", "success");
        return "redirect:/dashboard";
    }
}
