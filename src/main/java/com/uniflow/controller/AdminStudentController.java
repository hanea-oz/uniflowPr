package com.uniflow.controller;

import com.uniflow.model.Student;
import com.uniflow.service.GroupService;
import com.uniflow.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {

    private final StudentService studentService;
    private final GroupService groupService;

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "admin/students/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("groups", groupService.findAll());
        return "admin/students/create";
    }

    @PostMapping("/create")
    public String createStudent(@RequestParam String email,
                                @RequestParam String password,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String program,
                                @RequestParam String level,
                                @RequestParam(required = false) String skills,
                                @RequestParam(required = false) Long groupId,
                                RedirectAttributes redirectAttributes) {
        try {
            studentService.createStudent(email, password, firstName, lastName, program, level, skills, groupId);
            redirectAttributes.addFlashAttribute("successMessage", "Student created successfully");
            return "redirect:/admin/students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/students/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
        model.addAttribute("student", student);
        model.addAttribute("groups", groupService.findAll());
        return "admin/students/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable Long id,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String program,
                                @RequestParam String level,
                                @RequestParam(required = false) String skills,
                                @RequestParam(required = false) Long groupId,
                                RedirectAttributes redirectAttributes) {
        try {
            studentService.updateStudent(id, firstName, lastName, program, level, skills, groupId);
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully");
            return "redirect:/admin/students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/students/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }
}
