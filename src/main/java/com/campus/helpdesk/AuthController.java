package com.campus.helpdesk;

import com.campus.helpdesk.Models.Student;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final HelpdeskRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(HelpdeskRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String department,
                           @RequestParam String password,
                           Model model, RedirectAttributes ra) {
        name = name.trim();
        email = email.trim().toLowerCase();
        department = department.trim();

        if (name.isEmpty() || email.isEmpty() || password.length() < 6) {
            model.addAttribute("msg", "Fill all fields; password must be at least 6 characters.");
            model.addAttribute("msgType", "danger");
            return "register";
        }
        if (repo.findStudentByEmail(email).isPresent()) {
            model.addAttribute("msg", "Email already registered.");
            model.addAttribute("msgType", "danger");
            return "register";
        }

        repo.createStudent(name, email, encoder.encode(password), department);
        ra.addFlashAttribute("msg", "Registration successful. Please log in.");
        ra.addFlashAttribute("msgType", "success");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletRequest request, Model model) {
        Optional<Student> found = repo.findStudentByEmail(email.trim().toLowerCase());

        if (found.isPresent() && encoder.matches(password, found.get().passwordHash())) {
            HttpSession old = request.getSession(false);
            if (old != null) old.invalidate();
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(30 * 60);
            session.setAttribute("studentId", found.get().studentId());
            session.setAttribute("studentName", found.get().name());
            return "redirect:/dashboard";
        }

        model.addAttribute("msg", "Invalid email or password.");
        model.addAttribute("msgType", "danger");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes ra) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        ra.addFlashAttribute("msg", "Logged out.");
        ra.addFlashAttribute("msgType", "info");
        return "redirect:/login";
    }
}
