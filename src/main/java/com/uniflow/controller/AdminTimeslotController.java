package com.uniflow.controller;

import com.uniflow.model.Timeslot;
import com.uniflow.service.TimeslotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;

@Controller
@RequestMapping("/admin/timeslots")
@RequiredArgsConstructor
public class AdminTimeslotController {

    private final TimeslotService timeslotService;

    @GetMapping
    public String listTimeslots(Model model) {
        model.addAttribute("timeslots", timeslotService.findAll());
        return "admin/timeslots/list";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "admin/timeslots/create";
    }

    @PostMapping("/create")
    public String createTimeslot(@RequestParam String dayOfWeek,
                                 @RequestParam String startTime,
                                 @RequestParam String endTime,
                                 RedirectAttributes redirectAttributes) {
        try {
            timeslotService.createTimeslot(dayOfWeek, LocalTime.parse(startTime), LocalTime.parse(endTime));
            redirectAttributes.addFlashAttribute("successMessage", "Timeslot created successfully");
            return "redirect:/admin/timeslots";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/timeslots/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Timeslot timeslot = timeslotService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timeslot not found: " + id));
        model.addAttribute("timeslot", timeslot);
        return "admin/timeslots/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateTimeslot(@PathVariable Long id,
                                 @RequestParam String dayOfWeek,
                                 @RequestParam String startTime,
                                 @RequestParam String endTime,
                                 RedirectAttributes redirectAttributes) {
        try {
            timeslotService.updateTimeslot(id, dayOfWeek, LocalTime.parse(startTime), LocalTime.parse(endTime));
            redirectAttributes.addFlashAttribute("successMessage", "Timeslot updated successfully");
            return "redirect:/admin/timeslots";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/timeslots/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteTimeslot(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            timeslotService.deleteTimeslot(id);
            redirectAttributes.addFlashAttribute("successMessage", "Timeslot deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting timeslot: " + e.getMessage());
        }
        return "redirect:/admin/timeslots";
    }
}
