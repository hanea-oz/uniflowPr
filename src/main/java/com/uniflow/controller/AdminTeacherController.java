package com.uniflow.controller;

import com.uniflow.model.Teacher;
import com.uniflow.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
public class AdminTeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        return "admin/teachers/list";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "admin/teachers/create";
    }

    @PostMapping("/create")
    public String createTeacher(@RequestParam String email,
                                @RequestParam String password,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam(required = false) String grade,
                                @RequestParam(required = false) String specialty,
                                @RequestParam(required = false) String skills,
                                @RequestParam(required = false, defaultValue = "0") Integer workloadHours,
                                RedirectAttributes redirectAttributes) {
        try {
            teacherService.createTeacher(email, password, firstName, lastName, grade, specialty, skills, workloadHours);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher created successfully");
            return "redirect:/admin/teachers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/teachers/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + id));
        model.addAttribute("teacher", teacher);
        return "admin/teachers/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateTeacher(@PathVariable Long id,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam(required = false) String grade,
                                @RequestParam(required = false) String specialty,
                                @RequestParam(required = false) String skills,
                                @RequestParam(required = false, defaultValue = "0") Integer workloadHours,
                                RedirectAttributes redirectAttributes) {
        try {
            teacherService.updateTeacher(id, firstName, lastName, grade, specialty, skills, workloadHours);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher updated successfully");
            return "redirect:/admin/teachers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/teachers/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            teacherService.deleteTeacher(id);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting teacher: " + e.getMessage());
        }
        return "redirect:/admin/teachers";
    }
}
