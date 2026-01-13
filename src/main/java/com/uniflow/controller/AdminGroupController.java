package com.uniflow.controller;

import com.uniflow.model.Group;
import com.uniflow.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/groups")
@RequiredArgsConstructor
public class AdminGroupController {

    private final GroupService groupService;

    @GetMapping
    public String listGroups(Model model) {
        model.addAttribute("groups", groupService.findAll());
        return "admin/groups/list";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "admin/groups/create";
    }

    @PostMapping("/create")
    public String createGroup(@RequestParam String name,
                              @RequestParam String program,
                              @RequestParam String level,
                              RedirectAttributes redirectAttributes) {
        try {
            groupService.createGroup(name, program, level);
            redirectAttributes.addFlashAttribute("successMessage", "Group created successfully");
            return "redirect:/admin/groups";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/groups/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Group group = groupService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + id));
        model.addAttribute("group", group);
        return "admin/groups/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateGroup(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String program,
                              @RequestParam String level,
                              RedirectAttributes redirectAttributes) {
        try {
            groupService.updateGroup(id, name, program, level);
            redirectAttributes.addFlashAttribute("successMessage", "Group updated successfully");
            return "redirect:/admin/groups";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/groups/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteGroup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            groupService.deleteGroup(id);
            redirectAttributes.addFlashAttribute("successMessage", "Group deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting group: " + e.getMessage());
        }
        return "redirect:/admin/groups";
    }
}
