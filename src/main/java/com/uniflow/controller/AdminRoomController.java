package com.uniflow.controller;

import com.uniflow.model.Room;
import com.uniflow.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class AdminRoomController {

    private final RoomService roomService;

    @GetMapping
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.findAll());
        return "admin/rooms/list";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "admin/rooms/create";
    }

    @PostMapping("/create")
    public String createRoom(@RequestParam String name,
                             @RequestParam Integer capacity,
                             @RequestParam(required = false) String type,
                             RedirectAttributes redirectAttributes) {
        try {
            roomService.createRoom(name, capacity, type);
            redirectAttributes.addFlashAttribute("successMessage", "Room created successfully");
            return "redirect:/admin/rooms";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/rooms/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Room room = roomService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + id));
        model.addAttribute("room", room);
        return "admin/rooms/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateRoom(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam Integer capacity,
                             RedirectAttributes redirectAttributes) {
        try {
            roomService.updateRoom(id, name, capacity);
            redirectAttributes.addFlashAttribute("successMessage", "Room updated successfully");
            return "redirect:/admin/rooms";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/rooms/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("successMessage", "Room deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting room: " + e.getMessage());
        }
        return "redirect:/admin/rooms";
    }
}
