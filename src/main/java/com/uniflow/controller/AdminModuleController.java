package com.uniflow.controller;

import com.uniflow.model.Module;
import com.uniflow.service.GroupService;
import com.uniflow.service.ModuleService;
import com.uniflow.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/modules")
@RequiredArgsConstructor
public class AdminModuleController {

    private final ModuleService moduleService;
    private final TeacherService teacherService;
    private final GroupService groupService;

    @GetMapping
    public String listModules(Model model) {
        model.addAttribute("modules", moduleService.findAll());
        return "admin/modules/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        model.addAttribute("groups", groupService.findAll());
        return "admin/modules/create";
    }

    @PostMapping("/create")
    public String createModule(@RequestParam String name,
                               @RequestParam String program,
                               @RequestParam Integer semester,
                               @RequestParam Integer volumeHours,
                               @RequestParam(required = false) String requiredSkills,
                               @RequestParam(required = false) Long responsibleTeacherId,
                               @RequestParam(required = false) List<Long> groupIds,
                               RedirectAttributes redirectAttributes) {
        try {
            Module module = moduleService.createModule(name, program, semester, volumeHours, requiredSkills, responsibleTeacherId);
            
            if (groupIds != null && !groupIds.isEmpty()) {
                moduleService.assignGroupsToModule(module.getId(), new HashSet<>(groupIds));
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Module created successfully");
            return "redirect:/admin/modules";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/modules/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Module module = moduleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + id));
        model.addAttribute("module", module);
        model.addAttribute("teachers", teacherService.findAll());
        model.addAttribute("groups", groupService.findAll());
        model.addAttribute("assignedGroupIds", 
            module.getAssignedGroups().stream().map(g -> g.getId()).collect(Collectors.toSet()));
        return "admin/modules/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateModule(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String program,
                               @RequestParam Integer semester,
                               @RequestParam Integer volumeHours,
                               @RequestParam(required = false) String requiredSkills,
                               @RequestParam(required = false) Long responsibleTeacherId,
                               @RequestParam(required = false) List<Long> groupIds,
                               RedirectAttributes redirectAttributes) {
        try {
            moduleService.updateModule(id, name, program, semester, volumeHours, requiredSkills, responsibleTeacherId);
            
            Set<Long> groups = groupIds != null ? new HashSet<>(groupIds) : new HashSet<>();
            moduleService.assignGroupsToModule(id, groups);
            
            redirectAttributes.addFlashAttribute("successMessage", "Module updated successfully");
            return "redirect:/admin/modules";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/modules/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteModule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            moduleService.deleteModule(id);
            redirectAttributes.addFlashAttribute("successMessage", "Module deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting module: " + e.getMessage());
        }
        return "redirect:/admin/modules";
    }
}
