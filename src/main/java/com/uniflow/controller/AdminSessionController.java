package com.uniflow.controller;

import com.uniflow.model.Session;
import com.uniflow.model.enums.SessionTypeEnum;
import com.uniflow.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final SessionService sessionService;
    private final ModuleService moduleService;
    private final TeacherService teacherService;
    private final GroupService groupService;
    private final RoomService roomService;
    private final TimeslotService timeslotService;

    @GetMapping
    public String listSessions(Model model) {
        model.addAttribute("sessions", sessionService.findAll());
        return "admin/sessions/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("sessionTypes", SessionTypeEnum.values());
        model.addAttribute("modules", moduleService.findAll());
        model.addAttribute("teachers", teacherService.findAll());
        model.addAttribute("groups", groupService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("timeslots", timeslotService.findAll());
        return "admin/sessions/create";
    }

    @PostMapping("/create")
    public String createSession(@RequestParam SessionTypeEnum type,
                                @RequestParam Long moduleId,
                                @RequestParam Long teacherId,
                                @RequestParam Long groupId,
                                @RequestParam Long roomId,
                                @RequestParam Long timeslotId,
                                RedirectAttributes redirectAttributes) {
        try {
            sessionService.createSession(type, moduleId, teacherId, groupId, roomId, timeslotId);
            redirectAttributes.addFlashAttribute("successMessage", "Session created successfully");
            return "redirect:/admin/sessions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/sessions/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Session session = sessionService.findByIdWithRelations(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + id));
        model.addAttribute("session", session);
        model.addAttribute("sessionTypes", SessionTypeEnum.values());
        model.addAttribute("modules", moduleService.findAll());
        model.addAttribute("teachers", teacherService.findAll());
        model.addAttribute("groups", groupService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("timeslots", timeslotService.findAll());
        return "admin/sessions/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateSession(@PathVariable Long id,
                                @RequestParam SessionTypeEnum type,
                                @RequestParam Long moduleId,
                                @RequestParam Long teacherId,
                                @RequestParam Long groupId,
                                @RequestParam Long roomId,
                                @RequestParam Long timeslotId,
                                RedirectAttributes redirectAttributes) {
        try {
            sessionService.updateSession(id, type, moduleId, teacherId, groupId, roomId, timeslotId);
            redirectAttributes.addFlashAttribute("successMessage", "Session updated successfully");
            return "redirect:/admin/sessions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/sessions/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteSession(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sessionService.deleteSession(id);
            redirectAttributes.addFlashAttribute("successMessage", "Session deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting session: " + e.getMessage());
        }
        return "redirect:/admin/sessions";
    }
}
