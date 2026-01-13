package com.uniflow.controller;

import com.uniflow.model.Student;
import com.uniflow.service.GradeService;
import com.uniflow.service.ModuleService;
import com.uniflow.service.SessionService;
import com.uniflow.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final SessionService sessionService;
    private final GradeService gradeService;
    private final ModuleService moduleService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));

        model.addAttribute("student", student);
        model.addAttribute("group", student.getGroup());
        
        if (student.getGroup() != null) {
            model.addAttribute("sessions", sessionService.findByGroupId(student.getGroup().getId()));
        }
        
        return "student/dashboard";
    }

    @GetMapping("/sessions")
    public String listSessions(Authentication authentication, Model model) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));

        if (student.getGroup() != null) {
            model.addAttribute("sessions", sessionService.findByGroupId(student.getGroup().getId()));
            model.addAttribute("group", student.getGroup());
        }
        
        return "student/sessions";
    }

    @GetMapping("/grades")
    public String viewGrades(Authentication authentication, Model model) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));

        model.addAttribute("grades", gradeService.findByStudentId(student.getId()));
        model.addAttribute("modules", moduleService.findByStudentId(student.getId()));
        
        return "student/grades";
    }
}
