package com.campus.helpdesk;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        boolean isAdminArea = request.getRequestURI().startsWith("/admin");
        String key = isAdminArea ? "adminId" : "studentId";

        if (session == null || session.getAttribute(key) == null) {
            response.sendRedirect(isAdminArea ? "/admin/login?required" : "/login?required");
            return false;
        }
        return true;
    }
}
