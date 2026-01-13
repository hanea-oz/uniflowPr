package com.uniflow.controller;

import com.uniflow.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final GroupService groupService;
    private final ModuleService moduleService;
    private final RoomService roomService;
    private final TimeslotService timeslotService;
    private final SessionService sessionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            model.addAttribute("studentCount", studentService.findAll().size());
            model.addAttribute("teacherCount", teacherService.findAll().size());
            model.addAttribute("groupCount", groupService.findAll().size());
            model.addAttribute("moduleCount", moduleService.findAll().size());
            model.addAttribute("sessionCount", sessionService.findAll().size());
        } catch (Exception e) {
            // If there's an error, set counts to 0
            model.addAttribute("studentCount", 0);
            model.addAttribute("teacherCount", 0);
            model.addAttribute("groupCount", 0);
            model.addAttribute("moduleCount", 0);
            model.addAttribute("sessionCount", 0);
        }
        return "admin/dashboard";
    }
}
