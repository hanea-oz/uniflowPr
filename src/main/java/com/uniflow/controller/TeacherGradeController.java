package com.uniflow.controller;

import com.uniflow.model.Enrollment;
import com.uniflow.model.Grade;
import com.uniflow.model.Module;
import com.uniflow.model.Teacher;
import com.uniflow.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/teacher/grades")
@RequiredArgsConstructor
public class TeacherGradeController {

    private final TeacherService teacherService;
    private final ModuleService moduleService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;

    @GetMapping
    public String selectModule(Authentication authentication, Model model) {
        String email = authentication.getName();
        Teacher teacher = teacherService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));

        model.addAttribute("modules", moduleService.findByTeacherId(teacher.getId()));
        return "teacher/grades/select-module";
    }

    @GetMapping("/module/{moduleId}")
    public String listStudents(@PathVariable Long moduleId, Authentication authentication, Model model) {
        String email = authentication.getName();
        Teacher teacher = teacherService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));

        Module module = moduleService.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found"));

        // Verify teacher owns this module
        if (module.getResponsibleTeacher() == null || !module.getResponsibleTeacher().getId().equals(teacher.getId())) {
            throw new IllegalStateException("You are not authorized to grade this module");
        }

        List<Enrollment> enrollments = enrollmentService.findByModuleId(moduleId);
        
        model.addAttribute("module", module);
        model.addAttribute("enrollments", enrollments);
        return "teacher/grades/list-students";
    }

    @GetMapping("/edit/{enrollmentId}")
    public String showEditGradeForm(@PathVariable Long enrollmentId, Authentication authentication, Model model) {
        String email = authentication.getName();
        Teacher teacher = teacherService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));

        Enrollment enrollment = enrollmentService.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        // Verify teacher owns this module
        Module module = enrollment.getModule();
        if (module.getResponsibleTeacher() == null || !module.getResponsibleTeacher().getId().equals(teacher.getId())) {
            throw new IllegalStateException("You are not authorized to grade this module");
        }

        Grade grade = gradeService.findByEnrollmentId(enrollmentId).orElse(null);
        
        model.addAttribute("enrollment", enrollment);
        model.addAttribute("grade", grade);
        model.addAttribute("teacher", teacher);
        return "teacher/grades/edit";
    }

    @PostMapping("/edit/{enrollmentId}")
    public String updateGrade(@PathVariable Long enrollmentId,
                              @RequestParam(required = false) Double labGrade,
                              @RequestParam(required = false) Double examGrade,
                              @RequestParam(required = false) Double projectGrade,
                              @RequestParam(required = false) Double participationGrade,
                              @RequestParam(required = false) Double finalGrade,
                              @RequestParam(required = false) String feedback,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Teacher teacher = teacherService.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));

            Enrollment enrollment = enrollmentService.findById(enrollmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

            // Verify teacher owns this module
            Module module = enrollment.getModule();
            if (module.getResponsibleTeacher() == null || !module.getResponsibleTeacher().getId().equals(teacher.getId())) {
                throw new IllegalStateException("You are not authorized to grade this module");
            }

            Grade existingGrade = gradeService.findByEnrollmentId(enrollmentId).orElse(null);
            
            if (existingGrade == null) {
                gradeService.createGrade(enrollmentId, teacher.getId(), labGrade, examGrade, 
                        projectGrade, participationGrade, finalGrade, feedback);
            } else {
                gradeService.updateGrade(existingGrade.getId(), labGrade, examGrade, 
                        projectGrade, participationGrade, finalGrade, feedback);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Grade saved successfully");
            return "redirect:/teacher/grades/module/" + module.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/teacher/grades/edit/" + enrollmentId;
        }
    }
}
