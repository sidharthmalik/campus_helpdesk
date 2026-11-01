package com.campus.helpdesk;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModel {

    @ModelAttribute
    public void addLoggedInUser(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            model.addAttribute("studentName", session.getAttribute("studentName"));
            model.addAttribute("adminName", session.getAttribute("adminName"));
        }
    }
}
