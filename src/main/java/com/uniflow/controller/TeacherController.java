package com.uniflow.controller;

import com.uniflow.model.Teacher;
import com.uniflow.model.Module;
import com.uniflow.model.Session;
import com.uniflow.model.Group;
import com.uniflow.service.ModuleService;
import com.uniflow.service.SessionService;
import com.uniflow.service.TeacherService;
import com.uniflow.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final ModuleService moduleService;
    private final SessionService sessionService;
    private final GroupService groupService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            Teacher teacher = teacherService.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));

            // Get teacher's modules with null safety
            List<Module> modules = moduleService.findByTeacherId(teacher.getId());
            if (modules == null) {
                modules = List.of();
            }

            // Get teacher's sessions with null safety
            List<Session> sessions = sessionService.findByTeacherId(teacher.getId());
            if (sessions == null) {
                sessions = List.of();
            }

            // Get unique groups from sessions
            Set<Group> uniqueGroups = new HashSet<>();
            for (Session session : sessions) {
                if (session != null && session.getGroup() != null) {
                    uniqueGroups.add(session.getGroup());
                }
            }
            List<Group> groups = uniqueGroups.stream().collect(Collectors.toList());

            model.addAttribute("teacher", teacher);
            model.addAttribute("modules", modules);
            model.addAttribute("sessions", sessions);
            model.addAttribute("groups", groups);
            
            return "teacher/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/modules")
    public String listModules(Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            Teacher teacher = teacherService.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));

            List<Module> modules = moduleService.findByTeacherId(teacher.getId());
            if (modules == null) {
                modules = List.of();
            }

            model.addAttribute("teacher", teacher);
            model.addAttribute("modules", modules);
            return "teacher/modules";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading modules: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/sessions")
    public String listSessions(Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            Teacher teacher = teacherService.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));

            List<Session> sessions = sessionService.findByTeacherId(teacher.getId());
            if (sessions == null) {
                sessions = List.of();
            }

            model.addAttribute("teacher", teacher);
            model.addAttribute("sessions", sessions);
            return "teacher/sessions";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading sessions: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/grades/select")
    public String manageGrades(Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            Teacher teacher = teacherService.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));

            List<Module> modules = moduleService.findByTeacherId(teacher.getId());
            if (modules == null) {
                modules = List.of();
            }

            model.addAttribute("teacher", teacher);
            model.addAttribute("modules", modules);
            return "teacher/grades/select-module";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading grades: " + e.getMessage());
            return "error";
        }
    }
}
